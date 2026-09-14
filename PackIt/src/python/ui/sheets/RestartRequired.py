# pyright: reportMissingImports=false
import threading
import time
from packutil import logx
from android_utils import run_on_ui_thread, OnClickListener
from client_utils import get_last_fragment

def restartApp():
    try:
        from org.telegram.messenger import ApplicationLoader
        from android.content import Intent
        from android.os import Process

        context = ApplicationLoader.applicationContext
        if context is None:
            logx("restartApp: applicationContext is None", isDebug=False)
            return

        packageManager = context.getPackageManager()
        intent = packageManager.getLaunchIntentForPackage(context.getPackageName())
        if intent is not None and intent.getComponent() is not None:
            restartIntent = Intent.makeRestartActivityTask(intent.getComponent())
            restartIntent.setPackage(context.getPackageName())
            context.startActivity(restartIntent)
        else:
            logx("restartApp: component name is None", isDebug=False)

        def killTask():
            try:
                time.sleep(0.3)
                Process.killProcess(Process.myPid())
            except Exception as e:
                logx(f"killTask error: {e}", isDebug=False)

        threading.Thread(target=killTask, daemon=True).start()
    except Exception as e:
        logx(f"restartApp error: {e}", isDebug=False)

def buildDragHandle(activity):
    from android.view import View
    from android.graphics.drawable import GradientDrawable
    from org.telegram.messenger import AndroidUtilities
    from org.telegram.ui.ActionBar import Theme

    handle = View(activity)
    bg = GradientDrawable()
    bg.setShape(GradientDrawable.RECTANGLE)
    bg.setCornerRadius(AndroidUtilities.dp(2))
    try:
        handleColor = Theme.getColor(Theme.key_sheet_scrollUp)
    except Exception:
        handleColor = 0x33888888
    bg.setColor(handleColor)
    handle.setBackground(bg)
    return handle

def buildHeroIcon(activity, sheet):
    from android.widget import FrameLayout
    from android.view import Gravity
    from android.graphics.drawable import GradientDrawable
    from org.telegram.messenger import AndroidUtilities
    from org.telegram.ui.ActionBar import Theme
    from org.telegram.ui.Components import RLottieImageView

    iconBox = FrameLayout(activity)
    boxBg = GradientDrawable()
    boxBg.setShape(GradientDrawable.RECTANGLE)
    boxBg.setCornerRadius(AndroidUtilities.dp(22))

    try:
        accent = sheet.getThemedColor(Theme.key_featuredStickers_addButton)
    except Exception:
        accent = 0xFF2196F3

    r = (accent >> 16) & 0xFF
    g = (accent >> 8) & 0xFF
    b = accent & 0xFF
    fillColor = (0x24 << 24) | (r << 16) | (g << 8) | b
    boxBg.setColor(fillColor)
    iconBox.setBackground(boxBg)

    iconView = RLottieImageView(activity)
    try:
        from hook_utils import find_class
        rRawCls = find_class("org.telegram.messenger.R$raw")
        infoId = getattr(rRawCls, "info", 0)
        if infoId:
            iconView.setAnimation(infoId, 44, 44)
            iconView.playAnimation()
    except Exception as e:
        logx(f"buildHeroIcon lottie error: {e}", isDebug=False)

    iconLp = FrameLayout.LayoutParams(AndroidUtilities.dp(44), AndroidUtilities.dp(44), Gravity.CENTER)
    iconBox.addView(iconView, iconLp)
    return iconBox

def buildInfoCard(activity, sheet):
    from android.widget import LinearLayout, TextView
    from android.view import Gravity
    from android.util import TypedValue
    from android.graphics.drawable import GradientDrawable
    from org.telegram.messenger import AndroidUtilities
    from org.telegram.ui.ActionBar import Theme
    from org.telegram.ui.Components import LayoutHelper

    card = LinearLayout(activity)
    card.setOrientation(LinearLayout.VERTICAL)
    card.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12))

    cardBg = GradientDrawable()
    cardBg.setShape(GradientDrawable.RECTANGLE)
    cardBg.setCornerRadius(AndroidUtilities.dp(16))
    try:
        cardBg.setColor(sheet.getThemedColor(Theme.key_windowBackgroundGray))
    except Exception:
        cardBg.setColor(0x15888888)
    try:
        cardBg.setStroke(AndroidUtilities.dp(1), sheet.getThemedColor(Theme.key_divider))
    except Exception as e:
        logx(f"card stroke notice: {e}", isDebug=False)
    card.setBackground(cardBg)

    itemTitle = TextView(activity)
    itemTitle.setText("Core classes & state updated")
    itemTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14)
    itemTitle.setTypeface(AndroidUtilities.bold())
    itemTitle.setTextColor(sheet.getThemedColor(Theme.key_windowBackgroundWhiteBlackText))
    card.addView(itemTitle, LayoutHelper.createLinear(-1, -2))

    row2 = TextView(activity)
    row2.setText("Client reload required to apply bytecode changes")
    row2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13)
    row2.setTextColor(sheet.getThemedColor(Theme.key_windowBackgroundWhiteGrayText))
    card.addView(row2, LayoutHelper.createLinear(-1, -2, 0, 4, 0, 0))
    return card

