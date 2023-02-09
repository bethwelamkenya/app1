package com.bethwelamkenya.app2.models

import java.time.Year

data class Member(
    val id: Long,
    val name: String,
    val email: String,
    val regno: String,
    val number: Long,
    val school: String,
    val year: Int,
    val department: String,
    val residence:  String

)
