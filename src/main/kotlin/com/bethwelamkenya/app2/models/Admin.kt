package com.bethwelamkenya.app2.models

data class Admin(
    val id: Long,
    val name: String,
    val email: String,
    val number: Long,
    val userName: String,
    val password: String,
    val security: String,
    val answer: String
)
