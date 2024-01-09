package com.example.hellofx

import java.time.LocalDate

data class Student(
    val studentNumber: String,
    val givenName: String,
    val familyName: String,
    val dateOfBirth: LocalDate,
    val email: String,
    val courseType: CourseType,
    val dateOfEntry: LocalDate,
    val modeOfStudy: ModeOfStudy,
    val hasEnrolled: Boolean,
    val hasDebts: Boolean,
    val moduleMark: List<Mark>

)

enum class CourseType {
    CS,
    CSG,
    SE
}

enum class ModeOfStudy {
    FullTime,
    PartTime
}