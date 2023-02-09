package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.bethwelamkenya.app2.models.Admin
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class ResetPasswordController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private lateinit var admin: Admin

    @FXML
    lateinit var userName: TextField

    @FXML
    lateinit var security: TextField

    @FXML
    lateinit var answer: PasswordField

    @FXML
    lateinit var newPassword: PasswordField

    @FXML
    lateinit var confirmPassword: PasswordField

    @FXML
    lateinit var done: Button

    @FXML
    lateinit var cancel: Button

    @FXML
    lateinit var checkUserName: Button

    @FXML
    lateinit var checkAnswer: Button

    @FXML
    lateinit var checkBoxUserName: CheckBox

    @FXML
    lateinit var checkBoxAnswer: CheckBox
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        done.isDisable = true
        cancel.setOnAction { stageSwitcher.closeStage(it) }
        checkUserName.setOnAction {
            if (adapter.adminExists(userName.text)){
                checkBoxUserName.isSelected = true
                admin = adapter.getAdmin(userName.text)!!
                security.text = admin.security
            } else{
                checkBoxUserName.isSelected = false
                done.isDisable = true
                stageSwitcher.switchDialogStages("Error", "The User Name Does Not Exist")
            }
        }
        checkAnswer.setOnAction {
           if (sha256(answer.text).equals(admin.answer)) {
               checkBoxAnswer.isSelected = true
               done.isDisable = false
           } else{
               checkBoxAnswer.isSelected = false
               done.isDisable = true
               stageSwitcher.switchDialogStages("Error", "Answer Does Not Match To Existing Answer. Please Try Again")
           }
        }
        done.setOnAction { validateDetails(it) }
    }

    private fun validateDetails(it: Event) {
        if (newPassword.text.isEmpty()){
            stageSwitcher.switchDialogStages("Error", "Please Fill In The Password")
        } else{
            if (newPassword.text.equals(confirmPassword.text)){
                adapter.resetAdminPassword(admin.userName, newPassword.text)
                stageSwitcher.switchDialogStages("Confirmation", "Password Updated Successfully")
                stageSwitcher.closeStage(it)
            } else {
                stageSwitcher.switchDialogStages("Error", "Passwords Do Not Match")
            }
        }
    }

    fun goBack(mouseEvent: MouseEvent) {
        stageSwitcher.closeStage(mouseEvent)
    }

    private fun sha256(input: String): String? {
        return try {
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            digest.joinToString("") { String.format("%02x", it) }
        } catch (e: NoSuchAlgorithmException) {
            println(e)
            null
        }
    }
}