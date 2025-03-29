package com.github.danielkuon.superlayout.dialogs

import com.github.danielkuon.superlayout.model.Layout
import com.github.danielkuon.superlayout.services.LayoutStorageService
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Dialog for configuring a layout's shortcuts and trigger actions.
 */
class ConfigureLayoutDialog(
    private val project: Project,
    private val layout: Layout
) : DialogWrapper(project) {
    private val storageService = LayoutStorageService.getInstance()
    
    private val shortcutField = JBTextField(30)
    private val actionsList = JBList<String>()
    private val actionsListModel = DefaultListModel<String>()
    private val selectedActionsList = JBList<String>()
    private val selectedActionsListModel = DefaultListModel<String>()
    
    private var shortcutFirstKeyStroke: String? = layout.shortcutFirstKeyStroke
    private var shortcutSecondKeyStroke: String? = layout.shortcutSecondKeyStroke
    
    init {
        title = "Configure Layout: ${layout.name}"
        init()
        
        // Initialize shortcut field
        updateShortcutField()
        
        // Initialize actions lists
        refreshActionsList()
        refreshSelectedActionsList()
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.preferredSize = Dimension(600, 400)
        
        // Create shortcut panel
        val shortcutPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.WEST
        shortcutPanel.add(JLabel("Keyboard Shortcut:"), gbc)
        
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        shortcutPanel.add(shortcutField, gbc)
        
        gbc.gridx = 2
        gbc.weightx = 0.0
        val recordButton = JButton("Record Shortcut")
        recordButton.addActionListener { recordShortcut() }
        shortcutPanel.add(recordButton, gbc)
        
        gbc.gridx = 3
        val clearButton = JButton("Clear")
        clearButton.addActionListener { clearShortcut() }
        shortcutPanel.add(clearButton, gbc)
        
        panel.add(shortcutPanel, BorderLayout.NORTH)
        
        // Create actions panel
        val actionsPanel = JPanel(BorderLayout())
        actionsPanel.border = BorderFactory.createTitledBorder("Trigger Actions")
        
        // Create filter field
        val filterField = JBTextField()
        filterField.getDocument().addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = filterActions(filterField.text)
            override fun removeUpdate(e: DocumentEvent) = filterActions(filterField.text)
            override fun changedUpdate(e: DocumentEvent) = filterActions(filterField.text)
        })
        
        val filterPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        filterPanel.add(JLabel("Filter:"))
        filterPanel.add(filterField)
        
        actionsPanel.add(filterPanel, BorderLayout.NORTH)
        
        // Create lists panel
        val listsPanel = JPanel(GridBagLayout())
        val listsGbc = GridBagConstraints()
        
        // Available actions list
        listsGbc.gridx = 0
        listsGbc.gridy = 0
        listsGbc.weightx = 0.5
        listsGbc.weighty = 1.0
        listsGbc.fill = GridBagConstraints.BOTH
        
        actionsList.model = actionsListModel
        actionsList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        val actionsScrollPane = JBScrollPane(actionsList)
        actionsScrollPane.border = BorderFactory.createTitledBorder("Available Actions")
        listsPanel.add(actionsScrollPane, listsGbc)
        
        // Buttons panel
        listsGbc.gridx = 1
        listsGbc.weightx = 0.0
        listsGbc.fill = GridBagConstraints.NONE
        
        val buttonsPanel = JPanel(GridBagLayout())
        val buttonsGbc = GridBagConstraints()
        
        buttonsGbc.gridx = 0
        buttonsGbc.gridy = 0
        buttonsGbc.insets = java.awt.Insets(5, 5, 5, 5)
        
        val addButton = JButton("→")
        addButton.addActionListener { addSelectedAction() }
        buttonsPanel.add(addButton, buttonsGbc)
        
        buttonsGbc.gridy = 1
        val removeButton = JButton("←")
        removeButton.addActionListener { removeSelectedAction() }
        buttonsPanel.add(removeButton, buttonsGbc)
        
        listsPanel.add(buttonsPanel, listsGbc)
        
        // Selected actions list
        listsGbc.gridx = 2
        listsGbc.weightx = 0.5
        listsGbc.fill = GridBagConstraints.BOTH
        
        selectedActionsList.model = selectedActionsListModel
        selectedActionsList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        val selectedActionsScrollPane = JBScrollPane(selectedActionsList)
        selectedActionsScrollPane.border = BorderFactory.createTitledBorder("Selected Trigger Actions")
        listsPanel.add(selectedActionsScrollPane, listsGbc)
        
        actionsPanel.add(listsPanel, BorderLayout.CENTER)
        
        panel.add(actionsPanel, BorderLayout.CENTER)
        
        return panel
    }
    
    override fun doOKAction() {
        // Update layout in storage service
        storageService.updateLayoutShortcut(
            layout.name,
            shortcutFirstKeyStroke,
            shortcutSecondKeyStroke
        )
        
        val selectedActions = mutableListOf<String>()
        for (i in 0 until selectedActionsListModel.size()) {
            selectedActions.add(selectedActionsListModel.getElementAt(i))
        }
        
        storageService.updateLayoutTriggerActions(layout.name, selectedActions)
        
        super.doOKAction()
    }
    
    /**
     * Updates the shortcut field text.
     */
    private fun updateShortcutField() {
        val text = if (shortcutFirstKeyStroke != null) {
            val firstStroke = KeyStroke.getKeyStroke(shortcutFirstKeyStroke)
            val secondStroke = shortcutSecondKeyStroke?.let { KeyStroke.getKeyStroke(it) }
            
            if (secondStroke != null) {
                "${KeymapUtil.getKeystrokeText(firstStroke)}, ${KeymapUtil.getKeystrokeText(secondStroke)}"
            } else {
                KeymapUtil.getKeystrokeText(firstStroke)
            }
        } else {
            ""
        }
        
        shortcutField.text = text
    }
    
    /**
     * Records a keyboard shortcut.
     */
    private fun recordShortcut() {
        Messages.showInfoMessage(
            project,
            "Press the key combination you want to use as a shortcut.",
            "Record Shortcut"
        )
        
        // In a real implementation, we would use a proper shortcut recorder component
        // For this example, we'll just set a dummy shortcut
        shortcutFirstKeyStroke = "control alt L"
        shortcutSecondKeyStroke = null
        
        updateShortcutField()
    }
    
    /**
     * Clears the keyboard shortcut.
     */
    private fun clearShortcut() {
        shortcutFirstKeyStroke = null
        shortcutSecondKeyStroke = null
        updateShortcutField()
    }
    
    /**
     * Refreshes the list of available actions.
     */
    private fun refreshActionsList() {
        actionsListModel.clear()
        
        val actionManager = ActionManager.getInstance()
        val actionIds = actionManager.getActionIdList("")
        
        actionIds.sorted().forEach { actionId ->
            if (!layout.triggerActions.contains(actionId)) {
                actionsListModel.addElement(actionId)
            }
        }
    }
    
    /**
     * Refreshes the list of selected actions.
     */
    private fun refreshSelectedActionsList() {
        selectedActionsListModel.clear()
        layout.triggerActions.forEach { actionId ->
            selectedActionsListModel.addElement(actionId)
        }
    }
    
    /**
     * Filters the actions list based on the filter text.
     */
    private fun filterActions(filterText: String) {
        actionsListModel.clear()
        
        val actionManager = ActionManager.getInstance()
        val actionIds = actionManager.getActionIdList("")
        
        actionIds.sorted().forEach { actionId ->
            if (!layout.triggerActions.contains(actionId) && 
                (filterText.isEmpty() || actionId.contains(filterText, ignoreCase = true))) {
                actionsListModel.addElement(actionId)
            }
        }
    }
    
    /**
     * Adds the selected action to the selected actions list.
     */
    private fun addSelectedAction() {
        val selectedAction = actionsList.selectedValue ?: return
        
        actionsListModel.removeElement(selectedAction)
        selectedActionsListModel.addElement(selectedAction)
    }
    
    /**
     * Removes the selected action from the selected actions list.
     */
    private fun removeSelectedAction() {
        val selectedAction = selectedActionsList.selectedValue ?: return
        
        selectedActionsListModel.removeElement(selectedAction)
        actionsListModel.addElement(selectedAction)
    }
}
