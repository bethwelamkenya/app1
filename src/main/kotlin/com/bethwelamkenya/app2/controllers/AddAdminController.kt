package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.PassData
import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.bethwelamkenya.app2.models.Admin
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import java.net.URL
import java.util.*

class AddAdminController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private val emailPattern = Regex("[a-zA-Z0-9]+@+[a-zA-Z0-9]+[.]+[a-zA-Z0-9]+$")
    private val mobilNoPattern = Regex("[0-9]*$")

    @FXML
    lateinit var name: TextField

    @FXML
    lateinit var email: TextField

    @FXML
    lateinit var mobile: TextField

    @FXML
    lateinit var userName: TextField

    @FXML
    lateinit var security: TextField

    @FXML
    lateinit var password: PasswordField

    @FXML
    lateinit var confirmPassword: PasswordField

    @FXML
    lateinit var answer: PasswordField

    @FXML
    lateinit var done: Button

    @FXML
    lateinit var cancel: Button
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        done.isDisable = true
        name.setOnKeyReleased {validateDetails()}
        email.setOnKeyReleased {validateDetails()}
        userName.setOnKeyReleased {validateDetails()}
        mobile.setOnKeyReleased {validateDetails()}
        security.setOnKeyReleased {validateDetails()}
        password.setOnKeyReleased {validateDetails()}
        confirmPassword.setOnKeyReleased {validateDetails()}
        answer.setOnKeyReleased {validateDetails()}
        cancel.setOnAction { stageSwitcher.closeStage(it) }
        done.setOnAction { editAdmin(it) }
    }

    private fun validateDetails() {
        done.isDisable = name.text.isEmpty() || email.text.isEmpty() || mobile.text.isEmpty() || userName.text.isEmpty()
                || security.text.isEmpty() || password.text.isEmpty() || confirmPassword.text.isEmpty()
                || answer.text.isEmpty() || !mobile.text.matches(mobilNoPattern)
                || !email.text.matches(emailPattern) || !password.text.equals(confirmPassword.text)
    }

    private fun editAdmin(it: ActionEvent) {
        val myName = name.text
        val myEmail = email.text
        val myMobile = mobile.text.toLong()
        val myUsername = userName.text
        val  myPassword = password.text
        val mySecurity = security.text
        val myAnswer = answer.text
        if (adapter.insertAdmin(myName, myEmail, myMobile, myUsername, myPassword, mySecurity, myAnswer)) {
            stageSwitcher.switchDialogStages("Error", "Could Not Add Admin. Please Try Again")
        } else{
            stageSwitcher.switchDialogStages("Confirmation", "Admin Added Successfully")
            stageSwitcher.closeStage(it)
        }
    }

    fun goBack(mouseEvent: MouseEvent) {
        stageSwitcher.closeStage(mouseEvent)
    }
}