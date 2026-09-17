package sh.packit.compose.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import sh.packit.core.utils.Logx

object ScrollHelper {
    private val screenStack: MutableList<String> = mutableListOf()
    private val savedScrolls: MutableMap<String, Int> = mutableMapOf()
    private val currentScrolls: MutableMap<String, Int> = mutableMapOf()
    private val navigatingForwardScreens: MutableSet<String> = mutableSetOf()

    fun onNavigateForward(screenKey: String) {
        val current: Int = currentScrolls[screenKey] ?: 0
        savedScrolls[screenKey] = current
        navigatingForwardScreens.add(screenKey)
        Logx.logx("ScrollHelper: navigating forward from $screenKey (scroll=$current)", isDebug = true)
    }

    fun onScreenAttached(screenKey: String) {
        if (screenStack.contains(screenKey)) {
            while (screenStack.isNotEmpty() && screenStack.last() != screenKey) {
                val popped: String = screenStack.removeAt(screenStack.lastIndex)
                savedScrolls.remove(popped)
                currentScrolls.remove(popped)
                navigatingForwardScreens.remove(popped)
            }
        } else {
            val prevScreen: String? = screenStack.lastOrNull()
            if (prevScreen != null) {
                val prevScroll: Int = currentScrolls[prevScreen] ?: 0
                savedScrolls[prevScreen] = prevScroll
            }
            screenStack.add(screenKey)
        }
        navigatingForwardScreens.remove(screenKey)
        Logx.logx("ScrollHelper: screen attached $screenKey, stack=$screenStack", isDebug = true)
    }

    fun onScreenDisposed(screenKey: String) {
        if (navigatingForwardScreens.contains(screenKey)) {
            Logx.logx("ScrollHelper: screen $screenKey disposed while navigating forward, preserving scroll", isDebug = true)
            return
        }
        if (screenStack.lastOrNull() == screenKey) {
            screenStack.removeAt(screenStack.lastIndex)
            savedScrolls.remove(screenKey)
            currentScrolls.remove(screenKey)
            Logx.logx("ScrollHelper: screen $screenKey exited, cleared scroll", isDebug = true)
        }
    }

    fun updateCurrentScroll(screenKey: String, value: Int) {
        currentScrolls[screenKey] = value
    }

    fun getSavedScroll(screenKey: String): Int {
        return savedScrolls[screenKey] ?: 0
    }
}

@Composable
fun rememberScreenScrollState(screenKey: String): ScrollState {
    val initialScroll: Int = remember(screenKey) {
        ScrollHelper.getSavedScroll(screenKey)
    }
    val scrollState: ScrollState = rememberScrollState(initial = initialScroll)

    DisposableEffect(screenKey) {
        ScrollHelper.onScreenAttached(screenKey)
        onDispose {
            ScrollHelper.onScreenDisposed(screenKey)
        }
    }

    LaunchedEffect(scrollState.value) {
        ScrollHelper.updateCurrentScroll(screenKey, scrollState.value)
    }

    LaunchedEffect(screenKey) {
        val saved: Int = ScrollHelper.getSavedScroll(screenKey)
        if (saved > 0 && scrollState.value != saved) {
            scrollState.scrollTo(saved)
        }
    }

    return scrollState
}
