package com.bethwelamkenya.app2

import com.bethwelamkenya.app2.databases.DatabaseAdapter
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        val scene: Scene = Scene(fxmlLoader.load())
        stage.title = "Hello!"
        stage.centerOnScreen()
        stage.isResizable = false
        stage.scene = scene
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val adapter = DatabaseAdapter()
            val appDatabaseDir = File(System.getProperty("user.home") + "/AppData/Roaming/App2/Databases")
            if (!appDatabaseDir.exists()){
                appDatabaseDir.mkdirs()
                adapter.memberTable()
                adapter.adminTable()
                adapter.attendaceTable()
                adapter.dateTable()
            }
            launch(HelloApplication::class.java)
        }
    }
}

//public fun main() {
//    Application.launch(HelloApplication::class.java)
//}