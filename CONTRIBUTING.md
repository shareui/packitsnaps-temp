# Contributing Guidelines for PackIt

## Logging with `logx`

PackIt uses a unified logging system across both Python and Kotlin codebases. The `[packit]` prefix is attached automatically by the logger — do not add it manually in message strings.

### Severity Rules

- All errors and exceptions are ALWAYS non-debug (isDebug = false).
- Everything that is not an error (lifecycle, tracing, state changes) is ALWAYS debug (isDebug = true).

### Exception Handling Rules

- **NEVER leave empty catch blocks or swallow exceptions silently** (e.g. `catch (_: Throwable) {}` or `except Exception: pass`).
- Every caught exception must at minimum be logged with `isDebug = false`.

---

### Usage in Python

Import `logx` from `packutil`:

```python
from packutil import logx

# non-error
logx("plugin loaded successfully", isDebug=True)
logx("CoreState marked ready", isDebug=True)

# errors and exceptions
try: initDangerousOperation()
except Exception as e: logx(f"operation failed: {e}", isDebug=False)
```

---

### Usage in Kotlin

Import `Logx` from `sh.packit.core.utils`:

```kotlin
import sh.packit.core.utils.Logx

// non-error events
Logx.logx("ComposeEntry.createView started", isDebug = true)
Logx.logx("Sticker loaded successfully", isDebug = true)

// errors and exceptions
try { performAction()
} catch (e: Throwable) { Logx.logx("performAction failed: $e", isDebug = false) }
```

---

### Settings & Output

- `debug_logs`: Controls whether `isDebug = true` messages are emitted
- `write_logs`: Controls whether messages are written to `<filesDir>/packit/var/logs/latest.txt` (symlinked to active session `<filesDir>/packit/var/logs/history/{YY:MM:DD}-{HH:MM:SS}.txt`)

---

## Accessing `sh.packit..` Classes Across Runtimes and Dexes

PackIt is split across multiple components:
- `Core.dex`: houses core state, bridges, sticker loaders, and utilities (`sh.packit.core.*`).
- `Compose.dex`: houses Jetpack Compose UI screens, activities, and components (`sh.packit.compose.*`).
- Python layer: loads dex files and orchestrates plugin lifecycle.

### 1. From Python

Use `loadCoreClass` and `callStatic` from `core.DexLoader`:

```python
from .core.DexLoader import loadCoreClass, callStatic

# Load class by qualified name
coreCls = loadCoreClass("sh.packit.core.state.CoreState")
if coreCls is not None:
    # callStatic handles both @JvmStatic methods and Kotlin `object` singletons (via INSTANCE)
    callStatic(coreCls, "markCoreReady")

# Example: show restart bulletin
restartCls = loadCoreClass("sh.packit.core.ui.RestartRequired")
if restartCls is not None:
    callStatic(restartCls, "show", "Client restart required", "Restart")
```

---

### 2. In Kotlin from Another Dex

#### Safety Rules & Android ART Pitfalls

- **Direct static imports are NOT automatically safe across dex files.**
- If you directly import and call a class that has not been loaded into the ClassLoader hierarchy yet, Android ART will fail bytecode verification and throw **`java.lang.NoClassDefFoundError`**.
- Note: `NoClassDefFoundError` is a subclass of `java.lang.Error`, **NOT `java.lang.Exception`**. If you catch only `Exception`, the app **will crash**!
- Always catch `Throwable` when dealing with cross-dex boundary calls.

#### When Direct Imports ARE Safe

Direct imports are safe **only when calling from child to parent in a chained ClassLoader hierarchy where the parent is already initialized**:
- Example: `Compose.dex` is loaded with `parent_loader = coreLoader`. Since `Core.dex` is already loaded before `ComposeFragment` is opened, classes in `Compose.dex` can directly import `sh.packit.core.*`:

```kotlin
import sh.packit.core.state.CoreState

// Safe: Compose.dex -> Core.dex (child delegates to already loaded parent)
CoreState.markComposeReady()
val pluginId = CoreState.PLUGIN_ID
```

