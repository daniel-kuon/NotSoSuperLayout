package com.github.danielkuon.superlayout.actions

import com.github.danielkuon.superlayout.dialogs.ManageLayoutsDialog
import com.github.danielkuon.superlayout.services.LayoutStorageService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

/**
 * Action to manage saved layouts.
 */
class ManageLayoutsAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Open the manage layouts dialog
        val dialog = ManageLayoutsDialog(project)
        dialog.show()
    }
}
