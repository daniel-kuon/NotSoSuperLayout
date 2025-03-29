package com.github.danielkuon.superlayout.actions

import com.github.danielkuon.superlayout.services.LayoutStorageService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.project.DumbAware

/**
 * Action to save the current window layout.
 */
class SaveLayoutAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Prompt for layout name
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
        
        // Save the layout
        val storageService = LayoutStorageService.getInstance()
        val layout = storageService.saveCurrentLayout(layoutName)
        
        Messages.showInfoMessage(
            project,
            "Layout '$layoutName' has been saved successfully.",
            "Layout Saved"
        )
    }
}
