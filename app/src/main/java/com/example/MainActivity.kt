package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notification.NotificationHelper
import com.example.ui.components.AddEditTransactionSheet
import com.example.ui.components.BudgetAndProfileView
import com.example.ui.components.BudgetConfigDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.MonthlyChartsView
import com.example.ui.components.ProfileDialog
import com.example.ui.components.RegisterScreen
import com.example.ui.components.TransactionsListView
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                FinanceApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    if (!uiState.userProfile.isRegistered) {
        RegisterScreen(
            onRegister = { name, email ->
                viewModel.registerUser(name, email)
            }
        )
        return
    }

    // Request POST_NOTIFICATIONS permission for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                NotificationHelper.scheduleWeeklyReminder(context)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Show feedback message if any
    LaunchedEffect(uiState.notificationMessage) {
        uiState.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotificationMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(DarkBackground)
            ) {
                HeaderBar(
                    userProfile = uiState.userProfile,
                    isOverBudget = uiState.isOverBudget,
                    onOpenProfile = { viewModel.openProfileDialog() },
                    onOpenBudgetDialog = { viewModel.openBudgetDialog() },
                    onSendTestNotification = { viewModel.triggerTestNotification(context) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Transaksi
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.TRANSACTIONS,
                    onClick = { viewModel.selectTab(AppTab.TRANSACTIONS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.TRANSACTIONS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Transaksi"
                        )
                    },
                    label = {
                        Text(
                            text = "Transaksi",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.currentTab == AppTab.TRANSACTIONS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MintGreen,
                        selectedTextColor = MintGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = MintGreen.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_transactions")
                )

                // Tab 2: Statistik Grafik Bulanan
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.STATISTICS,
                    onClick = { viewModel.selectTab(AppTab.STATISTICS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.STATISTICS) Icons.Filled.ShowChart else Icons.Outlined.ShowChart,
                            contentDescription = "Statistik"
                        )
                    },
                    label = {
                        Text(
                            text = "Statistik",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.currentTab == AppTab.STATISTICS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanAccent,
                        selectedTextColor = CyanAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = CyanAccent.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_statistics")
                )

                // Tab 3: Anggaran & Akun
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.BUDGET_PROFILE,
                    onClick = { viewModel.selectTab(AppTab.BUDGET_PROFILE) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.BUDGET_PROFILE) Icons.Filled.Tune else Icons.Outlined.Tune,
                            contentDescription = "Anggaran & Akun"
                        )
                    },
                    label = {
                        Text(
                            text = "Anggaran & Akun",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.currentTab == AppTab.BUDGET_PROFILE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = if (uiState.isOverBudget) AlertRed else MintGreen,
                        selectedTextColor = if (uiState.isOverBudget) AlertRed else MintGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = if (uiState.isOverBudget) AlertRed.copy(alpha = 0.2f) else MintGreen.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_budget_profile")
                )
            }
        },
        floatingActionButton = {
            if (uiState.currentTab == AppTab.TRANSACTIONS) {
                FloatingActionButton(
                    onClick = { viewModel.openAddTransaction() },
                    containerColor = MintGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("fab_add_transaction")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Catat Transaksi",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (uiState.currentTab) {
                AppTab.TRANSACTIONS -> {
                    TransactionsListView(
                        transactions = uiState.allTransactions,
                        monthlyIncome = uiState.monthlyIncome,
                        monthlyExpense = uiState.monthlyExpense,
                        monthlyNetBalance = uiState.monthlyNetBalance,
                        expenseToIncomeRatio = uiState.expenseToIncomeRatio,
                        currentPeriodExpense = uiState.currentPeriodExpense,
                        isOverBudget = uiState.isOverBudget,
                        budgetConfig = uiState.budgetConfig,
                        filterType = uiState.selectedFilterType,
                        onFilterTypeChange = { viewModel.setFilterType(it) },
                        onTransactionClick = { viewModel.openEditTransaction(it) },
                        onOpenAddTransaction = { viewModel.openAddTransaction() },
                        onOpenBudgetDialog = { viewModel.openBudgetDialog() }
                    )
                }

                AppTab.STATISTICS -> {
                    MonthlyChartsView(
                        selectedMonth = uiState.selectedMonthCalendar,
                        monthlyIncome = uiState.monthlyIncome,
                        monthlyExpense = uiState.monthlyExpense,
                        monthlyNetBalance = uiState.monthlyNetBalance,
                        expenseToIncomeRatio = uiState.expenseToIncomeRatio,
                        dailyChartData = uiState.dailyChartData,
                        categorySummaries = uiState.categoryExpenseSummaries,
                        budgetConfig = uiState.budgetConfig,
                        onPrevMonth = { viewModel.prevMonth() },
                        onNextMonth = { viewModel.nextMonth() }
                    )
                }

                AppTab.BUDGET_PROFILE -> {
                    BudgetAndProfileView(
                        budgetConfig = uiState.budgetConfig,
                        currentPeriodExpense = uiState.currentPeriodExpense,
                        isOverBudget = uiState.isOverBudget,
                        userProfile = uiState.userProfile,
                        onOpenBudgetDialog = { viewModel.openBudgetDialog() },
                        onOpenProfileDialog = { viewModel.openProfileDialog() },
                        onToggleGoogleLink = {
                            viewModel.toggleGoogleAccount(
                                uiState.userProfile.email,
                                uiState.userProfile.name
                            )
                        },
                        onSendTestNotification = { viewModel.triggerTestNotification(context) },
                        onToggleWeeklyNotification = { enabled ->
                            viewModel.updateBudgetConfig(
                                isEnabled = uiState.budgetConfig.isEnabled,
                                amount = uiState.budgetConfig.amount,
                                period = uiState.budgetConfig.period,
                                notifyWeekly = enabled
                            )
                        },
                        onToggleBudgetEnabled = { enabled ->
                            viewModel.updateBudgetConfig(
                                isEnabled = enabled,
                                amount = uiState.budgetConfig.amount,
                                period = uiState.budgetConfig.period,
                                notifyWeekly = uiState.budgetConfig.notifyWeekly
                            )
                        }
                    )
                }
            }

            // Add/Edit Transaction BottomSheet
            if (uiState.isAddEditSheetOpen) {
                AddEditTransactionSheet(
                    initialTransaction = uiState.editingTransaction,
                    monthlyIncome = uiState.monthlyIncome,
                    onDismiss = { viewModel.closeAddEditSheet() },
                    onSave = { id, title, amount, type, categoryId, categoryName, dateMillis, note ->
                        viewModel.saveTransaction(
                            id = id,
                            title = title,
                            amount = amount,
                            type = type,
                            categoryId = categoryId,
                            categoryName = categoryName,
                            dateMillis = dateMillis,
                            note = note
                        )
                    },
                    onDelete = { viewModel.deleteTransaction(it) }
                )
            }

            // Budget Configuration Dialog
            if (uiState.isBudgetDialogOpen) {
                BudgetConfigDialog(
                    initialConfig = uiState.budgetConfig,
                    onDismiss = { viewModel.closeBudgetDialog() },
                    onSave = { isEnabled, amount, period, notifyWeekly ->
                        viewModel.updateBudgetConfig(isEnabled, amount, period, notifyWeekly)
                    }
                )
            }

            // Profile Dialog
            if (uiState.isProfileDialogOpen) {
                ProfileDialog(
                    initialProfile = uiState.userProfile,
                    onDismiss = { viewModel.closeProfileDialog() },
                    onSave = { name, email, isGoogleLinked ->
                        viewModel.updateUserProfile(name, email, isGoogleLinked)
                    }
                )
            }
        }
    }
}
