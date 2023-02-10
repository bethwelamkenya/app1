package com.bethwelamkenya.app2.databases

import com.bethwelamkenya.app2.models.Admin
import com.bethwelamkenya.app2.models.Member
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.*

class DatabaseAdapter {
    private val databaseConnector: DatabaseConnector = DatabaseConnector()
    private var connection: Connection = databaseConnector.getConnection()
    private lateinit var preparedStatement: PreparedStatement
    private lateinit var resultSet: ResultSet

    fun isDatabaseConnected(): Boolean {
        return connection.isValid(2)
    }

    private fun createTables(query: String) {
        try {
            val statement: Statement = connection.createStatement()
            statement.executeUpdate(query)
        } catch (e: SQLException) {
            println(e)
//            throw RuntimeException(e)
        }
    }

    fun memberTable() {
        val memberTable = ("CREATE TABLE member (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT UNIQUE, "
                + "email TEXT, "
                + "number INTEGER, "
                + "regno TEXT, "
                + "school TEXT, "
                + "year INTEGER, "
                + "department TEXT, "
                + "residence TEXT, "
                + "UNIQUE (name))")
        createTables(memberTable)
    }

    fun adminTable() {
        val adminTable = ("CREATE TABLE admin (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "email TEXT, "
                + "number INTEGER, "
                + "username VARCHAR (200) UNIQUE, "
                + "password TEXT, "
                + "security TEXT, "
                + "answer TEXT, "
                + "UNIQUE (username))")
        createTables(adminTable)
    }

    fun attendaceTable() {
        val attendancesTable = ("CREATE TABLE attendance (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "memberid INTEGER, "
                + "membername TEXT, " /* REFERENCES members (name), "*/
                + "date TEXT, "
                + "status INTEGER)")
        createTables(attendancesTable)
    }

    fun dateTable() {
        val dateTable = ("CREATE TABLE date (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT)")
        createTables(dateTable)
    }

    fun logIn(userName: String, password: String): Boolean {
        return try {
            val select = "SELECT * FROM admin WHERE username = ? AND password = ?"
            preparedStatement = connection.prepareStatement(select)
            preparedStatement.setString(1, userName)
            preparedStatement.setString(2, sha256(password))
            resultSet = preparedStatement.executeQuery()
            return resultSet.next()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
            if(preparedStatement != null){
                preparedStatement.close()
            }
            if (resultSet !=null){
                resultSet.close()
            }
//            connection.close()
//            connection.endRequest()
        }
    }

    fun insertMember(
        name: String, email: String?, regNo: String?, number: Long,
        school: String?, year: Int, department: String?, residence: String?
    ): Boolean {
        return try {
            val membersInsert = "INSERT INTO member ( name , email, regno, number, school, year, department, residence) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(membersInsert)
            preparedStatement.setString(1, name)
            preparedStatement.setString(2, email)
            preparedStatement.setString(3, regNo)
            preparedStatement.setLong(4, number)
            preparedStatement.setString(5, school)
            preparedStatement.setInt(6, year)
            preparedStatement.setString(7, department)
            preparedStatement.setString(8, residence)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
            if(preparedStatement != null){
                preparedStatement.close()
            }
            if (resultSet !=null){
                resultSet.close()
            }
//            connection.close()
//            connection.endRequest()
        }
    }

