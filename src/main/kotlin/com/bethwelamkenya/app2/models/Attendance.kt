package com.bethwelamkenya.app2.models

import javafx.scene.control.CheckBox

data class Attendance(
    val attendanceid: Long,
    val id: Long,
    val name: String,
    val number: Long,
    val residence: String,
    val date: String,
    val status: CheckBox
) {
    constructor(
        id: Long,
        name: String,
        number: Long,
        residence: String,
        date: String,
        status: CheckBox
    ) : this(0, id, name, number, residence, date, status)
}
