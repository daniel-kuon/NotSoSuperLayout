package com.github.danielkuon.superlayout.dialogs

import com.github.danielkuon.superlayout.model.Layout
import com.github.danielkuon.superlayout.services.LayoutStorageService
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

/**
 * Dialog for managing saved layouts.
 */
class ManageLayoutsDialog(private val project: Project) : DialogWrapper(project) {
    private val storageService = LayoutStorageService.getInstance()
    private val layoutList = JBList<String>()
    private val layoutListModel = DefaultListModel<String>()
    
    init {
        title = "Manage Layouts"
        init()
        refreshLayoutList()
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.preferredSize = Dimension(500, 300)
        
        // Create layout list
        layoutList.model = layoutListModel
        layoutList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        
        val scrollPane = JBScrollPane(layoutList)
        panel.add(scrollPane, BorderLayout.CENTER)
        
        // Create buttons panel
        val buttonsPanel = JPanel()
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)
        
        val applyButton = JButton("Apply Selected Layout")
        applyButton.addActionListener { applySelectedLayout() }
        
        val deleteButton = JButton("Delete Selected Layout")
        deleteButton.addActionListener { deleteSelectedLayout() }
        
        val configureButton = JButton("Configure Selected Layout")
        configureButton.addActionListener { configureSelectedLayout() }
        
        buttonsPanel.add(applyButton)
        buttonsPanel.add(Box.createVerticalStrut(5))
        buttonsPanel.add(deleteButton)
        buttonsPanel.add(Box.createVerticalStrut(5))
        buttonsPanel.add(configureButton)
        
        panel.add(buttonsPanel, BorderLayout.EAST)
        
        return panel
    }
    
    /**
     * Refreshes the list of layouts.
     */
    private fun refreshLayoutList() {
        layoutListModel.clear()
        storageService.getLayouts().forEach { layout ->
            layoutListModel.addElement(layout.name)
        }
    }
    
    /**
     * Applies the selected layout.
     */
    private fun applySelectedLayout() {
        val selectedLayoutName = layoutList.selectedValue ?: return
        
        storageService.applyLayout(selectedLayoutName)
        
        Messages.showInfoMessage(
            project,
            "Layout '$selectedLayoutName' has been applied.",
            "Layout Applied"
        )
    }
    
    /**
     * Deletes the selected layout.
     */
    private fun deleteSelectedLayout() {
        val selectedLayoutName = layoutList.selectedValue ?: return
        
        val result = Messages.showYesNoDialog(
            project,
            "Are you sure you want to delete layout '$selectedLayoutName'?",
            "Confirm Delete",
            Messages.getQuestionIcon()
        )
        
        if (result == Messages.YES) {
            storageService.removeLayout(selectedLayoutName)
            refreshLayoutList()
            
            Messages.showInfoMessage(
                project,
                "Layout '$selectedLayoutName' has been deleted.",
                "Layout Deleted"
            )
        }
    }
    
    /**
     * Opens the configuration dialog for the selected layout.
     */
    private fun configureSelectedLayout() {
        val selectedLayoutName = layoutList.selectedValue ?: return
        val layout = storageService.getLayout(selectedLayoutName) ?: return
        
        val dialog = ConfigureLayoutDialog(project, layout)
        if (dialog.showAndGet()) {
            refreshLayoutList()
        }
    }
}
