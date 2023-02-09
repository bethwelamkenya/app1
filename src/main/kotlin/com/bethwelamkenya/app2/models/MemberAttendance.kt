package com.bethwelamkenya.app2.models

import javafx.scene.control.CheckBox

data class MemberAttendance(
    val id: Long,
    val date: String,
    val status: CheckBox
)
