package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.model.BudgetPeriod
import com.example.data.model.TransactionType
import com.example.util.Formatters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val userPrefs = UserPreferences(context)
                val budgetConfig = userPrefs.budgetConfig.value

                if (!budgetConfig.isEnabled || !budgetConfig.notifyWeekly) return@launch

                val (startMillis, endMillis) = if (budgetConfig.period == BudgetPeriod.WEEKLY) {
                    Formatters.getCurrentWeekRange()
                } else {
                    Formatters.getMonthRange(Calendar.getInstance())
                }

                val transactions = db.transactionDao().getTransactionsBetween(startMillis, endMillis).first()
                val totalExpense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                val totalIncome = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val ratio = if (totalIncome > 0) (totalExpense / totalIncome * 100) else 0.0
                val ratioText = if (totalIncome > 0) String.format("%.1f%% dari pemasukan", ratio) else ""

                val isOver = totalExpense > budgetConfig.amount
                val title = if (isOver) {
                    "⚠️ Peringatan Akhir Minggu: Batas Anggaran Terlampaui!"
                } else {
                    "📊 Evaluasi Anggaran Akhir Minggu"
                }

                val periodName = budgetConfig.period.shortLabel
                val msg = if (isOver) {
                    val overAmount = totalExpense - budgetConfig.amount
                    "Pengeluaran $periodName telah mencapai ${Formatters.formatRupiah(totalExpense)}, melebihi batas anggaran ${Formatters.formatRupiah(budgetConfig.amount)} sebesar ${Formatters.formatRupiah(overAmount)}. $ratioText"
                } else {
                    val remaining = budgetConfig.amount - totalExpense
                    "Pengeluaran $periodName aman di ${Formatters.formatRupiah(totalExpense)} dari batas ${Formatters.formatRupiah(budgetConfig.amount)}. Sisa anggaran: ${Formatters.formatRupiah(remaining)}. $ratioText"
                }

                NotificationHelper.showBudgetNotification(
                    context = context,
                    title = title,
                    message = msg,
                    isWarning = isOver
                )
            } catch (e: Exception) {
                // Ignore background errors
            }
        }
    }
}
