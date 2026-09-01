package com.example.data.repository

import com.example.data.local.BudgetConfig
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.local.UserPreferences
import com.example.data.local.UserProfile
import com.example.data.model.BudgetPeriod
import com.example.data.model.CategoryDefaults
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val userPreferences: UserPreferences
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val budgetConfig: StateFlow<BudgetConfig> = userPreferences.budgetConfig
    val userProfile: StateFlow<UserProfile> = userPreferences.userProfile

    fun getTransactionsBetween(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsBetween(startMillis, endMillis)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteById(id)
    }

    fun saveBudgetConfig(config: BudgetConfig) {
        userPreferences.saveBudgetConfig(config)
    }

    fun saveUserProfile(profile: UserProfile) {
        userPreferences.saveUserProfile(profile)
    }

    fun toggleGoogleAccountLink(email: String, name: String) {
        userPreferences.toggleGoogleAccountLink(email, name)
    }

    suspend fun seedInitialDataIfEmpty() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Seed realistic starter transactions across the current month
        val sampleTransactions = listOf(
            TransactionEntity(
                title = "Gaji Bulanan",
                amount = 7500000.0,
                type = TransactionType.INCOME,
                categoryId = "salary",
                categoryName = CategoryDefaults.getCategory("salary", TransactionType.INCOME).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 1, 9, 0)
                }.timeInMillis,
                note = "Transfer Gaji Pokok"
            ),
            TransactionEntity(
                title = "Proyek Desain Web",
                amount = 2000000.0,
                type = TransactionType.INCOME,
                categoryId = "freelance",
                categoryName = CategoryDefaults.getCategory("freelance", TransactionType.INCOME).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 10, 14, 30)
                }.timeInMillis,
                note = "Klien PT Digital"
            ),
            TransactionEntity(
                title = "Belanja Bulanan Supermarket",
                amount = 850000.0,
                type = TransactionType.EXPENSE,
                categoryId = "shopping",
                categoryName = CategoryDefaults.getCategory("shopping", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 3, 11, 15)
                }.timeInMillis,
                note = "Bahan pokok & sabun"
            ),
            TransactionEntity(
                title = "Makan Siang & Kopi",
                amount = 65000.0,
                type = TransactionType.EXPENSE,
                categoryId = "food",
                categoryName = CategoryDefaults.getCategory("food", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 5, 12, 45)
                }.timeInMillis,
                note = "Nasi Padang + Es Kopi"
            ),
            TransactionEntity(
                title = "Bensin Kendaraan",
                amount = 100000.0,
                type = TransactionType.EXPENSE,
                categoryId = "transport",
                categoryName = CategoryDefaults.getCategory("transport", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 8, 8, 30)
                }.timeInMillis,
                note = "Pertamax Full"
            ),
            TransactionEntity(
                title = "Tagihan Listrik & WiFi",
                amount = 450000.0,
                type = TransactionType.EXPENSE,
                categoryId = "bills",
                categoryName = CategoryDefaults.getCategory("bills", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 12, 10, 0)
                }.timeInMillis,
                note = "Token PLN & Indihome"
            ),
            TransactionEntity(
                title = "Makan Malam Bersama Keluarga",
                amount = 280000.0,
                type = TransactionType.EXPENSE,
                categoryId = "food",
                categoryName = CategoryDefaults.getCategory("food", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 18, 19, 30)
                }.timeInMillis,
                note = "Restoran Seafood"
            ),
            TransactionEntity(
                title = "Langganan Streaming & Game",
                amount = 185000.0,
                type = TransactionType.EXPENSE,
                categoryId = "entertainment",
                categoryName = CategoryDefaults.getCategory("entertainment", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 22, 16, 20)
                }.timeInMillis,
                note = "Spotify & Netflix"
            ),
            TransactionEntity(
                title = "Vitamin & Suplemen Kesehatan",
                amount = 140000.0,
                type = TransactionType.EXPENSE,
                categoryId = "health",
                categoryName = CategoryDefaults.getCategory("health", TransactionType.EXPENSE).name,
                dateMillis = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 25, 15, 0)
                }.timeInMillis,
                note = "Apotek Sehat"
            )
        )

        for (item in sampleTransactions) {
            transactionDao.insert(item)
        }
    }
}
