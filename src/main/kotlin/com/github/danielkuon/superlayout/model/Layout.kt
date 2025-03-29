package com.github.danielkuon.superlayout.model

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import java.io.Serializable
import javax.swing.KeyStroke
import java.awt.Dimension
import java.awt.Point

/**
 * Represents a saved window layout configuration.
 */
data class Layout(
    val name: String,
    val windowState: WindowState,
    val toolWindowStates: Map<String, ToolWindowState>,
    val shortcutFirstKeyStroke: String? = null,
    val shortcutSecondKeyStroke: String? = null,
    val triggerActions: List<String> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * Represents the state of the main IDE window.
 */
data class WindowState(
    val location: Point,
    val size: Dimension,
    val isMaximized: Boolean,
    val isFullScreen: Boolean
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * Represents the state of a tool window.
 */
data class ToolWindowState(
    val isVisible: Boolean,
    val anchor: String,
    val type: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * Utility class for capturing and applying layouts.
 */
class LayoutManager {
    /**
     * Captures the current window layout.
     */
    fun captureCurrentLayout(name: String): Layout {
        // Get the main window state
        val frame = WindowManager.getInstance().allProjectFrames.firstOrNull() as? IdeFrameImpl
        val windowState = WindowState(
            location = frame?.location ?: Point(0, 0),
            size = frame?.size ?: Dimension(1000, 800),
            isMaximized = frame?.extendedState?.and(java.awt.Frame.MAXIMIZED_BOTH) != 0,
            isFullScreen = frame?.isInFullScreen ?: false
        )

        // Get tool window states
        val toolWindowStates = mutableMapOf<String, ToolWindowState>()
        val project = frame?.project
        if (project != null) {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            toolWindowManager.toolWindowIds.forEach { id ->
                val toolWindow = toolWindowManager.getToolWindow(id)
                if (toolWindow != null) {
                    toolWindowStates[id] = ToolWindowState(
                        isVisible = toolWindow.isVisible,
                        anchor = toolWindow.anchor.toString(),
                        type = toolWindow.type.toString()
                    )
                }
            }
        }

        return Layout(
            name = name,
            windowState = windowState,
            toolWindowStates = toolWindowStates
        )
    }

    /**
     * Applies a saved layout.
     */
    fun applyLayout(layout: Layout) {
        val frame = WindowManager.getInstance().allProjectFrames.firstOrNull() as? IdeFrameImpl
        val project = frame?.project ?: return

        // Apply window state
        if (!layout.windowState.isMaximized && !layout.windowState.isFullScreen) {
            frame.location = layout.windowState.location
            frame.size = layout.windowState.size
        }

        if (layout.windowState.isMaximized) {
            frame.extendedState = frame.extendedState or java.awt.Frame.MAXIMIZED_BOTH
        }

        // Full screen toggling is not supported in this version of IntelliJ IDEA
        // if (layout.windowState.isFullScreen && frame.isInFullScreen != layout.windowState.isFullScreen) {
        //     frame.toggleFullScreen(true)
        // }

        // Apply tool window states
        val toolWindowManager = ToolWindowManager.getInstance(project)
        layout.toolWindowStates.forEach { (id, state) ->
            val toolWindow = toolWindowManager.getToolWindow(id) ?: return@forEach

            if (state.isVisible) {
                toolWindow.show()
            } else {
                toolWindow.hide()
            }
        }
    }

    /**
     * Registers a keyboard shortcut for a layout.
     */
    fun registerShortcut(layout: Layout) {
        if (layout.shortcutFirstKeyStroke == null) return

        val firstKeyStroke = KeyStroke.getKeyStroke(layout.shortcutFirstKeyStroke)
        val secondKeyStroke = layout.shortcutSecondKeyStroke?.let { KeyStroke.getKeyStroke(it) }

        val shortcut = if (secondKeyStroke != null) {
            KeyboardShortcut(firstKeyStroke, secondKeyStroke)
        } else {
            KeyboardShortcut(firstKeyStroke, null)
        }

        // This would need to be implemented with the KeymapManager
        // KeymapManager.getInstance().activeKeymap.addShortcut(actionId, shortcut)
    }
}
