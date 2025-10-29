package com.example.mynotes.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


object DateFormater {

    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)

    private val formatDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatCurrentDate(): String{
        return formatDate.format(System.currentTimeMillis())
    }

    fun formatDateToString(timestamp: Long): String{
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < millisInHour -> "Just Now"
            diff < millisInDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours ago"
            }
            else -> {
                formatDate.format(timestamp)
            }
        }
    }
}