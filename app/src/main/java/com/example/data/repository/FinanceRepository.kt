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

    suspend fun clearAllTransactions() {
        transactionDao.deleteAll()
    }
}

