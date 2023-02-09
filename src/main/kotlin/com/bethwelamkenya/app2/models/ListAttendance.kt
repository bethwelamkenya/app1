package com.bethwelamkenya.app2.models

import javafx.scene.control.CheckBox

data class ListAttendance(
    val attendanceId: Long,
    val id: Long,
    val name: String,
    val date: String,
    val status: CheckBox
)
