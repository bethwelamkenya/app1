package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.PassData
import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import java.io.File
import java.net.URL
import java.sql.SQLException
import java.time.LocalTime
import java.util.*

class HelloController : Initializable{

    private val stageSwitcher: StageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private val accountTypes = arrayOf("Admin", "Member", "Developer")
    private var firstTime = false

    @FXML
    lateinit var windowsUserName: Label

    @FXML
    lateinit var timeOfDay: Label

    @FXML
    lateinit var status: Label

    @FXML
    lateinit var resetPassword: Hyperlink

    @FXML
    lateinit var logIn: Button

    @FXML
    lateinit var password: PasswordField

    @FXML
    lateinit var userName: TextField

    @FXML
    lateinit var accountType: ComboBox<String>
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val databaseFile = File(System.getProperty("user.home") + "/AppData/Roaming/App/Databases/church.sqlite")
        firstTime = !databaseFile.exists()
        windowsUserName.text = System.getProperty("user.name")
        logIn.isDisable = true
        if (adapter.isDatabaseConnected()){
            status.text = "Connected".uppercase(Locale.getDefault())
        } else{
            status.text = "Not Connected".uppercase(Locale.getDefault())
        }
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//            }
//        }, 2000)
        val time = LocalTime.now()
        if (time.isBefore(LocalTime.NOON)) {
            timeOfDay.text = "Morning".uppercase(Locale.getDefault())
        } else if (time.isBefore(LocalTime.of(17, 0))) {
            timeOfDay.text = "AfterNoon".uppercase(Locale.getDefault())
        } else if (time.isBefore(LocalTime.of(20, 0))) {
            timeOfDay.text = "Evening".uppercase(Locale.getDefault())
        } else {
            timeOfDay.text = "Night".uppercase(Locale.getDefault())
        }
        accountType.items.addAll(accountTypes)
        accountType.value = accountTypes[0]
        userName.setOnKeyReleased {
            if (accountType.selectionModel.selectedIndex == 1){
                validateMemberDetails()
            } else{
                validateDetails()
            }
        }
        password.setOnKeyReleased {
            if (accountType.selectionModel.selectedIndex == 1) {
                validateMemberDetails()
            } else {
                validateDetails()
            }
        }
        accountType.setOnAction {
            if (accountType.selectionModel.selectedIndex == 1) {
                validateMemberDetails()
            } else {
                validateDetails()
            }
        }
        logIn.setOnAction{checkDetailsValidity(it)}
        resetPassword.setOnAction { stageSwitcher.switchUtilityStages(it, "reset-password.fxml","Reset Password", false) }
    }

    private fun validateDetails() {
        logIn.isDisable = userName.text.isEmpty() || password.text.isEmpty()
    }

    private fun validateMemberDetails() {
        logIn.isDisable = userName.text.isEmpty()
    }

    private fun checkDetailsValidity(event: Event){
        try {
            if (firstTime){
                adapter.memberTable()
                adapter.adminTable()
                adapter.attendaceTable()
                adapter.dateTable()
            }
            if (accountType.selectionModel.selectedIndex == 0){
                if (adapter.logIn(userName.text, password.text)){
                    println("hi " + userName.text)
                    PassData.adminName = userName.text
                    stageSwitcher.switchStages(event, "admin-home.fxml", userName.text, true)
                } else{
                    stageSwitcher.switchDialogStages("Error", "Wrong Details Passed. Please Try Again")
                }
            } else if (accountType.selectionModel.selectedIndex ==1){
                if (adapter.memberExists(userName.text)){
                    PassData.passedMember = adapter.getMember(userName.text)
                    stageSwitcher.switchStages(event, "member-home.fxml", userName.text, true)
                } else{
                    stageSwitcher.switchDialogStages("Error", "Wrong Details Passed. Please Try Again")
                }
            } else{
                if (userName.text.equals("bethu") && password.text.equals("9852")){
                    stageSwitcher.switchStages(event, "developer-home.fxml", "Developer Home", true)
                } else{
                    stageSwitcher.switchDialogStages("Error", "Wrong Details Passed. Please Try Again")
                }
            }
        } catch (e: SQLException){
            stageSwitcher.switchDialogStages("Error", e.toString())
            throw RuntimeException(e)
        }
    }


}
