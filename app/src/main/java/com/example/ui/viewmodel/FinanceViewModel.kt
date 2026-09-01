package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BudgetConfig
import com.example.data.local.TransactionEntity
import com.example.data.local.UserPreferences
import com.example.data.local.UserProfile
import com.example.data.model.BudgetPeriod
import com.example.data.model.CategoryDefaults
import com.example.data.model.TransactionType
import com.example.data.repository.FinanceRepository
import com.example.notification.NotificationHelper
import com.example.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppTab(val title: String) {
    TRANSACTIONS("Transaksi"),
    STATISTICS("Statistik"),
    BUDGET_PROFILE("Anggaran & Akun")
}

data class DailyChartItem(
    val dayNumber: Int,
    val dateLabel: String,
    val incomeAmount: Double,
    val expenseAmount: Double
)

data class CategoryExpenseSummary(
    val categoryId: String,
    val categoryName: String,
    val amount: Double,
    val percentage: Double,
    val colorHex: Long,
    val transactionCount: Int
)

data class FinanceUiState(
    val selectedMonthCalendar: Calendar = Calendar.getInstance(),
    val currentTab: AppTab = AppTab.TRANSACTIONS,
    val allTransactions: List<TransactionEntity> = emptyList(),
    val monthlyTransactions: List<TransactionEntity> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyNetBalance: Double = 0.0,
    val expenseToIncomeRatio: Double = 0.0,
    val currentPeriodExpense: Double = 0.0,
    val isOverBudget: Boolean = false,
    val budgetUsedPercentage: Double = 0.0,
    val budgetRemaining: Double = 0.0,
    val budgetConfig: BudgetConfig = BudgetConfig(),
    val userProfile: UserProfile = UserProfile(),
    val dailyChartData: List<DailyChartItem> = emptyList(),
    val categoryExpenseSummaries: List<CategoryExpenseSummary> = emptyList(),
    val selectedFilterType: TransactionType? = null,
    val selectedFilterCategory: String? = null,
    val isAddEditSheetOpen: Boolean = false,
    val editingTransaction: TransactionEntity? = null,
    val isBudgetDialogOpen: Boolean = false,
    val isProfileDialogOpen: Boolean = false,
    val notificationMessage: String? = null
)

// Internal helper holders for combining streams
private data class FilterState(
    val monthCal: Calendar,
    val tab: AppTab,
    val typeFilter: TransactionType?,
    val catFilter: String?
)