#### When Direct Imports are DANGEROUS

- **Calling from `Core.dex` into `Compose.dex`**: `Core.dex` loads on app startup. `Compose.dex` is only loaded later when settings are opened. If `Core.dex` had a direct import like `import sh.packit.compose.ComposeEntry`, it would immediately crash on plugin startup with `NoClassDefFoundError`!
- **Optional/Conditional Plugins**: If a plugin or dex might not be present (e.g. `ComposeShell` not installed).

#### 3 Safe Patterns for Cross-Dex Communication

##### Pattern 1: Interface / Service Provider Registry (Recommended for high performance)
Define a common interface in the parent dex (`Core.dex`), and let the child dex register its implementation when it loads:

```kotlin
// In Core.dex (Parent):
interface ComposeBridge {
    fun openSettings(context: Context)
}

object CoreState {
    var composeBridge: ComposeBridge? = null
}

// In Compose.dex (Child):
class ComposeBridgeImpl : ComposeBridge {
    override fun openSettings(context: Context) { ... }
}

// When Compose.dex loads (e.g. ComposeEntry):
CoreState.composeBridge = ComposeBridgeImpl()

// In Core.dex (caller):
// 100% safe, zero reflection, zero risk of NoClassDefFoundError:
CoreState.composeBridge?.openSettings(context)
```

##### Pattern 2: Isolated Bridge Class (Lazy Class Verification)
Android ART verifies and resolves classes lazily upon executing the method that references them. If you isolate direct imports in a separate class, that class won't be loaded until invoked:

```kotlin
// Isolated helper in a separate file:
object ComposeLazyBridge {
    fun run() {
        // Direct import of ComposeEntry is only resolved when run() is called
        ComposeEntry.init()
    }
}

// Caller checks readiness first:
if (CoreState.isComposeReady()) {
    try {
        ComposeLazyBridge.run()
    } catch (e: Throwable) { // MUST catch Throwable, not Exception!
        Logx.logx("Failed to invoke ComposeLazyBridge: $e", isDebug = false)
    }
}
```

##### Pattern 3: Dynamic Class Loading (Across Independent ClassLoaders)
If two dexes are not in a parent-child relationship, load classes dynamically via `ClassLoader`:

```kotlin
try {
    // 1. Resolve class using target ClassLoader
    val classLoader: ClassLoader = targetClassLoader
    val clazz: Class<*> = Class.forName("sh.packit.core.ui.RestartRequired", true, classLoader)

    // 2. Call @JvmStatic or static method:
    val method = clazz.getMethod("show")
    method.invoke(null)

    // 3. Or access Kotlin `object` singleton via INSTANCE field:
    val instanceField = clazz.getField("INSTANCE")
    val singleton = instanceField.get(null)
} catch (e: Throwable) { // Catch Throwable to handle ClassNotFoundException and LinkageError
    Logx.logx("Dynamic cross-dex call failed: $e", isDebug = false)
}
```

---

## 3. Invoking Python (Chaquopy) from Kotlin

When invoking Python plugin code from Kotlin via Chaquopy reflection:

### 1. Module Namespace in ElyxCore
- Python plugin modules do **NOT** reside at the top level of `sys.modules`.
- They are imported under the package `ElyxPlugins.<plugin_id>.*`.
- **Wrong**: `py.getModule("ui.activities.PluginSettings")` (fails with `ModuleNotFoundError`).
- **Correct**: `py.getModule("ElyxPlugins." + CoreState.PLUGIN_ID + ".ui.activities.PluginSettings")` (e.g. `ElyxPlugins.packit.ui.activities.PluginSettings`).

### 2. Method Reflection on `PyObject.callAttr`
- `PyObject.callAttr(String name, Object... args)` has Java parameter types `(String, Object[])`.
- When invoking `callAttr` through reflection via `Method.invoke(Object obj, Object... args)`, passing an `Array<Any?>` as the second argument can unpack the array elements into `Method.invoke` itself, causing `IllegalArgumentException: wrong number of arguments`.
- **Correct pattern in Kotlin**:
```kotlin
val pyObjClass = Class.forName("com.chaquo.python.PyObject")
val callAttr = pyObjClass.getMethod("callAttr", String::class.java, Array<Any?>::class.java)
val args = arrayOf<Any?>(param1, param2)
val result = callAttr.invoke(module, *arrayOf<Any?>("functionName", args))
```