def showRestartBottomSheet():
    def showTask():
        try:
            frag = get_last_fragment()
            if frag is None:
                logx("showRestartBottomSheet: get_last_fragment is None", isDebug=False)
                return
            activity = frag.getParentActivity()
            if activity is None:
                logx("showRestartBottomSheet: getParentActivity is None", isDebug=False)
                return

            resourceProvider = frag.getResourceProvider()
            from org.telegram.ui.ActionBar import BottomSheet, Theme
            from org.telegram.messenger import AndroidUtilities
            from org.telegram.ui.Components import LayoutHelper
            from org.telegram.ui.Stories.recorder import ButtonWithCounterView
            from android.widget import LinearLayout, TextView
            from android.view import Gravity
            from android.util import TypedValue

            sheet = BottomSheet(activity, False, resourceProvider)
            sheet.fixNavigationBar()
            sheet.setCanceledOnTouchOutside(True)

            content = LinearLayout(activity)
            content.setOrientation(LinearLayout.VERTICAL)
            content.setGravity(Gravity.CENTER_HORIZONTAL)
            content.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(12), AndroidUtilities.dp(20), AndroidUtilities.dp(20))

            handle = buildDragHandle(activity)
            content.addView(handle, LayoutHelper.createLinear(36, 4, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 16))

            iconBox = buildHeroIcon(activity, sheet)
            content.addView(iconBox, LayoutHelper.createLinear(72, 72, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 14))

            titleText = "Restart required"
            descText = "You need to restart the client for PackIt to update the Kotlin components."
            buttonText = "Restart"
            try:
                from elyx import strings
                titleText = str(getattr(strings, "restart_sheet_title", titleText))
                descText = str(getattr(strings, "restart_sheet_description", descText))
                buttonText = str(getattr(strings, "restart_button", buttonText))
            except Exception as e:
                logx(f"strings lookup notice: {e}", isDebug=False)

            titleTv = TextView(activity)
            titleTv.setText(titleText)
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20)
            titleTv.setTypeface(AndroidUtilities.bold())
            titleTv.setGravity(Gravity.CENTER_HORIZONTAL)
            titleTv.setTextColor(sheet.getThemedColor(Theme.key_windowBackgroundWhiteBlackText))
            content.addView(titleTv, LayoutHelper.createLinear(-1, -2, 0, 0, 0, 8))

            descTv = TextView(activity)
            descTv.setText(descText)
            descTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14)
            descTv.setGravity(Gravity.CENTER_HORIZONTAL)
            descTv.setTextColor(sheet.getThemedColor(Theme.key_windowBackgroundWhiteGrayText))
            content.addView(descTv, LayoutHelper.createLinear(-1, -2, 12, 0, 12, 16))

            infoCard = buildInfoCard(activity, sheet)
            content.addView(infoCard, LayoutHelper.createLinear(-1, -2, 0, 0, 0, 18))

            restartBtn = ButtonWithCounterView(activity, True, resourceProvider)
            restartBtn.setText(buttonText, False)
            restartBtn.setRound()

            def onRestartClick(v):
                try:
                    sheet.dismiss()
                except Exception as e:
                    logx(f"sheet dismiss error: {e}", isDebug=False)
                restartApp()

            restartBtn.setOnClickListener(OnClickListener(onRestartClick))
            content.addView(restartBtn, LayoutHelper.createLinear(-1, 48, 0, 0, 0, 0))

            sheet.setCustomView(content)
            sheet.show()
            logx("RestartRequired bottomsheet displayed successfully", isDebug=False)
        except Exception as e:
            logx(f"showRestartBottomSheet error: {e}", isDebug=False)

    run_on_ui_thread(showTask, 1200)