    fun insertAdmin(
        name: String, email: String?, number: Long, userName: String,
        password: String, security: String, answer: String
    ): Boolean {
        return try {
            val adminInsert =
                "INSERT INTO admin ( name, email, number, username, password, security, answer) VALUES ( ?, ?, ?, ?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(adminInsert)
            preparedStatement.setString(1, name)
            preparedStatement.setString(2, email)
            preparedStatement.setLong(3, number)
            preparedStatement.setString(4, userName)
            preparedStatement.setString(5, sha256(password))
            preparedStatement.setString(6, security)
            preparedStatement.setString(7, sha256(answer))
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }


    fun insertAttendance(memberID: Long, memberName: String, date: String, status: Int): Boolean {
        return try {
            val attendanceInsert = "INSERT INTO attendance ( memberid, membername, date, status) VALUES ( ?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(attendanceInsert)
            preparedStatement.setLong(1, memberID)
            preparedStatement.setString(2, memberName)
            preparedStatement.setString(3, date)
            preparedStatement.setInt(4, status)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun insertDate(date: String?): Boolean {
        return try {
            val dateInsert = "INSERT INTO date ( date) VALUES ( ?)"
            preparedStatement = connection.prepareStatement(dateInsert)
            preparedStatement.setString(1, date)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun updateMember(
        id: Long, name: String, email: String?, regNo: String?,
        number: Long, school: String?, year: Int, department: String?, residence: String?
    ): Boolean {
        return try {
            val update =
                "UPDATE member SET name=?, email=?, regno=?, number=?, school=?, year=?, department=?, residence=? WHERE id=? "
            preparedStatement = connection.prepareStatement(update)
            preparedStatement.setString(1, name)
            preparedStatement.setString(2, email)
            preparedStatement.setString(3, regNo)
            preparedStatement.setLong(4, number)
            preparedStatement.setString(5, school)
            preparedStatement.setInt(6, year)
            preparedStatement.setString(7, department)
            preparedStatement.setString(8, residence)
            preparedStatement.setLong(9, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun updateAdmin(
        id: Long, name: String, email: String?, number: Long,
        userName: String, password: String, security: String, answer: String
    ): Boolean {
        return try {
            val adminUpdate =
                "UPDATE admin SET name=?, email=?, number=?, username=?, password=?, security=?, answer=? WHERE id=? "
            preparedStatement = connection.prepareStatement(adminUpdate)
            preparedStatement.setString(1, name)
            preparedStatement.setString(2, email)
            preparedStatement.setLong(3, number)
            preparedStatement.setString(4, userName)
            preparedStatement.setString(5, sha256(password))
            preparedStatement.setString(6, security)
            preparedStatement.setString(7, sha256(answer))
            preparedStatement.setLong(8, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun updateAttendance(
        id: Long, memberID: Long, memberName: String?, date: String?,
        status: Int
    ): Boolean {
        return try {
            val attendanceUpdate =
                "UPDATE attendance SET memberid=?, membername=?, date=?, status=? WHERE id=? "
            preparedStatement = connection.prepareStatement(attendanceUpdate)
            preparedStatement.setLong(1, memberID)
            preparedStatement.setString(2, memberName)
            preparedStatement.setString(3, date)
            preparedStatement.setInt(4, status)
            preparedStatement.setLong(5, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun updateDate(id: Long, date: String?): Boolean {
        return try {
            val dateUpdate = "UPDATE date SET date=? WHERE id=? "
            preparedStatement = connection.prepareStatement(dateUpdate)
            preparedStatement.setString(1, date)
            preparedStatement.setLong(2, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun deleteMember(id: Long): Boolean {
        return try {
            val delete = "DELETE FROM member WHERE id = ?"
            preparedStatement = connection.prepareStatement(delete)
            preparedStatement.setLong(1, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun deleteAdmin(id: Long): Boolean {
        return try {
            val adminDelete = "DELETE FROM admin WHERE id = ?"
            preparedStatement = connection.prepareStatement(adminDelete)
            preparedStatement.setLong(1, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun deleteAttendance(id: Long): Boolean {
        return try {
            val attendanceDelete = "DELETE FROM attendance WHERE id = ?"
            preparedStatement = connection.prepareStatement(attendanceDelete)
            preparedStatement.setLong(1, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun deleteDate(id: Long): Boolean {
        return try {
            val dateDelete = "DELETE FROM date WHERE id = ?"
            preparedStatement = connection.prepareStatement(dateDelete)
            preparedStatement.setLong(1, id)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun getMembers(): ResultSet? {
        val memberQuery = "SELECT * FROM member"
        return connection.createStatement().executeQuery(memberQuery)
    }

    fun getAdmins(): ResultSet? {
        val adminQuery = "SELECT * FROM admin"
        return connection.createStatement().executeQuery(adminQuery)
    }

    fun getAttendances(): ResultSet? {
        val attendanceQuery = "SELECT * FROM attendance"
        return connection.createStatement().executeQuery(attendanceQuery)
    }

    fun getDates(): ResultSet? {
        val dateQuery = "SELECT * FROM date"
        return connection.createStatement().executeQuery(dateQuery)
    }

    fun getMember(id: Long): Member? {
        return try {
            val selectMember = "SELECT * FROM members WHERE id = ?"
            preparedStatement = connection.prepareStatement(selectMember)
            preparedStatement.setLong(1, id)
            resultSet = preparedStatement.executeQuery()
            val member = Member(
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
            member
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun getMember(name: String): Member? {
        return try {
            val selectMember = "SELECT * FROM member WHERE name LIKE ?"
            preparedStatement = connection.prepareStatement(selectMember)
            preparedStatement.setString(1, name)
            resultSet = preparedStatement.executeQuery()
            val member = Member(
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
            member
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
            connection.endRequest()
//            connection.close()
        }
    }

    fun getAdmin(id: Long): Admin? {
        return try {
            val selectAdmin = "SELECT * FROM admin WHERE id = ?"
            preparedStatement = connection.prepareStatement(selectAdmin)
            preparedStatement.setLong(1, id)
            resultSet = preparedStatement.executeQuery()
            val admin = Admin(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getLong("mobileno"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("security"),
                resultSet.getString("answer")
            )
            admin
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun getAdmin(userName: String): Admin? {
        return try {
            val selectAdmin = "SELECT * FROM admin WHERE username = ?"
            preparedStatement = connection.prepareStatement(selectAdmin)
            preparedStatement.setString(1, userName)
            resultSet = preparedStatement.executeQuery()
            val admin = Admin(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getLong("number"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("security"),
                resultSet.getString("answer")
            )
            admin
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
            connection.endRequest()
//            connection.close()
        }
    }

    fun getMembersByYear(year: Int): ResultSet? {
        return try {
            val selectMember = "SELECT * FROM member WHERE year = ?"
            preparedStatement = connection.prepareStatement(selectMember)
            preparedStatement.setInt(1, year)
            preparedStatement.executeQuery()
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun getAttendancesByDate(date: String): ResultSet? {
        return try {
            val selectAdmin = "SELECT * FROM attendance WHERE date = ?"
            preparedStatement = connection.prepareStatement(selectAdmin)
            preparedStatement.setString(1, date)
            preparedStatement.executeQuery()
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun getAttendancesByName(name: String): ResultSet? {
        return try {
            val selectAdmin = "SELECT * FROM attendance WHERE membername = ?"
            preparedStatement = connection.prepareStatement(selectAdmin)
            preparedStatement.setString(1, name)
            preparedStatement.executeQuery()
        } catch (e: SQLException) {
            println(e)
            null
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun memberExists(name: String): Boolean {
        return try {
            val select = "SELECT * FROM member WHERE name like ?"
            preparedStatement = connection.prepareStatement(select)
            preparedStatement.setString(1, name)
            resultSet = preparedStatement.executeQuery()
            resultSet.next()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
            if(preparedStatement != null){
                preparedStatement.close()
            }
            if (resultSet !=null){
                resultSet.close()
            }
//            connection.close()
//            connection.endRequest()
        }
    }

    fun adminExists(userName: String): Boolean {
        return try {
            val select = "SELECT * FROM admin WHERE username = ?"
            preparedStatement = connection.prepareStatement(select)
            preparedStatement.setString(1, userName)
            resultSet = preparedStatement.executeQuery()
            resultSet.next()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun dateExists(date: String): Boolean {
        val select = "SELECT * FROM date WHERE date = ?"
        return try {
            preparedStatement = connection.prepareStatement(select)
            preparedStatement.setString(1, date)
            resultSet = preparedStatement.executeQuery()
            resultSet.next()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    fun resetAdminPassword(userName: String, password: String): Boolean {
        return try {
            val resetPassword = "UPDATE admin SET password=? WHERE username= ?"
            preparedStatement = connection.prepareStatement(resetPassword)
            preparedStatement.setString(1, sha256(password))
            preparedStatement.setString(2, userName)
            preparedStatement.execute()
        } catch (e: SQLException) {
            println(e)
            false
        } finally {
//            connection.close()
            connection.endRequest()
        }
    }

    private fun sha256(input: String): String? {
        return try {
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            digest.joinToString("") { String.format("%02x", it) }
        } catch (e: NoSuchAlgorithmException) {
            println(e)
            null
        }
    }
}