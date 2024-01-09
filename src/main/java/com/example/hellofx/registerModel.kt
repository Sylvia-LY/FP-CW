package com.example.hellofx

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Year


class RegisterModel {
    init {
        Main.registerM = this
    }

    val students: ObservableList<Student> = getAllStudents()
    val modules: ObservableList<Module> = getAllModules()

    fun getAllModules(): ObservableList<Module> {
        val allModulesList: ObservableList<Module> = FXCollections.observableArrayList()
        val selectSql = "select * from modules"
        val con: Connection = DBConnection.getDBConnection()
        try {
            val prep: PreparedStatement = con.prepareStatement(selectSql)
            val result: ResultSet = prep.executeQuery()
            while (result.next()) {
                allModulesList += Module(
                    result.getInt("id"),
                    result.getString("module_number"),
                    result.getString("module_name"),
                    result.getInt("level"),
                    result.getInt("credit_value"),
                    result.getInt("threshold_mark")
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return allModulesList
    }

    fun getModuleMark(studentId: Int): ObservableList<Mark> {
        val moduleMarkList: ObservableList<Mark> = FXCollections.observableArrayList()
        val selectSql = "SELECT * FROM marks INNER JOIN modules on marks.module_id = modules.id where student_id = '$studentId'"
        val con: Connection = DBConnection.getDBConnection()
        try {
            val prep: PreparedStatement = con.prepareStatement(selectSql)
            val result: ResultSet = prep.executeQuery()
            while (result.next()) {
                var credit = result.getInt("credit_value")
                if (result.getDouble("mark")<result.getDouble("threshold_mark")) {
                    credit = 0
                }
                moduleMarkList += Mark(
                    result.getString("module_number"),
                    result.getString("module_name"),
                    result.getInt("level"),
                    result.getDouble("mark"),
                    credit,
                    result.getBoolean("has_passed")

                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return moduleMarkList
    }

    fun getAllStudents(): ObservableList<Student> {
        val allStudentsList: ObservableList<Student> = FXCollections.observableArrayList()
        val selectSql = "select * from students"
        val con: Connection = DBConnection.getDBConnection()
        try {
            val prep: PreparedStatement = con.prepareStatement(selectSql)
            val result: ResultSet = prep.executeQuery()
            while (result.next()) {
                allStudentsList += Student(
                    result.getString("student_number"),
                    result.getString("given_name"),
                    result.getString("family_name"),
                    result.getDate("date_of_birth").toLocalDate(),
                    result.getString("email"),
                    CourseType.valueOf(result.getString("course_type")),
                    result.getDate("date_of_entry").toLocalDate(),
                    ModeOfStudy.valueOf(result.getString("mode_of_study")),
                    result.getBoolean("has_enrolled"),
                    result.getBoolean("has_debts"),
                    getModuleMark(result.getInt("id"))
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return allStudentsList
    }

    fun getStudentsByStudentNumber(studentNumber: String): List<Student> {
        return students.filter { it.studentNumber == studentNumber }
    }

    fun getStudentsByFullName(fullName: String): List<Student> {
        return students.filter {
            it.givenName == fullName.split(" ")[0]
                && it.familyName == fullName.split(" ")[1]
        }
    }

    fun getStudentsByCourseType(courseType: CourseType): List<Student> {
        return students.filter { it.courseType == courseType }
    }

    fun getStudentsByModeOfStudy(modeOfStudy: ModeOfStudy): List<Student> {
        return students.filter { it.modeOfStudy == modeOfStudy }
    }


    fun getStudentsByModule(moduleNumber: String): List<Student> {
        return getAllStudents().filter {
            s -> s.moduleMark.any { m -> m.moduleNumber == moduleNumber }
        }
    }


    fun getStudentsByPartialName(text: String): List<Student> {
        return students.filter {
            it.givenName.contains(text)
                || it.familyName.contains(text)
        }
    }

    fun areAllStudentsEnrolled(): Boolean {
        return students.all { it.hasEnrolled }
    }

    fun getUnenrolledStudents(): List<Student> {
        return students.filter { !it.hasEnrolled }
    }

    fun getEnrolledStudents(): List<Student> {
        return students.filter { it.hasEnrolled }
    }

    fun areAllStudentsDebtFree(): Boolean {
        return students.all { !it.hasDebts }
    }

    fun getDebtFreeStudents(): List<Student> {
        return students.filter { !it.hasDebts }
    }

    fun getStudentsWithDebts(): List<Student> {
        return students.filter { it.hasDebts }
    }

    fun getStudentsByAgeRange(min: Int, max: Int): List<Student> {
        return students.filter {
            (Year.now().value - it.dateOfBirth.year) in min..max
        }
    }

    fun getStudentsByGivenNameRange(beginningLetter: String, endLetter: String): List<Student> {
        return students.filter {
            it.givenName.substring(0, 1) in beginningLetter..endLetter
        }
    }

    fun calculateTotalMarks(student: Student): Double {
        return student.moduleMark.sumOf { it.mark }
    }

    fun calculateTotalCredits(student: Student): Int {
        return student.moduleMark.filter { it.hasPassed }
                                .sumOf { it.credit }
    }

    fun getPassedModules(student: Student): List<Mark> {
        return student.moduleMark.filter { it.hasPassed }
    }

    fun getFailedModules(student: Student): List<Mark> {
        return student.moduleMark.filter { !it.hasPassed }
    }


    fun calculatePassRate(list: List<RowData>): Double {
        val passCnt = list.count { it.hasPassed }
        return passCnt.toDouble()/list.size * 100
    }

    fun calculateAverageMark(list: List<RowData>): Double {
        return list.map { it.mark }.average()
    }

    fun findMaxMark(list: List<RowData>): Double {
        return list.maxOf { it.mark }
    }

    fun findMinMark(list: List<RowData>): Double {
        return list.minOf { it.mark }
    }

    fun getModulesByLevel(level: Int): List<Module>? {
        return modules.groupBy { it.level }[level]
    }

}