private data class DialogState(
    val isAddEditOpen: Boolean,
    val editingTx: TransactionEntity?,
    val isBudgetOpen: Boolean,
    val isProfileOpen: Boolean,
    val notifMsg: String?
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
    val selectedMonth: StateFlow<Calendar> = _selectedMonth.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.TRANSACTIONS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _filterType = MutableStateFlow<TransactionType?>(null)
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory.asStateFlow()

    private val _isAddEditSheetOpen = MutableStateFlow(false)
    val isAddEditSheetOpen: StateFlow<Boolean> = _isAddEditSheetOpen.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val editingTransaction: StateFlow<TransactionEntity?> = _editingTransaction.asStateFlow()

    private val _isBudgetDialogOpen = MutableStateFlow(false)
    val isBudgetDialogOpen: StateFlow<Boolean> = _isBudgetDialogOpen.asStateFlow()

    private val _isProfileDialogOpen = MutableStateFlow(false)
    val isProfileDialogOpen: StateFlow<Boolean> = _isProfileDialogOpen.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        val userPrefs = UserPreferences(application)
        repository = FinanceRepository(db.transactionDao(), userPrefs)

        viewModelScope.launch {
            repository.allTransactions.collect { list ->
                if (list.isEmpty()) {
                    repository.seedInitialDataIfEmpty()
                }
            }
        }

        // Schedule weekly notification if enabled
        if (repository.budgetConfig.value.isEnabled && repository.budgetConfig.value.notifyWeekly) {
            NotificationHelper.scheduleWeeklyReminder(application)
        }
    }

    private val filterFlow = combine(
        _selectedMonth,
        _currentTab,
        _filterType,
        _filterCategory
    ) { monthCal, tab, typeFilter, catFilter ->
        FilterState(monthCal, tab, typeFilter, catFilter)
    }

    private val dialogFlow = combine(
        _isAddEditSheetOpen,
        _editingTransaction,
        _isBudgetDialogOpen,
        _isProfileDialogOpen,
        _notificationMessage
    ) { isAddEdit, editing, isBudget, isProfile, msg ->
        DialogState(isAddEdit, editing, isBudget, isProfile, msg)
    }

    val uiState: StateFlow<FinanceUiState> = combine(
        repository.allTransactions,
        repository.budgetConfig,
        repository.userProfile,
        filterFlow,
        dialogFlow
    ) { allTx: List<TransactionEntity>, budget: BudgetConfig, profile: UserProfile, filter: FilterState, dialog: DialogState ->
        val monthCal = filter.monthCal
        val (monthStart, monthEnd) = Formatters.getMonthRange(monthCal)
        val monthlyTx = allTx.filter { it.dateMillis in monthStart..monthEnd }

        var monthlyIncome = 0.0
        var monthlyExpense = 0.0
        for (tx in monthlyTx) {
            if (tx.type == TransactionType.INCOME) {
                monthlyIncome += tx.amount
            } else {
                monthlyExpense += tx.amount
            }
        }
        val monthlyNet = monthlyIncome - monthlyExpense

        val ratio = if (monthlyIncome > 0.0) (monthlyExpense / monthlyIncome * 100.0) else 0.0

        // Calculate current period expense for active budget (Weekly or Monthly)
        val (budgetStart, budgetEnd) = if (budget.period == BudgetPeriod.WEEKLY) {
            Formatters.getCurrentWeekRange()
        } else {
            Formatters.getMonthRange(Calendar.getInstance())
        }
        var currentPeriodExpense = 0.0
        for (tx in allTx) {
            if (tx.type == TransactionType.EXPENSE && tx.dateMillis in budgetStart..budgetEnd) {
                currentPeriodExpense += tx.amount
            }
        }

        val isOver = budget.isEnabled && (currentPeriodExpense > budget.amount)
        val budgetUsedPct = if (budget.amount > 0.0) (currentPeriodExpense / budget.amount * 100.0) else 0.0
        val remaining = budget.amount - currentPeriodExpense

        // Daily chart data for selected month
        val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dailyItems = (1..daysInMonth).map { day ->
            val dayCal = (monthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
            val dayStart = (dayCal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = (dayCal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            var dayIncome = 0.0
            var dayExpense = 0.0
            for (tx in monthlyTx) {
                if (tx.dateMillis in dayStart..dayEnd) {
                    if (tx.type == TransactionType.INCOME) {
                        dayIncome += tx.amount
                    } else {
                        dayExpense += tx.amount
                    }
                }
            }

            DailyChartItem(
                dayNumber = day,
                dateLabel = "$day",
                incomeAmount = dayIncome,
                expenseAmount = dayExpense
            )
        }

        // Category breakdown
        val expenseTx = monthlyTx.filter { it.type == TransactionType.EXPENSE }
        val groupedCategories = expenseTx.groupBy { it.categoryId }
        val categorySummaries = groupedCategories.map { (catId, txList) ->
            var catTotal = 0.0
            for (tx in txList) {
                catTotal += tx.amount
            }
            val catObj = CategoryDefaults.getCategory(catId, TransactionType.EXPENSE)
            val pct = if (monthlyExpense > 0.0) (catTotal / monthlyExpense * 100.0) else 0.0
            CategoryExpenseSummary(
                categoryId = catId,
                categoryName = catObj.name,
                amount = catTotal,
                percentage = pct,
                colorHex = catObj.colorHex,
                transactionCount = txList.size
            )
        }.sortedByDescending { it.amount }

        FinanceUiState(
            selectedMonthCalendar = monthCal,
            currentTab = filter.tab,
            allTransactions = allTx,
            monthlyTransactions = monthlyTx,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            monthlyNetBalance = monthlyNet,
            expenseToIncomeRatio = ratio,
            currentPeriodExpense = currentPeriodExpense,
            isOverBudget = isOver,
            budgetUsedPercentage = budgetUsedPct,
            budgetRemaining = remaining,
            budgetConfig = budget,
            userProfile = profile,
            dailyChartData = dailyItems,
            categoryExpenseSummaries = categorySummaries,
            selectedFilterType = filter.typeFilter,
            selectedFilterCategory = filter.catFilter,
            isAddEditSheetOpen = dialog.isAddEditOpen,
            editingTransaction = dialog.editingTx,
            isBudgetDialogOpen = dialog.isBudgetOpen,
            isProfileDialogOpen = dialog.isProfileOpen,
            notificationMessage = dialog.notifMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceUiState()
    )

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setFilterType(type: TransactionType?) {
        _filterType.value = type
    }

    fun setFilterCategory(catId: String?) {
        _filterCategory.value = catId
    }

    fun prevMonth() {
        val newCal = (_selectedMonth.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _selectedMonth.value = newCal
    }

    fun nextMonth() {
        val newCal = (_selectedMonth.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _selectedMonth.value = newCal
    }

    fun setMonth(year: Int, month: Int) {
        val newCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        _selectedMonth.value = newCal
    }

    fun openAddTransaction() {
        _editingTransaction.value = null
        _isAddEditSheetOpen.value = true
    }

    fun openEditTransaction(transaction: TransactionEntity) {
        _editingTransaction.value = transaction
        _isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        _isAddEditSheetOpen.value = false
        _editingTransaction.value = null
    }

    fun openBudgetDialog() {
        _isBudgetDialogOpen.value = true
    }

    fun closeBudgetDialog() {
        _isBudgetDialogOpen.value = false
    }

    fun openProfileDialog() {
        _isProfileDialogOpen.value = true
    }

    fun closeProfileDialog() {
        _isProfileDialogOpen.value = false
    }

    fun saveTransaction(
        id: Long,
        title: String,
        amount: Double,
        type: TransactionType,
        categoryId: String,
        categoryName: String,
        dateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                title = title.ifBlank { if (type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran" },
                amount = amount,
                type = type,
                categoryId = categoryId,
                categoryName = categoryName,
                dateMillis = dateMillis,
                note = note
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
            } else {
                repository.updateTransaction(entity)
            }
            closeAddEditSheet()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            closeAddEditSheet()
        }
    }

    fun updateBudgetConfig(
        isEnabled: Boolean,
        amount: Double,
        period: BudgetPeriod,
        notifyWeekly: Boolean
    ) {
        val newConfig = BudgetConfig(
            isEnabled = isEnabled,
            amount = amount,
            period = period,
            notifyWeekly = notifyWeekly,
            isFirstTimeConfigured = true
        )
        repository.saveBudgetConfig(newConfig)
        if (isEnabled && notifyWeekly) {
            NotificationHelper.scheduleWeeklyReminder(getApplication())
        } else {
            NotificationHelper.cancelWeeklyReminder(getApplication())
        }
        closeBudgetDialog()
    }

    fun updateUserProfile(name: String, email: String, isGoogleLinked: Boolean) {
        val current = repository.userProfile.value
        repository.saveUserProfile(
            current.copy(
                name = name,
                email = email,
                isGoogleLinked = isGoogleLinked
            )
        )
        closeProfileDialog()
    }

    fun toggleGoogleAccount(email: String, name: String) {
        repository.toggleGoogleAccountLink(email, name)
    }

    fun triggerTestNotification(context: Context) {
        val state = uiState.value
        val isOver = state.isOverBudget
        val title = if (isOver) {
            "⚠️ Peringatan Akhir Minggu: Batas Anggaran Terlampaui!"
        } else {
            "📊 Evaluasi Anggaran Akhir Minggu"
        }

        val periodName = state.budgetConfig.period.shortLabel
        val ratioText = if (state.monthlyIncome > 0) String.format("%.1f%% dari pemasukan", state.expenseToIncomeRatio) else ""

        val msg = if (isOver) {
            val overAmount = state.currentPeriodExpense - state.budgetConfig.amount
            "Pengeluaran $periodName telah mencapai ${Formatters.formatRupiah(state.currentPeriodExpense)}, melebihi batas ${Formatters.formatRupiah(state.budgetConfig.amount)} sebesar ${Formatters.formatRupiah(overAmount)}. $ratioText"
        } else {
            val remaining = state.budgetConfig.amount - state.currentPeriodExpense
            "Pengeluaran $periodName terkendali di ${Formatters.formatRupiah(state.currentPeriodExpense)} dari batas ${Formatters.formatRupiah(state.budgetConfig.amount)}. Sisa: ${Formatters.formatRupiah(remaining)}. $ratioText"
        }

        NotificationHelper.showBudgetNotification(
            context = context,
            title = title,
            message = msg,
            isWarning = isOver
        )
        _notificationMessage.value = "Notifikasi pengingat berhasil dikirim ke perangkat!"
    }

    fun clearNotificationMessage() {
        _notificationMessage.value = null
    }
}
