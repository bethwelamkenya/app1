package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.PassData
import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.bethwelamkenya.app2.models.Member
import com.bethwelamkenya.app2.models.MemberAttendance
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import java.net.URL
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList
import kotlin.system.exitProcess


class MemberHomeController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private var emailPattern = Regex("[a-zA-Z0-9]+@+[a-zA-Z0-9]+[.]+[a-zA-Z0-9]+$")
    private var mobilNoPattern = Regex("[0-9]*$")
    private var regNoPattern = Regex("[a-zA-Z]+/+[0-9]+/+[0-9]+$")
    private var schools = arrayOf("Engineering", "Education", "Science", "Arts", "Business", "Law", "Medicine", "Aerospace", "Community")
    private var departments = arrayOf(
        "Media", "Keyboardist", "Worshipper", "Usher", "Technician", "Intercessor",
        "Security", "Protocol", "Sanitation", "Violinist", "Pastor", "Bishop", "None"
    )
    private var years = arrayOf("Community", "One", "Two", "Three", "Four", "Five")
    private lateinit var member: Member
    private var adapter = DatabaseAdapter()
    private var allTheDates: MutableList<String> = ArrayList()
    private var otherDates: MutableList<String> = ArrayList()
    private var storedDates: MutableList<String> = ArrayList()
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var memberAttendances = FXCollections.observableArrayList<MemberAttendance>()
    private var memberAttendancesTemporary = FXCollections.observableArrayList<MemberAttendance>()
    private val today: LocalDate = LocalDate.now()

    @FXML
    lateinit var allTime: CheckBox

    @FXML
    lateinit var specified: CheckBox

    @FXML
    lateinit var startDate: DatePicker

    @FXML
    lateinit var endDate: DatePicker

    @FXML
    lateinit var name: TextField

    @FXML
    lateinit var email: TextField

    @FXML
    lateinit var regNo: TextField

    @FXML
    lateinit var number: TextField

    @FXML
    lateinit var residence: TextField

    @FXML
    lateinit var school: ComboBox<String>

    @FXML
    lateinit var year: ComboBox<String>

    @FXML
    lateinit var department: ComboBox<String>

    @FXML
    lateinit var updateDetails: Button

    @FXML
    lateinit var printDetails: Button

    @FXML
    lateinit var getData: Button

    @FXML
    lateinit var menuBar: MenuBar

    @FXML
    lateinit var logOut: MenuItem

    @FXML
    lateinit var close: MenuItem

    @FXML
    lateinit var attendanceTable: TableView<MemberAttendance>

    @FXML
    lateinit var attendanceID: TableColumn<MemberAttendance, Long>

    @FXML
    lateinit var attendanceDate: TableColumn<MemberAttendance, String>

    @FXML
    lateinit var attendanceStatus: TableColumn<MemberAttendance, CheckBox>

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        school.items.addAll(schools)
        year.items.addAll(years)
        department.items.addAll(departments)
        member = PassData.passedMember!!
        name.text = member.name
        email.text = member.email
        regNo.text = member.regno
        number.text = member.number.toString()
        school.value = member.school
        year.value = years[member.year]
        department.value = member.department
        residence.text = member.residence

        startDate.value = today
        endDate.value = today
        startDate.isDisable = true
        endDate.isDisable = true
        allTime.isSelected = true
        updateDetails.setOnAction { validateDetails() }
        allTime.setOnAction {
            if (allTime.isSelected) {
                specified.isSelected = false
                startDate.isDisable = true
                endDate.isDisable = true
            } else {
                specified.isSelected = true
                startDate.isDisable = false
                endDate.isDisable = false
            }
        }
        specified.setOnAction {
            if (specified.isSelected) {
                allTime.isSelected = false
                startDate.isDisable = false
                endDate.isDisable = false
            } else {
                allTime.isSelected = true
                endDate.isDisable = true
                startDate.isDisable = true
            }
        }
        getData.setOnAction { getAttendanceData() }
        logOut.setOnAction { stageSwitcher.switchStagesFromMenu("hello-view.fxml", "Log In", false, menuBar) }
        close.setOnAction { exitProcess(0) }
    }

    private fun validateDetails() {
        if (name.text.isEmpty()) {
            stageSwitcher.switchDialogStages("Error", "Please Fill In The Name")
        } else {
            checkEmail()
        }
    }

    private fun checkEmail() {
        if (email.text.isEmpty()) {
            checkRegNo()
        } else {
            if (email.text.matches(emailPattern)) {
                checkRegNo()
            } else {
                stageSwitcher.switchDialogStages("Error", "Please Fill in the Correct Email Pattern")
            }
        }
    }

    private fun checkRegNo() {
        if (regNo.text.isEmpty()) {
            checkMobile()
        } else {
            if (regNo.text.matches(regNoPattern)) {
                checkMobile()
            } else {
                stageSwitcher.switchDialogStages("Error", "Please Fill in the Correct Registration Number Pattern")
            }
        }

    }

    private fun checkMobile() {
        if (number.text.isEmpty()) {
            editMemberDetails()
        } else {
            if (number.text.matches(mobilNoPattern)) {
                editMemberDetails()
            } else {
                stageSwitcher.switchDialogStages("Error", "Please Fill in the Correct Mobile Number Pattern")
            }
        }

    }

    private fun editMemberDetails() {
        var num: Long = 0
        if (number.text.isNotEmpty()) {
            num = number.text.toLong()
        }
        if (!adapter.updateMember(
                member.id, member.name, email.text, regNo.text, num, school.value,
                year.selectionModel.selectedIndex, department.value, residence.text
            )
        ) {
            stageSwitcher.switchDialogStages("Confirmation", "Successfully Updated Your Details")
            member = adapter.getMember(name.text)!!
        } else {
            stageSwitcher.switchDialogStages("Error", "Could Not Update Your Details")
        }

    }

    private fun getAttendanceData() {
        memberAttendances.clear()
        memberAttendancesTemporary.clear()
        allTheDates.clear()
        otherDates.clear()
        storedDates.clear()
        val startingDate = startDate.value
        val endingDate = endDate.value
        val resultSet = adapter.getAttendancesByName(member.name)
        if (allTime.isSelected) {
            var i = 1
            while (resultSet!!.next()) {
                val box = CheckBox()
                box.isDisable = true
                box.isSelected = resultSet.getInt("status") != 0
                memberAttendances.add(
                    MemberAttendance(
                        i.toLong(),
                        dateFormat.format(Date.valueOf(resultSet.getString("date"))),
                        box
                    )
                )
                i++
            }
        } else {
            if (startingDate != null && endingDate != null) {
                val datesList = startingDate.datesUntil(endingDate).toList()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val listOfDatesAsString = datesList.map { it.format(formatter) }
                allTheDates.addAll(listOfDatesAsString)
                otherDates.addAll(listOfDatesAsString)
                var i = 1
                while (resultSet!!.next()) {
                    val checkBox = CheckBox()
                    checkBox.isDisable = true
                    checkBox.isSelected = resultSet.getInt("status") != 0
                    memberAttendancesTemporary.add(
                        MemberAttendance(
                            i.toLong(),
                            dateFormat.format(Date.valueOf(resultSet.getString("date"))),
                            checkBox
                        )
                    )
                    storedDates.add(dateFormat.format(Date.valueOf(resultSet.getString("date"))))
                    i++
                }
                var j = 1
                for (date in allTheDates) {
                    if (storedDates.contains(date)) {
                        val memNo = storedDates.indexOf(date)
                        val memberAttendance = memberAttendancesTemporary[memNo]
                        memberAttendances.add(MemberAttendance(j.toLong(), date, memberAttendance.status))
                    } else {
                        val checkBox = CheckBox()
                        checkBox.isSelected = false
                        checkBox.isDisable = true
                        memberAttendances.add(MemberAttendance(j.toLong(), date, checkBox))
                    }
                    j++
                }
            } else {
                stageSwitcher.switchDialogStages("Error", "Start date or end date is not selected.")
            }
        }
        fillTable()
    }


    private fun fillTable() {
        attendanceTable.items = memberAttendances
        attendanceID.cellValueFactory = PropertyValueFactory("id")
        attendanceDate.cellValueFactory = PropertyValueFactory("date")
        attendanceStatus.cellValueFactory = PropertyValueFactory("status")
    }

    fun goBack(mouseEvent: MouseEvent) {
        stageSwitcher.switchStages(mouseEvent, "hello-view.fxml", "Log In", false)
    }
}