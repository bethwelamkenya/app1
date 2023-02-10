package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.bethwelamkenya.app2.models.Attendance
import com.bethwelamkenya.app2.models.ListAttendance
import com.bethwelamkenya.app2.models.Member
import com.bethwelamkenya.app2.models.MemberAttendance
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.URL
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList
import kotlin.system.exitProcess


class AdminHomeController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private var members = FXCollections.observableArrayList<Member>()
    private var listAttendances = FXCollections.observableArrayList<ListAttendance>()
    private var attendances = FXCollections.observableArrayList<Attendance>()
    private var allMembersForAttendance = FXCollections.observableArrayList<Attendance>()
    private var memberAttendances = FXCollections.observableArrayList<MemberAttendance>()
    private var memberAttendancesTemporary = FXCollections.observableArrayList<MemberAttendance>()
    private var allTheDates: MutableList<String> = ArrayList()
    private var otherDates: MutableList<String> = ArrayList()
    private var storedDates: MutableList<String> = ArrayList()
    private var allNames: MutableList<String> = ArrayList()
    private var storedNames: MutableList<String> = ArrayList()
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private lateinit var member: Member
    private var isEditing = false
    private var isAttendanceEditing = false
    private var emailPattern = Regex("[a-zA-Z0-9]+@+[a-zA-Z0-9]+[.]+[a-zA-Z0-9]+$")
    private var mobilNoPattern = Regex("[0-9]*$")
    private var regNoPattern = Regex("[a-zA-Z]+/+[0-9]+/+[0-9]+$")
    private var schools = arrayOf("Engineering", "Education", "Science", "Arts", "Business", "Law", "Medicine", "Aerospace", "Community")
    private var departments = arrayOf("Media", "Keyboardist", "Worshipper", "Usher", "Technician", "Intercessor",
        "Security", "Protocol", "Sanitation", "Violinist", "Pastor", "Bishop", "None")
    private var years = arrayOf("Community", "One", "Two", "Three", "Four", "Five")
    private val today: LocalDate = LocalDate.now()

    @FXML
    lateinit var memberTable: TableView<Member>

    @FXML
    lateinit var attendanceTable: TableView<Attendance>

    @FXML
    lateinit var specificAttendanceTable: TableView<MemberAttendance>

    @FXML
    lateinit var memberName: TableColumn<Member, String>

    @FXML
    lateinit var memberEmail: TableColumn<Member, String>

    @FXML
    lateinit var memberRegNo: TableColumn<Member, String>

    @FXML
    lateinit var memberDepartment: TableColumn<Member, String>

    @FXML
    lateinit var memberResidence: TableColumn<Member, String>

    @FXML
    lateinit var memberSchool: TableColumn<Member, String>

    @FXML
    lateinit var memberId: TableColumn<Member, Long>

    @FXML
    lateinit var memberMobile: TableColumn<Member, Long>

    @FXML
    lateinit var memberYear: TableColumn<Member, Int>

    @FXML
    lateinit var attendanceMemberName: TableColumn<Attendance, String>

    @FXML
    lateinit var attendanceMemberResidence: TableColumn<Attendance, String>

    @FXML
    lateinit var attendanceId: TableColumn<Attendance, Long>

    @FXML
    lateinit var attendanceMemberId: TableColumn<Attendance, Long>

    @FXML
    lateinit var attendanceMemberNumber: TableColumn<Attendance, Long>

    @FXML
    lateinit var attendanceStatus: TableColumn<Attendance, CheckBox>

    @FXML
    lateinit var specificAttendanceID: TableColumn<MemberAttendance, Long>

    @FXML
    lateinit var specificAttendanceDate: TableColumn<MemberAttendance, String>

    @FXML
    lateinit var specificAttendanceStatus: TableColumn<MemberAttendance, CheckBox>

    @FXML
    lateinit var fetchData: Button

    @FXML
    lateinit var refresh: Button

    @FXML
    lateinit var editDetails: Button

    @FXML
    lateinit var clearForm: Button

    @FXML
    lateinit var deleteMember: Button

    @FXML
    lateinit var fetchAttendanceData: Button

    @FXML
    lateinit var refreshAttendance: Button

    @FXML
    lateinit var submitAttendance: Button

    @FXML
    lateinit var printForm: Button

    @FXML
    lateinit var getSpecificAttendance: Button


    @FXML
    lateinit var getSpecificMembers: Button

    @FXML
    lateinit var id: TextField

    @FXML
    lateinit var name: TextField

    @FXML
    lateinit var email: TextField

    @FXML
    lateinit var regno: TextField

    @FXML
    lateinit var mobile: TextField

    @FXML
    lateinit var residence: TextField

    @FXML
    lateinit var school: ComboBox<String>

    @FXML
    lateinit var department: ComboBox<String>

    @FXML
    lateinit var year: ComboBox<String>


    @FXML
    lateinit var attendanceDatePicker: DatePicker

    @FXML
    lateinit var startDate: DatePicker

    @FXML
    lateinit var endDate: DatePicker


    @FXML
    lateinit var attendanceNumber: Label


    @FXML
    lateinit var menuBar: MenuBar


    @FXML
    lateinit var logOut: MenuItem

    @FXML
    lateinit var changePassword: MenuItem

    @FXML
    lateinit var close: MenuItem


    @FXML
    lateinit var addMemberImage: ImageView


    @FXML
    lateinit var specificMembers: ListView<String>


    @FXML
    lateinit var allTime: CheckBox

    @FXML
    lateinit var specified: CheckBox
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        school.items.addAll(schools)
        department.items.addAll(departments)
        year.items.addAll(years)
        school.value = schools[8]
        department.value = departments[12]
        year.value = years[0]
        deleteMember.isDisable = true
        submitAttendance.isDisable = true
        startDate.isDisable = true
        endDate.isDisable = true
        attendanceDatePicker.value = today
        startDate.value = today
        endDate.value = today
        allTime.isSelected = true
        checkBooleans()
        getMembers()
        fetchData.setOnAction { getMembers() }
        refresh.setOnAction { getMembers() }
        editDetails.setOnAction { validateDetails() }
        clearForm.setOnAction { clearMemberForm() }
        deleteMember.setOnAction {
            if (adapter.deleteMember(member.id)){
                stageSwitcher.switchDialogStages("Confirmation", "Member Deleted Successfully")
                isEditing = false
                checkBooleans()
                clearMemberForm()
            } else{
                stageSwitcher.switchDialogStages("Error", "Could Not Delete Member")
            }
        }
        memberTable.setOnMouseClicked {
            if (memberTable.selectionModel.selectedItem != null) {
                member = memberTable.selectionModel.selectedItem
                id.text = member.id.toString()
                name.text = member.name
                email.text = member.email
                regno.text = member.regno
                mobile.text = member.number.toString()
                school.value = member.school
                year.value = years[member.year]
                department.value = member.department
                residence.text = member.residence
                isEditing = true
                checkBooleans()
            }
        }
        printForm.setOnAction { stageSwitcher.switchUtilityStages(it, "print-data.fxml", "Print Data", true) }
        fetchAttendanceData.setOnAction { fetchTheAttendances() }
        refreshAttendance.setOnAction { fetchTheAttendances() }
        submitAttendance.setOnAction {
            if (isAttendanceEditing){
                updateAttendance()
            } else{
                saveAttendance()
            }
        }
        getSpecificMembers.setOnAction { getSpecificMembers() }
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
        getSpecificAttendance.setOnAction { fetchSpecificAttendance() }
        changePassword.setOnAction { stageSwitcher.switchUtilityStagesFromMenu("reset-password.fxml", "Change Password", false, menuBar) }
        logOut.setOnAction { stageSwitcher.switchStagesFromMenu("hello-view.fxml", "Log In", false, menuBar) }
        close.setOnAction { exitProcess(0) }
    }

    private fun fetchTheAttendances() {
        if (attendanceDatePicker.value == null) {
            stageSwitcher.switchDialogStages("Error", "Please select A Date To Continue")
        } else {
            println("fetching data")
            if (!adapter.dateExists(attendanceDatePicker.value.toString())) {
                isAttendanceEditing = false
                getAttendance()
            } else {
                isAttendanceEditing = true
                getExistingAttendance()
            }
        }
    }

    private fun checkBooleans() {
        if (isEditing) {
            editDetails.text = "Edit Member"
            addMemberImage.image = Image(javaClass.getResourceAsStream("/com/bethwelamkenya/app2/assets/edit_user_filled_500px.png"))
            deleteMember.isDisable = false
        } else {
            editDetails.text = "Add Member"
            addMemberImage.image = Image(javaClass.getResourceAsStream("/com/bethwelamkenya/app2/assets/add_user_filled_500px.png"))
            deleteMember.isDisable = true
        }
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
        if (regno.text.isEmpty()) {
            checkMobile()
        } else {
            if (regno.text.matches(regNoPattern)) {
                checkMobile()
            } else {
                stageSwitcher.switchDialogStages("Error", "Please Fill in the Correct Registration Number Pattern")
            }
        }
    }

    private fun checkMobile() {
        if (mobile.text.isEmpty()) {
            if (isEditing) {
                editMemberDetails()
            } else {
                addMemberDetails()
            }
        } else {
            if (mobile.text.matches(mobilNoPattern)) {
                if (isEditing) {
                    editMemberDetails()
                } else {
                    addMemberDetails()
                }
            } else {
                stageSwitcher.switchDialogStages("Error", "Please Fill in the Correct Mobile Number Pattern")
            }
        }
    }

    private fun editMemberDetails() {
        val myId = id.text.toLong()
        val myName = name.text
        val myEmail = email.text
        val myRegNo = regno.text
        var myMobile: Long = 0
        if (mobile.text.isNotEmpty()) {
            myMobile = mobile.text.toLong()
        }
        val mySchool = school.selectionModel.selectedItem
        val myYear = year.selectionModel.selectedIndex
        val myDepartment = department.selectionModel.selectedItem
        val myResidence = residence.text
        if (adapter.updateMember(
                myId,
                myName,
                myEmail,
                myRegNo,
                myMobile,
                mySchool,
                myYear,
                myDepartment,
                myResidence
            )
        ) {
            stageSwitcher.switchDialogStages("Error", "We Could Not Update The Member. Please Try Again Later!!!")
        } else {
            stageSwitcher.switchDialogStages("Confirmation", "Member Updated Successfully")
            getMembers()
            clearMemberForm()
        }
    }

    private fun addMemberDetails() {
        val myName = name.text
        val myEmail = email.text
        val myRegNo = regno.text
        var myMobile: Long = 0
        if (mobile.text.isNotEmpty()) {
            myMobile = mobile.text.toLong()
        }
        val mySchool = school.selectionModel.selectedItem
        val myYear = year.selectionModel.selectedIndex
        val myDepartment = department.selectionModel.selectedItem
        val myResidence = residence.text
        if (!adapter.memberExists(myName)) {
            if (adapter.insertMember(myName, myEmail, myRegNo, myMobile, mySchool, myYear, myDepartment, myResidence)) {
                stageSwitcher.switchDialogStages("Error", "We Could Not Add Member. Please Try Again Later!!!")
            } else {
                stageSwitcher.switchDialogStages("Confirmation", "Member Created Successfully")
                getMembers()
                clearMemberForm()
            }
        } else {
            stageSwitcher.switchDialogStages("Error", "A Member With The Name: $myName :Already Exists")
        }
    }

    private fun clearMemberForm() {
        id.text = ""
        name.text = ""
        email.text = ""
        regno.text = ""
        mobile.text = ""
        school.value = schools[8]
        year.value = years[0]
        department.value = departments[12]
        residence.text = ""
        isEditing = false
        checkBooleans()
    }

    private fun getMembers() {
        members.clear()
        val resultSet = adapter.getMembers()
        while (resultSet!!.next()) {
            members.add(
                Member(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("regno"),
                    resultSet.getLong("number"),
                    resultSet.getString("school"),
                    resultSet.getInt("year"),
                    resultSet.getString("department"),
                    resultSet.getString("residence")
                )
            )
        }
        memberTable.items = members
        memberId.cellValueFactory = PropertyValueFactory("id")
        memberName.cellValueFactory = PropertyValueFactory("name")
        memberEmail.cellValueFactory = PropertyValueFactory("email")
        memberRegNo.cellValueFactory = PropertyValueFactory("regno")
        memberMobile.cellValueFactory = PropertyValueFactory("number")
        memberSchool.cellValueFactory = PropertyValueFactory("school")
        memberYear.cellValueFactory = PropertyValueFactory("year")
        memberDepartment.cellValueFactory = PropertyValueFactory("department")
        memberResidence.cellValueFactory = PropertyValueFactory("residence")
    }

    private fun getAttendance() {
        attendances.clear()
        val resultSet = adapter.getMembers()
        while (resultSet!!.next()) {
            val box = CheckBox()
            box.isSelected = false
            attendances.add(
                Attendance(
                    resultSet.getLong("id"), resultSet.getString("name"), resultSet.getLong("number"),
                    resultSet.getString("residence"), attendanceDatePicker.value.toString(), box
                )
            )
        }
        attendanceTable.items = attendances
        attendanceMemberId.cellValueFactory = PropertyValueFactory("id")
        attendanceMemberName.cellValueFactory = PropertyValueFactory("name")
        attendanceMemberNumber.cellValueFactory = PropertyValueFactory("number")
        attendanceMemberResidence.cellValueFactory = PropertyValueFactory("residence")
        attendanceStatus.cellValueFactory = PropertyValueFactory("status")
//        attendanceTable.items.removeAt(0)
        attendanceNumber.text = attendances.size.toString() + " Members"
        submitAttendance.isDisable = false
        isAttendanceEditing = false
    }

    private fun getExistingAttendance() {
        members.clear()
        attendances.clear()
        allMembersForAttendance.clear()
        listAttendances.clear()
        storedNames.clear()
        allNames.clear()
        val myDate = attendanceDatePicker.value.toString()
//        get the members who exist in attendances databases into a list called "listattendances"
        val attendanceSet = adapter.getAttendancesByDate(myDate)
        val membersSet = adapter.getMembers()
        while (attendanceSet!!.next()) {
            val box = CheckBox()
            box.isSelected = attendanceSet.getInt("status") != 0
            listAttendances.add(
                ListAttendance(
                    attendanceSet.getLong("id"),
                    attendanceSet.getLong("memberid"),
                    attendanceSet.getString("membername"),
                    attendanceSet.getString("date"),
                    box
                )
            )
            storedNames.add(attendanceSet.getString("membername"))
            val myMember = adapter.getMember(attendanceSet.getString("membername"))
            if (myMember != null) {
                attendances.add(
                    Attendance(
                        attendanceSet.getLong("id"),
                        myMember.id,
                        myMember.name,
                        myMember.number,
                        myMember.residence,
                        attendanceSet.getString("date"),
                        box
                    )
                )
            }
        }

//            get all the members into a list called "allMembersForAttendance" and put the member names into a list called "allNames"
        while (membersSet!!.next()) {
            val box = CheckBox()
            box.isSelected = false
            allMembersForAttendance.add(
                Attendance(
                    membersSet.getLong("id"),
                    membersSet.getString("name"),
                    membersSet.getLong("number"),
                    membersSet.getString("residence"),
                    myDate,
                    box
                )
            )
            allNames.add(membersSet.getString("name"))
        }

//            remove the names in "storedNames" from "allNames"
        allNames.removeAll(storedNames)

//                add these other members to the "attendances" list
        for (name in allNames) {
            val myMember = adapter.getMember(name)
            if (myMember != null) {
                val box = CheckBox()
                box.isSelected = false
                attendances.add(
                    Attendance(
                        myMember.id,
                        myMember.name,
                        myMember.number,
                        myMember.residence,
                        myDate,
                        box
                    )
                )
            }
        }
        attendanceTable.items = attendances
        attendanceId.cellValueFactory = PropertyValueFactory("attendanceid")
        attendanceMemberId.cellValueFactory = PropertyValueFactory("id")
        attendanceMemberName.cellValueFactory = PropertyValueFactory("name")
        attendanceMemberNumber.cellValueFactory = PropertyValueFactory("number")
        attendanceMemberResidence.cellValueFactory = PropertyValueFactory("residence")
        attendanceStatus.cellValueFactory = PropertyValueFactory("status")
        if (attendanceTable.items.size != 0) {
//            attendanceTable.items.removeAt(0)
            attendanceNumber.text = attendances.size.toString() + " Members"
        }
        submitAttendance.isDisable = false
        isAttendanceEditing = true
    }

    private fun saveAttendance() {
        var status: Int
//            insert each member details into the "attendance" table in the database
        for (i in attendanceTable.items.indices) {
            status = if (attendanceStatus.getCellObservableValue(i).value.isSelected) {
                1
            } else {
                0
            }
            adapter.insertAttendance(
                attendanceMemberId.getCellObservableValue(i).value,
                attendanceMemberName.getCellObservableValue(i).value,
                attendanceDatePicker.value.toString(), status
            )
        }
        adapter.insertDate(attendanceDatePicker.value.toString())
        stageSwitcher.switchDialogStages("Confirmation", "Attendance Submitted")
    }

    private fun updateAttendance() {
        storedNames.clear()
        listAttendances.clear()
        val attendanceSet = adapter.getAttendancesByDate(attendanceDatePicker.value.toString())
        while (attendanceSet!!.next()) {
            val box = CheckBox()
            box.isSelected = attendanceSet.getInt("status") != 0
            listAttendances.add(ListAttendance(
                    attendanceSet.getLong("id"),
                    attendanceSet.getLong("memberid"),
                    attendanceSet.getString("membername"),
                    attendanceSet.getString("date"),
                    box)
            )
//            println(attendanceSet.getString("membername"))
            storedNames.add(attendanceSet.getString("membername").lowercase(Locale.getDefault()))
//            println(storedNames)
        }

        //            things to do for every row in the attendances table
        for (i in attendanceTable.items.indices) {
            //                if the checkbox is checked, status is 1 else the status is 0
            val status = if (attendanceStatus.getCellObservableValue(i).value.isSelected) {
                1
            } else {
                0
            }
//                if the member's name is in the "storedNames" list, update them otherwise create a new entry
            if (storedNames.contains(attendanceMemberName.getCellObservableValue(i).value.lowercase(Locale.getDefault()))) {
                adapter.updateAttendance(
                    attendanceId.getCellObservableValue(i).value,
                    attendanceMemberId.getCellObservableValue(i).value,
                    attendanceMemberName.getCellObservableValue(i).value,
                    attendanceDatePicker.value.toString(), status
                )
            } else {
                adapter.insertAttendance(
                    attendanceMemberId.getCellObservableValue(i).value,
                    attendanceMemberName.getCellObservableValue(i).value,
                    attendanceDatePicker.value.toString(), status
                )
            }
        }
        stageSwitcher.switchDialogStages("Confirmation", "Attendance Submitted")
    }

    private fun getSpecificMembers() {
        specificMembers.items.clear()
        val name: MutableList<String> = ArrayList()
        val resultSet = adapter.getMembers()
        while (resultSet!!.next()) {
            name.add(resultSet.getString("name"))
        }
        val names = arrayOfNulls<String>(name.size)
        for ((i, theName) in name.withIndex()) {
            names[i] = theName
        }
        specificMembers.items.addAll(*names)
    }

    private fun fetchSpecificAttendance() {
        memberAttendances.clear()
        memberAttendancesTemporary.clear()
        allTheDates.clear()
        otherDates.clear()
        storedDates.clear()
        val theName = specificMembers.selectionModel.selectedItem
        val startingDate = startDate.value
        val endingDate = endDate.value
        if (theName != null) {
            val resultSet = adapter.getAttendancesByName(theName)
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
        } else {
            stageSwitcher.switchDialogStages("Error", "Please select A member to continue")
        }
    }

    private fun fillTable() {
        specificAttendanceTable.items = memberAttendances
        specificAttendanceID.cellValueFactory = PropertyValueFactory("id")
        specificAttendanceDate.cellValueFactory = PropertyValueFactory("date")
        specificAttendanceStatus.cellValueFactory = PropertyValueFactory("status")
    }


}