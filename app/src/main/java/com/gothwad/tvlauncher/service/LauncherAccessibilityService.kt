package com.gothwad.tvlauncher.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.gothwad.tvlauncher.MainActivity

/**
 * Accessibility Service to handle Home key capture and OEM launcher overrides
 * on Android TV and Google TV where standard default home app selection is locked.
 */
class LauncherAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = flags or
                AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val pkg = event.packageName?.toString() ?: return
            if (isStockTvLauncher(pkg) && pkg != packageName) {
                // The OEM/Google TV launcher was brought to foreground; return to Gothwad Launcher
                launchHome()
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_HOME) {
            if (event.action == KeyEvent.ACTION_UP) {
                launchHome()
            }
            return true // Consume the HOME key event so locked stock TV launcher cannot react
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {}

    private fun launchHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    companion object {
        private val STOCK_LAUNCHERS = setOf(
            "com.google.android.apps.tv.launcherx",
            "com.google.android.tvlauncher",
            "com.google.android.tungsten.setupwraith",
            "com.amazon.tv.launcher",
            "com.amazon.firehomestarter",
            "com.xiaomi.mitv.tvhome",
            "com.mitv.tvhome",
            "com.tcl.tvplayer",
            "com.hisense.tv.launcher",
            "com.droidlogic.tv.launcher"
        )

        fun isStockTvLauncher(pkg: String): Boolean =
            STOCK_LAUNCHERS.any { pkg.startsWith(it, ignoreCase = true) }

        fun isEnabled(context: Context): Boolean {
            val expectedComponent = "${context.packageName}/${LauncherAccessibilityService::class.java.name}"
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabledServices.split(':').any {
                it.equals(expectedComponent, ignoreCase = true) ||
                    (it.contains(context.packageName, ignoreCase = true) &&
                        it.contains("LauncherAccessibilityService", ignoreCase = true))
            }
        }
    }
}