### 3. Return Types from `callAttr`
- `PyObject.callAttr` returns a `PyObject` instance, not a raw Java `Boolean` or primitive.
- A raw cast like `result as? Boolean` evaluates to `null`.
- Check truthiness via `result?.toString() == "True" || result as? Boolean == true` or call `pyObjClass.getMethod("toBoolean").invoke(result)`.

---

## 4. File Paths and Path Helper Functions

- **NEVER hardcode filesystem paths** (e.g. `/data/user/0/...`, `/data/data/...`, or manual string concatenations like `filesDir + "/packit/var/logs"`).
- **Always use dedicated path helper functions** instead of building or hardcoding paths manually:
  - **In Python**: import from `packutil.paths` (e.g. `getDexPath()`, `getCoreDexPath()`, `getComposeDexPath()`, `getPackitDir()`, `getPackitVarDir()`, `getLogsDir()`, `getHistoryDir()`, `getLatestLogPath()`, `getAssetsDir()`, `getConfigsDir()`, `getReposCacheDir()`, `getTempDir()`).
  - **In Kotlin**: use `sh.packit.core.utils.Paths` (e.g. `Paths.getFontsDir()`, `Paths.getAssetsDir()`, `Paths.getLogsDir()`, `Paths.getHistoryDir()`, `Paths.getLatestLogFile()`, `Paths.getPluginSettingsFile()`, `Paths.getPackitDir()`, `Paths.getPackitVarDir()`).
  - For base cruel plugin directories (`assets`, `source`, `strings`, `wheels`), use the cruel SDK functions or their wrappers:
    - **In Python**: `packutil.paths` or `cruel` (`get_assets_dir()`, `get_source_dir()`, etc.).
    - **In Kotlin**: `sh.packit.core.utils.Paths` or `de.shareui.exterasdk.cruel.PluginPaths`.

---

## 5. Compose Icons Guidelines

All Jetpack Compose icons in PackIt should primarily be vector icons obtained from [Google Fonts Material Symbols Outlined](https://fonts.google.com/icons?icon.size=24&icon.color=%23e3e3e3&icon.platform=android&icon.set=Material+Symbols&icon.style=Outlined), or from other platforms/sources as long as they seamlessly match and do not deviate from the Material Symbols Outlined visual style.

### Organization and Structure

- **Directory location**: `PackIt/src/kotlin/sh/packit/compose/icons/`
- **Subdirectories by size (dp)**: Sort icons into subdirectories named after their optical size:
  - `size20dp/`
  - `size24dp/`
  - `size28dp/`
- **File naming**: Icon file names must always use **`PascalCase`** (e.g. `Titlecase.kt`, `Colors.kt`, `ContractEdit.kt`, `History.kt`).
- **Package name**: Must match the directory: `package sh.packit.compose.icons.size<N>dp` (e.g. `package sh.packit.compose.icons.size24dp`).
- **Property naming**: Provide the base property name matching Google Fonts naming and an alias in `camelCase` (e.g. `public val contractEdit: ImageVector get() = contract_edit`).

### Downloading via curl

To download an icon from Google Fonts and adjust the package automatically:

```bash
# Example for 24dp icon:
curl -s --compressed "https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/<icon_name>.kt?var=opsz,wght,FILL,GRAD,ROND@24,400,0,0,50" \
  | sed 's/package com.example.test/package sh.packit.compose.icons.size24dp/' \
  > PackIt/src/kotlin/sh/packit/compose/icons/size24dp/<IconName>.kt
```

For other sizes (e.g. 28dp), adjust both the URL path (`/28dp/`) and opsz parameter (`@28,400,0,0,50`), as well as the destination package and folder.



