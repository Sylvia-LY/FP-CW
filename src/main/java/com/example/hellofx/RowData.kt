package com.example.hellofx

data class RowData(
    val studentNumber: String,
    val givenName: String,
    val familyName: String,
    val courseType: CourseType,
    val mark: Double,
    val credit: Int,
    val hasPassed: Boolean

)
