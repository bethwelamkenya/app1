package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.PassData
import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.bethwelamkenya.app2.models.Admin
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class DeveloperHomeController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private var admins: ObservableList<Admin> = FXCollections.observableArrayList()
    private lateinit var admin: Admin


    @FXML
    lateinit var addNew: Button

    @FXML
    lateinit var editDetails: Button

    @FXML
    lateinit var refresh: Button

    @FXML
    lateinit var done: Button

    @FXML
    lateinit var fetchAdmins: Button

    @FXML
    lateinit var deleteAdmin: Button

    @FXML
    lateinit var menuBar: MenuBar

    @FXML
    lateinit var logOut: MenuItem

    @FXML
    lateinit var close: MenuItem

    @FXML
    lateinit var adminTable: TableView<Admin>

    @FXML
    lateinit var adminName: TableColumn<Admin, String>

    @FXML
    lateinit var adminEmail: TableColumn<Admin, String>

    @FXML
    lateinit var adminUserName: TableColumn<Admin, String>

    @FXML
    lateinit var adminId: TableColumn<Admin, Long>

    @FXML
    lateinit var adminMobile: TableColumn<Admin, Long>
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        editDetails.isDisable = true
        deleteAdmin.isDisable = true
        getAdmin()
        addNew.setOnAction { stageSwitcher.switchUtilityStages(it, "add-admin.fxml", "Add Admin", false) }
        editDetails.setOnAction {
            if (adminTable.selectionModel.selectedItem != null) {
                PassData.admin = admin
                stageSwitcher.switchUtilityStages(it, "edit-admin.fxml", "Edit Admin", false)
            }
        }
        done.setOnAction { stageSwitcher.switchStages(it, "hello-view.fxml", "Log In", false) }
        fetchAdmins.setOnAction { getAdmin() }
        refresh.setOnAction { getAdmin() }
        adminTable.setOnMouseClicked {
            if (adminTable.selectionModel.selectedItem != null) {
                admin = adminTable.selectionModel.selectedItem
                editDetails.isDisable = false
                deleteAdmin.isDisable = false
            }
        }
        deleteAdmin.setOnAction {
            if (adminTable.selectionModel.selectedItem != null) {
                deleteAdmin()

            }
        }
        logOut.setOnAction { stageSwitcher.switchStagesFromMenu("hello-view.fxml", "Log In", false, menuBar) }
        close.setOnAction { exitProcess(0) }
    }

    private fun getAdmin() {
        admins.clear()
        val resultSet = adapter.getAdmins()
        while (resultSet!!.next()) {
            admins.add(
                Admin(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getLong("number"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("security"),
                    resultSet.getString("answer")
                )
            )
        }
        adminTable.items = admins
        adminId.cellValueFactory = PropertyValueFactory("id")
        adminName.cellValueFactory = PropertyValueFactory("name")
        adminEmail.cellValueFactory = PropertyValueFactory("email")
        adminMobile.cellValueFactory = PropertyValueFactory("number")
        adminUserName.cellValueFactory = PropertyValueFactory("username")
        deleteAdmin.isDisable = false
        editDetails.isDisable = false
    }

    private fun deleteAdmin() {
        if (adapter.deleteAdmin(admin.id)) {
            stageSwitcher.switchDialogStages("Error", "Could Not Delete Admin. Please Try Again Later")
        } else {
            stageSwitcher.switchDialogStages("Confirmation", "Admin Deleted Successfully")
        }
        getAdmin()
    }

    fun goBack(mouseEvent: MouseEvent) {
        stageSwitcher.switchStages(mouseEvent, "hello-view.fxml", "Log In", false)
    }
}