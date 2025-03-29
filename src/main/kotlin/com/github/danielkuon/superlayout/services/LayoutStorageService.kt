package com.github.danielkuon.superlayout.services

import com.github.danielkuon.superlayout.model.Layout
import com.github.danielkuon.superlayout.model.LayoutManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.XCollection

/**
 * Service for storing and retrieving layouts.
 * This is a persistent service that saves layouts between IDE sessions.
 */
@State(
    name = "SuperLayoutSettings",
    storages = [Storage("superLayout.xml")]
)
class LayoutStorageService : PersistentStateComponent<LayoutStorageService.State> {
    private val layoutManager = LayoutManager()
    private val state = State()

    /**
     * State class for serialization.
     */
    class State {
        @XCollection
        var layouts: MutableList<SerializableLayout> = mutableListOf()
    }

    /**
     * Serializable wrapper for Layout class.
     */
    data class SerializableLayout(
        var name: String = "",
        var windowStateJson: String = "",
        var toolWindowStatesJson: String = "",
        var shortcutFirstKeyStroke: String? = null,
        var shortcutSecondKeyStroke: String? = null,
        var triggerActions: List<String> = emptyList()
    )

    override fun getState(): State = state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    /**
     * Saves the current layout with the given name.
     */
    fun saveCurrentLayout(name: String): Layout {
        val layout = layoutManager.captureCurrentLayout(name)
        addLayout(layout)
        return layout
    }

    /**
     * Adds a layout to the storage.
     */
    fun addLayout(layout: Layout) {
        // Convert to serializable format
        val serializableLayout = SerializableLayout(
            name = layout.name,
            windowStateJson = layout.windowState.toString(), // In a real implementation, use proper JSON serialization
            toolWindowStatesJson = layout.toolWindowStates.toString(), // In a real implementation, use proper JSON serialization
            shortcutFirstKeyStroke = layout.shortcutFirstKeyStroke,
            shortcutSecondKeyStroke = layout.shortcutSecondKeyStroke,
            triggerActions = layout.triggerActions
        )

        // Remove any existing layout with the same name
        state.layouts.removeIf { it.name == layout.name }

        // Add the new layout
        state.layouts.add(serializableLayout)
    }

    /**
     * Gets all saved layouts.
     */
    fun getLayouts(): List<Layout> {
        // In a real implementation, properly deserialize from JSON
        return state.layouts.map { serLayout ->
            // This is a placeholder - in a real implementation, properly deserialize from JSON
            Layout(
                name = serLayout.name,
                windowState = layoutManager.captureCurrentLayout("temp").windowState, // Placeholder
                toolWindowStates = mapOf(), // Placeholder
                shortcutFirstKeyStroke = serLayout.shortcutFirstKeyStroke,
                shortcutSecondKeyStroke = serLayout.shortcutSecondKeyStroke,
                triggerActions = serLayout.triggerActions
            )
        }
    }

    /**
     * Gets a layout by name.
     */
    fun getLayout(name: String): Layout? {
        return getLayouts().find { it.name == name }
    }

    /**
     * Removes a layout by name.
     */
    fun removeLayout(name: String) {
        state.layouts.removeIf { it.name == name }
    }

    /**
     * Applies a layout by name.
     */
    fun applyLayout(name: String) {
        val layout = getLayout(name) ?: return
        layoutManager.applyLayout(layout)
    }

    /**
     * Updates a layout's shortcut.
     */
    fun updateLayoutShortcut(name: String, firstKeyStroke: String?, secondKeyStroke: String?) {
        val layoutIndex = state.layouts.indexOfFirst { it.name == name }
        if (layoutIndex >= 0) {
            state.layouts[layoutIndex].shortcutFirstKeyStroke = firstKeyStroke
            state.layouts[layoutIndex].shortcutSecondKeyStroke = secondKeyStroke
        }
    }

    /**
     * Updates a layout's trigger actions.
     */
    fun updateLayoutTriggerActions(name: String, triggerActions: List<String>) {
        val layoutIndex = state.layouts.indexOfFirst { it.name == name }
        if (layoutIndex >= 0) {
            state.layouts[layoutIndex].triggerActions = triggerActions
        }
    }

    /**
     * Checks if an action should trigger a layout change.
     */
    fun checkActionTrigger(event: AnActionEvent) {
        val actionId = event.actionManager.getId(event.getActionManager().getAction(event.place)) ?: return

        for (layout in getLayouts()) {
            if (layout.triggerActions.contains(actionId)) {
                layoutManager.applyLayout(layout)
                break
            }
        }
    }

    companion object {
        /**
         * Gets the instance of the service.
         */
        fun getInstance(): LayoutStorageService {
            return ServiceManager.getService(LayoutStorageService::class.java)
        }
    }
}
