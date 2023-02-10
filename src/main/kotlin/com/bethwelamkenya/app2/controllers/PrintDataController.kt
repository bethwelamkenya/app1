package com.bethwelamkenya.app2.controllers

import com.bethwelamkenya.app2.StageSwitcher
import com.bethwelamkenya.app2.databases.DatabaseAdapter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.GrayColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.input.MouseEvent
import javafx.stage.FileChooser
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList


class PrintDataController : Initializable {
    private val stageSwitcher = StageSwitcher()
    private val adapter = DatabaseAdapter()
    private val corbel: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL, BaseColor.BLACK)
    private val fontItalic: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.ITALIC, BaseColor.BLACK)
    private val fontBold: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD, BaseColor.BLACK)
    private var years = arrayOf("All", "Community", "One", "Two", "Three", "Four", "Five")
    private var selectedColumns = 1
    private var dir = FileChooser()
    private lateinit var desktopDir: File
    private var allTheDates: MutableList<String> = ArrayList()
    private val valueFactory = IntegerSpinnerValueFactory(0, 10, 0, 1)
    private val today: LocalDate = LocalDate.now()

    @FXML
    lateinit var selectYear: ComboBox<String>

    @FXML
    lateinit var startDate: DatePicker

    @FXML
    lateinit var endDate: DatePicker

    @FXML
    lateinit var blankColumns: Spinner<Int>

    @FXML
    lateinit var name: CheckBox

    @FXML
    lateinit var email: CheckBox

    @FXML
    lateinit var mobileNumber: CheckBox

    @FXML
    lateinit var regNo: CheckBox

    @FXML
    lateinit var school: CheckBox

    @FXML
    lateinit var year: CheckBox

    @FXML
    lateinit var department: CheckBox

    @FXML
    lateinit var residence: CheckBox

    @FXML
    lateinit var attendance: CheckBox

    @FXML
    lateinit var noColumns: Label

    @FXML
    lateinit var cancel: Button

    @FXML
    lateinit var print: Button

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        selectYear.items.addAll(years)
        selectYear.value = years[0]
        startDate.value = today
        endDate.value = today
        startDate.isDisable = true
        endDate.isDisable = true
        blankColumns.valueFactory = valueFactory
        attendance.setOnAction {
            if (attendance.isSelected) {
                startDate.isDisable = false
                endDate.isDisable = false
                getColumns(selectedColumns + 1)
            } else {
                startDate.isDisable = true
                endDate.isDisable = true
                getColumns(selectedColumns)
            }
        }
        blankColumns.valueProperty().addListener { _, _, _ ->  getColumns(selectedColumns)}
        name.setOnAction {
            if (name.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        email.setOnAction {
            if (email.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        mobileNumber.setOnAction {
            if (mobileNumber.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        regNo.setOnAction {
            if (regNo.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        school.setOnAction {
            if (school.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        year.setOnAction {
            if (year.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        department.setOnAction {
            if (department.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        residence.setOnAction {
            if (residence.isSelected) {
                selectedColumns++
            } else {
                selectedColumns--
            }
            getColumns(selectedColumns)
        }
        startDate.setOnAction { getNumberOfDays() }
        endDate.setOnAction { getNumberOfDays() }
        getColumns(selectedColumns)
        print.setOnAction {
            try {
                pdfs(it, selectedColumns)
            } catch (e: Exception) {
                stageSwitcher.switchDialogStages("Error", e.toString())
                throw RuntimeException(e)
            }
        }
        cancel.setOnAction { stageSwitcher.closeStage(it) }
    }

    private fun getNumberOfDays() {
        val startingDate = startDate.value
        val endingDate = endDate.value
        val datesList = startingDate.datesUntil(endingDate).toList()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val listOfDatesAsString = datesList.map { it.format(formatter) }
        getColumns(selectedColumns + listOfDatesAsString.size)
    }

    private fun getColumns(selectedColumns: Int) {
        noColumns.text = (selectedColumns + blankColumns.value).toString() + " Columns"
    }

    @Throws(java.lang.Exception::class)
    private fun pdfs(event: Event, selectedColumns: Int) {
        try {
            allTheDates.clear()
            val blankSpaces = blankColumns.value
            val startingDate = startDate.value
            val endingDate = endDate.value
            if (attendance.isSelected) {
                val datesList = startingDate.datesUntil(endingDate).toList()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val listOfDatesAsString = datesList.map { it.format(formatter) }
                allTheDates.addAll(listOfDatesAsString)
            }
            //            dir.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF", "pdf"));

            desktopDir = File(System.getProperty("user.home"))
            dir.initialDirectory = desktopDir
            dir.title = "Choose Directory to save pdf"
            dir.extensionFilters.add(FileChooser.ExtensionFilter("PDF File", "*.pdf"))
            val x = dir.showSaveDialog((event.source as Node).scene.window)
            if (x != null) {
//            step 1: get the members
                val members = when (selectYear.selectionModel.selectedIndex) {
                    1 -> adapter.getMembersByYear(0)
                    2 -> adapter.getMembersByYear(1)
                    3 -> adapter.getMembersByYear(2)
                    4 -> adapter.getMembersByYear(3)
                    5 -> adapter.getMembersByYear(4)
                    6 -> adapter.getMembersByYear(5)
                    else -> adapter.getMembers()
                }
                /* Step-2: Initialize PDF documents - logical objects */
                val printedList = Document()
                PdfWriter.getInstance(printedList, FileOutputStream(x))
                //                PdfWriter.getInstance(printedList, new FileOutputStream(System.getProperty("user.home") + "/Desktop/Printed List.pdf"));
                printedList.open()

                //we have five columns in our table
                val membersList = PdfPTable((selectedColumns + allTheDates.size + blankSpaces + 1) * 2)
                //create a cell object
                var tableCell: PdfPCell
                membersList.widthPercentage = 100f
                membersList.defaultCell.isUseAscender = true
                membersList.defaultCell.isUseDescender = true

                val title = "Repentance And Holiness Ministry Moi University Altar"
                tableCell = PdfPCell(Phrase(title, fontBold))
                tableCell.colspan = ((selectedColumns + allTheDates.size + blankSpaces + 1) * 2)
                tableCell.horizontalAlignment = Element.ALIGN_CENTER
                membersList.addCell(tableCell)

                val years = selectYear.value
                tableCell = PdfPCell(Phrase(years, fontBold))
                tableCell.colspan = ((selectedColumns + allTheDates.size + blankSpaces + 1) * 2)
                tableCell.horizontalAlignment = Element.ALIGN_CENTER
                membersList.addCell(tableCell)
                membersList.defaultCell.backgroundColor = GrayColor(0.75f)

                val a = "no."
                tableCell = PdfPCell(Phrase(a, fontBold))
                tableCell.horizontalAlignment = Element.ALIGN_CENTER
                tableCell.colspan = 1
                membersList.addCell(tableCell)
                if (name.isSelected) {
                    val b = "Name"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 3
                    membersList.addCell(tableCell)
                }
                if (email.isSelected) {
                    val b = "Email"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (mobileNumber.isSelected) {
                    val b = "Number"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (regNo.isSelected) {
                    val b = "Reg No."
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (school.isSelected) {
                    val b = "School"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (year.isSelected) {
                    val b = "Year"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (department.isSelected) {
                    val b = "Department"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                if (residence.isSelected) {
                    val b = "Residence"
                    tableCell = PdfPCell(Phrase(b, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                for (date in allTheDates) {
                    tableCell = PdfPCell(Phrase(date, fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                for (i in 0 until blankSpaces) {
                    tableCell = PdfPCell(Phrase("         ", fontBold))
                    tableCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableCell.colspan = 2
                    membersList.addCell(tableCell)
                }
                membersList.defaultCell.backgroundColor = GrayColor.GRAYWHITE
                var i = 1
                while (members!!.next()) {
                    val id = i.toString()
                    tableCell = PdfPCell(Phrase(id, fontItalic))
                    tableCell.colspan = 1
                    membersList.addCell(tableCell)
                    if (name.isSelected) {
                        val name = members.getString("name")
                        tableCell = PdfPCell(Phrase(name, corbel))
                        tableCell.colspan = 3
                        membersList.addCell(tableCell)
                    }
                    if (email.isSelected) {
                        val email = members.getString("email")
                        tableCell = PdfPCell(Phrase(email, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (mobileNumber.isSelected) {
                        val number = members.getString("number")
                        tableCell = PdfPCell(Phrase(number, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (regNo.isSelected) {
                        val regNo = members.getString("regno")
                        tableCell = PdfPCell(Phrase(regNo, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (school.isSelected) {
                        val school = members.getString("school")
                        tableCell = PdfPCell(Phrase(school, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (year.isSelected) {
                        val year = members.getString("year")
                        tableCell = PdfPCell(Phrase(year, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (department.isSelected) {
                        val department = members.getString("department")
                        tableCell = PdfPCell(Phrase(department, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    if (residence.isSelected) {
                        val residence = members.getString("residence")
                        tableCell = PdfPCell(Phrase(residence, corbel))
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    for (ignored in allTheDates) {
                        tableCell = PdfPCell(Phrase("           ", fontBold))
                        tableCell.horizontalAlignment = Element.ALIGN_CENTER
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    for (no in 0 until blankSpaces) {
                        tableCell = PdfPCell(Phrase("         ", fontBold))
                        tableCell.horizontalAlignment = Element.ALIGN_CENTER
                        tableCell.colspan = 2
                        membersList.addCell(tableCell)
                    }
                    i++
                }
//                Attach report table to PDF
                printedList.add(membersList)
                printedList.close()

//                Close all DB related objects
                members.close()
                stageSwitcher.switchDialogStages("Confirmation", "Document List Printed Successfully To:\n$x")
            }
        } catch (e: FileNotFoundException) {
            stageSwitcher.switchDialogStages("Error", e.toString())
        } catch (e: DocumentException) {
            stageSwitcher.switchDialogStages("Error", e.toString())
        }
    }


    fun goBack(mouseEvent: MouseEvent) {
        stageSwitcher.closeStage(mouseEvent)
    }
}