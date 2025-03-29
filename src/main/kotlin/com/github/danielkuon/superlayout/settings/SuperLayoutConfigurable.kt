package com.github.danielkuon.superlayout.settings

import com.github.danielkuon.superlayout.dialogs.ConfigureLayoutDialog
import com.github.danielkuon.superlayout.model.Layout
import com.github.danielkuon.superlayout.services.LayoutStorageService
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

/**
 * Settings page for the Super Layout plugin.
 */
class SuperLayoutConfigurable : Configurable {
    private val storageService = LayoutStorageService.getInstance()
    private var panel: JPanel? = null
    private var layoutList: JBList<String>? = null
    private var layoutListModel: DefaultListModel<String>? = null

    override fun getDisplayName(): String = "Super Layout"

    override fun getHelpTopic(): String? = null

    override fun createComponent(): JComponent? {
        if (panel == null) {
            panel = JPanel(BorderLayout())
            panel!!.preferredSize = Dimension(600, 400)

            // Create layout list
            layoutListModel = DefaultListModel<String>()
            layoutList = JBList<String>()
            layoutList!!.model = layoutListModel
            layoutList!!.selectionMode = ListSelectionModel.SINGLE_SELECTION

            val scrollPane = JBScrollPane(layoutList)
            scrollPane.border = BorderFactory.createTitledBorder("Saved Layouts")
            panel!!.add(scrollPane, BorderLayout.CENTER)

            // Create buttons panel
            val buttonsPanel = JPanel()
            buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)

            val saveButton = JButton("Save Current Layout")
            saveButton.addActionListener { saveCurrentLayout() }

            val applyButton = JButton("Apply Selected Layout")
            applyButton.addActionListener { applySelectedLayout() }

            val deleteButton = JButton("Delete Selected Layout")
            deleteButton.addActionListener { deleteSelectedLayout() }

            val configureButton = JButton("Configure Selected Layout")
            configureButton.addActionListener { configureSelectedLayout() }

            buttonsPanel.add(saveButton)
            buttonsPanel.add(Box.createVerticalStrut(5))
            buttonsPanel.add(applyButton)
            buttonsPanel.add(Box.createVerticalStrut(5))
            buttonsPanel.add(deleteButton)
            buttonsPanel.add(Box.createVerticalStrut(5))
            buttonsPanel.add(configureButton)

            panel!!.add(buttonsPanel, BorderLayout.EAST)

            refreshLayoutList()
        }

        return panel
    }

    override fun isModified(): Boolean = false

    override fun apply() {
        // Nothing to apply as changes are applied immediately
    }

    override fun reset() {
        refreshLayoutList()
    }

    override fun disposeUIResources() {
        panel = null
        layoutList = null
        layoutListModel = null
    }

    /**
     * Refreshes the list of layouts.
     */
    private fun refreshLayoutList() {
        layoutListModel?.clear()
        storageService.getLayouts().forEach { layout ->
            layoutListModel?.addElement(layout.name)
        }
    }

    /**
     * Saves the current layout.
     */
    private fun saveCurrentLayout() {
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return

        val layoutName = Messages.showInputDialog(
            project,
            "Enter a name for this layout:",
            "Save Layout",
            Messages.getQuestionIcon()
        )

        if (layoutName.isNullOrBlank()) {
            Messages.showErrorDialog(
                project,
                "Layout name cannot be empty",
                "Error Saving Layout"
            )
            return
        }

        storageService.saveCurrentLayout(layoutName)
        refreshLayoutList()

        Messages.showInfoMessage(
            project,
            "Layout '$layoutName' has been saved successfully.",
            "Layout Saved"
        )
    }

    /**
     * Applies the selected layout.
     */
    private fun applySelectedLayout() {
        val selectedLayoutName = layoutList?.selectedValue ?: return
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return

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
        val selectedLayoutName = layoutList?.selectedValue ?: return
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return

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
        val selectedLayoutName = layoutList?.selectedValue ?: return
        val layout = storageService.getLayout(selectedLayoutName) ?: return
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return

        val dialog = ConfigureLayoutDialog(project, layout)
        if (dialog.showAndGet()) {
            refreshLayoutList()
        }
    }
}
