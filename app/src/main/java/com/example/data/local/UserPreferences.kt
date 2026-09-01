package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BudgetPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BudgetConfig(
    val isEnabled: Boolean = true,
    val amount: Double = 3000000.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val notifyWeekly: Boolean = true,
    val isFirstTimeConfigured: Boolean = false
)

data class UserProfile(
    val name: String = "Pengguna",
    val email: String = "pengguna@example.com",
    val isGoogleLinked: Boolean = false,
    val avatarColorIndex: Int = 0
)

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("catat_keuangan_prefs", Context.MODE_PRIVATE)

    private val _budgetConfig = MutableStateFlow(loadBudgetConfig())
    val budgetConfig: StateFlow<BudgetConfig> = _budgetConfig.asStateFlow()

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadBudgetConfig(): BudgetConfig {
        return BudgetConfig(
            isEnabled = prefs.getBoolean("budget_enabled", true),
            amount = prefs.getFloat("budget_amount", 3000000f).toDouble(),
            period = try {
                BudgetPeriod.valueOf(prefs.getString("budget_period", BudgetPeriod.MONTHLY.name) ?: BudgetPeriod.MONTHLY.name)
            } catch (e: Exception) {
                BudgetPeriod.MONTHLY
            },
            notifyWeekly = prefs.getBoolean("budget_notify_weekly", true),
            isFirstTimeConfigured = prefs.getBoolean("budget_first_configured", false)
        )
    }

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Budi Santoso") ?: "Budi Santoso",
            email = prefs.getString("user_email", "budi.santoso@gmail.com") ?: "budi.santoso@gmail.com",
            isGoogleLinked = prefs.getBoolean("user_google_linked", false),
            avatarColorIndex = prefs.getInt("user_avatar_index", 0)
        )
    }

    fun saveBudgetConfig(config: BudgetConfig) {
        prefs.edit()
            .putBoolean("budget_enabled", config.isEnabled)
            .putFloat("budget_amount", config.amount.toFloat())
            .putString("budget_period", config.period.name)
            .putBoolean("budget_notify_weekly", config.notifyWeekly)
            .putBoolean("budget_first_configured", true)
            .apply()
        _budgetConfig.value = config.copy(isFirstTimeConfigured = true)
    }

    fun saveUserProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_email", profile.email)
            .putBoolean("user_google_linked", profile.isGoogleLinked)
            .putInt("user_avatar_index", profile.avatarColorIndex)
            .apply()
        _userProfile.value = profile
    }

    fun toggleGoogleAccountLink(email: String = "amint5126@gmail.com", name: String = "Amin T.") {
        val current = _userProfile.value
        val updated = if (current.isGoogleLinked) {
            current.copy(isGoogleLinked = false)
        } else {
            current.copy(
                isGoogleLinked = true,
                email = email.ifBlank { "amint5126@gmail.com" },
                name = name.ifBlank { "Amin T." }
            )
        }
        saveUserProfile(updated)
    }
}
