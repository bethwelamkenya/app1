package com.bethwelamkenya.app2

import javafx.event.Event
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException

class StageSwitcher {
    private lateinit var scene : Scene
    private lateinit var root : Parent
    private lateinit var stage: Stage
    fun switchStages(event: Event, fxml: String, title: String, resizable: Boolean){
        try {
            root = FXMLLoader.load(javaClass.getResource(fxml))
            val source = event.source as Node
            stage = source.scene.window as Stage
            scene = Scene(root)
            stage.scene = scene
            stage.centerOnScreen()
            stage.isResizable = resizable
            stage.title = title
            stage.show()
//            scene = source.scene
//            stage = scene.window as Stage
//            stage = (Stage)((Node)event.source)
        } catch (e: IOException){
            throw RuntimeException(e)
        }
    }

    fun switchStagesFromMenu(fxml: String, title: String, resizable: Boolean, menuBar: MenuBar){
        try {
            root = FXMLLoader.load(javaClass.getResource(fxml))
            stage = menuBar.scene.window as Stage
            scene = Scene(root)
            stage.scene = scene
            stage.centerOnScreen()
            stage.isResizable = resizable
            stage.title = title
            stage.show()
        } catch (e: IOException){
            throw RuntimeException(e)
        }
    }

    fun switchUtilityStages(event: Event, fxml: String, title: String, resizable: Boolean){
        try {
            root = FXMLLoader.load(javaClass.getResource(fxml))
            val source = event.source as Node
            val primarySage = source.scene.window
            stage = Stage()
            scene = Scene(root)
            stage.scene = scene
            stage.initModality(Modality.WINDOW_MODAL)
            stage.initOwner(primarySage)
            stage.x = primarySage.x
            stage.y = primarySage.y
            stage.isResizable = resizable
            stage.title = title
            stage.show()
        } catch (e: IOException){
            throw RuntimeException(e)
        }
    }

    fun switchDialogStages(title: String, message: String){
        val dialog: Dialog<Any> = Dialog()
        dialog.title = title
        dialog.dialogPane.buttonTypes.add(ButtonType.CANCEL)
        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
        dialog.dialogPane.content = dialogPane(message)
        dialog.dialogPane.style = "-fx-background-color:  #3c3535;"
        dialog.show()
    }

    private fun dialogPane(message: String): Node {
        val label = Label(message)
        label.style = "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 18;" +
                "-fx-font-weight: bold;"
        return label
    }

    fun closeStage(event: Event){
        val source = event.source as Node
        stage = source.scene.window as Stage
        stage.close()
    }
}