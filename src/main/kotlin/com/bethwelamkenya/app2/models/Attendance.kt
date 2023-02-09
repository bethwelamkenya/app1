package com.bethwelamkenya.app2.models

import javafx.scene.control.CheckBox

data class Attendance(
    val attendanceId: Long,
    val id: Long,
    val name: String,
    val number: Long,
    val residence: String,
    val date: String,
    val status: CheckBox
)
