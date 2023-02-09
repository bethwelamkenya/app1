package com.bethwelamkenya.app2.databases

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnector {
    private val sqlConnection: String = "jdbc:sqlite:" + System.getProperty("user.home") + "/AppData/Roaming/App2/Databases/church.sqlite"
//    private final val SQL_CONN: String = "jdbc:mysql://localhost:3306/church?useSSL=false"
    private val userName: String = "root"
    private val password: String = "9852"
    //            Class.forName("com.mysql.jdbc.driver")
//            return DriverManager.getConnection(sqlConnection, userName, password)
    fun getConnection() : Connection {
        try {
            val sqlConnection: String = "jdbc:sqlite:" + System.getProperty("user.home") + "/AppData/Roaming/App2/Databases/church.sqlite"
            Class.forName("org.sqlite.JDBC")
            return DriverManager.getConnection(sqlConnection)

        } catch (e : SQLException){
            throw RuntimeException(e)
        }
    }
}