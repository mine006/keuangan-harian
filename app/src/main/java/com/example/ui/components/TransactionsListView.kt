package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BudgetConfig
import com.example.data.local.TransactionEntity
import com.example.data.model.CategoryDefaults
import com.example.data.model.TransactionType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreen
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.Formatters

@Composable
fun TransactionsListView(
    transactions: List<TransactionEntity>,
    monthlyIncome: Double,
    monthlyExpense: Double,
    monthlyNetBalance: Double,
    expenseToIncomeRatio: Double,
    currentPeriodExpense: Double,
    isOverBudget: Boolean,
    budgetConfig: BudgetConfig,
    filterType: TransactionType?,
    onFilterTypeChange: (TransactionType?) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onOpenAddTransaction: () -> Unit,
    onOpenBudgetDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredList = transactions.filter { tx ->
        if (filterType != null) tx.type == filterType else true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Budget Warning or Normal Banner
        item {
            BudgetWarningBanner(
                budgetConfig = budgetConfig,
                currentPeriodExpense = currentPeriodExpense,
                isOverBudget = isOverBudget,
                onOpenBudgetSettings = onOpenBudgetDialog
            )
        }

        // Financial Overview Card
        item {
            FinancialSummaryCard(
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                monthlyNetBalance = monthlyNetBalance,
                expenseToIncomeRatio = expenseToIncomeRatio
            )
        }

        // Filter Pills Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Filter
                FilterChip(
                    selected = filterType == null,
                    onClick = { onFilterTypeChange(null) },
                    label = { Text("Semua (${transactions.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MintGreen.copy(alpha = 0.2f),
                        selectedLabelColor = MintGreen,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterType == null,
                        borderColor = DarkBorder,
                        selectedBorderColor = MintGreen
                    ),
                    modifier = Modifier.testTag("filter_all")
                )

                // Expense Filter
                FilterChip(
                    selected = filterType == TransactionType.EXPENSE,
                    onClick = { onFilterTypeChange(TransactionType.EXPENSE) },
                    label = { Text("Pengeluaran") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (filterType == TransactionType.EXPENSE) RoseExpense else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoseExpense.copy(alpha = 0.2f),
                        selectedLabelColor = RoseExpense,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterType == TransactionType.EXPENSE,
                        borderColor = DarkBorder,
                        selectedBorderColor = RoseExpense
                    ),
                    modifier = Modifier.testTag("filter_expense")
                )

                // Income Filter
                FilterChip(
                    selected = filterType == TransactionType.INCOME,
                    onClick = { onFilterTypeChange(TransactionType.INCOME) },
                    label = { Text("Pemasukan") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (filterType == TransactionType.INCOME) MintGreen else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MintGreen.copy(alpha = 0.2f),
                        selectedLabelColor = MintGreen,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterType == TransactionType.INCOME,
                        borderColor = DarkBorder,
                        selectedBorderColor = MintGreen
                    ),
                    modifier = Modifier.testTag("filter_income")
                )
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Transaksi",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${filteredList.size} item",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Empty state or Items
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                    color = DarkSurfaceElevated
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(DarkBorder.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Belum Ada Catatan Transaksi",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tekan tombol + di bawah untuk mencatat pemasukan atau pengeluaran baru Anda.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    monthlyIncome = monthlyIncome,
                    onClick = { onTransactionClick(tx) }
                )
            }
        }

        item {
            SimpleCopyrightText()
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
