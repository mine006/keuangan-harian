package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val localeId = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return try {
            val formatted = currencyFormat.format(amount)
            // Replace "Rp" with "Rp " if needed for cleaner spacing
            if (formatted.startsWith("Rp") && !formatted.startsWith("Rp ")) {
                formatted.replaceFirst("Rp", "Rp ")
            } else {
                formatted
            }
        } catch (e: Exception) {
            "Rp " + String.format(localeId, "%,.0f", amount)
        }
    }

    fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", localeId)
        return sdf.format(Date(millis))
    }

    fun formatDateFull(millis: Long): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", localeId)
        return sdf.format(Date(millis))
    }

    fun formatMonthYear(calendar: Calendar): String {
        val sdf = SimpleDateFormat("MMMM yyyy", localeId)
        return sdf.format(calendar.time)
    }

    fun formatShortMonth(calendar: Calendar): String {
        val sdf = SimpleDateFormat("MMM yyyy", localeId)
        return sdf.format(calendar.time)
    }

    fun formatDayMonth(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM", localeId)
        return sdf.format(Date(millis))
    }

    fun formatRelativeDate(millis: Long): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = millis }

        val isToday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "Hari Ini"
            isYesterday -> "Kemarin"
            else -> formatDate(millis)
        }
    }

    /**
     * Calculates the start and end millis for a given calendar month
     */
    fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
        val start = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Pair(start.timeInMillis, end.timeInMillis)
    }

    /**
     * Calculates the start and end millis for current week (Monday to Sunday)
     */
    fun getCurrentWeekRange(): Pair<Long, Long> {
        val start = Calendar.getInstance(localeId).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.DAY_OF_WEEK, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Pair(start.timeInMillis, end.timeInMillis)
    }
}
