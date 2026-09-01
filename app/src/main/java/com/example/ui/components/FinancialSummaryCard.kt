package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHover
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.RoseExpenseLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.Formatters

@Composable
fun FinancialSummaryCard(
    monthlyIncome: Double,
    monthlyExpense: Double,
    monthlyNetBalance: Double,
    expenseToIncomeRatio: Double,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            DarkSurfaceElevated,
            DarkSurface
        )
    )

    val ratioPercentageFormatted = String.format("%.1f", expenseToIncomeRatio)

    val (ratioColor, ratioStatusText) = when {
        monthlyIncome == 0.0 && monthlyExpense > 0 -> Pair(AlertRed, "100%+ (Tanpa Pemasukan)")
        expenseToIncomeRatio > 100 -> Pair(AlertRed, "Defisit ($ratioPercentageFormatted%)")
        expenseToIncomeRatio > 80 -> Pair(AmberWarning, "Waspada ($ratioPercentageFormatted%)")
        expenseToIncomeRatio > 50 -> Pair(CyanAccent, "Cukup Aman ($ratioPercentageFormatted%)")
        else -> Pair(MintGreen, "Sangat Sehat ($ratioPercentageFormatted%)")
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(22.dp))
            .testTag("financial_summary_card"),
        color = DarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .background(gradientBrush)
                .padding(20.dp)
        ) {
            // Net Balance Top Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "SISA SALDO BULAN INI",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatRupiah(monthlyNetBalance),
                        color = if (monthlyNetBalance >= 0) TextPrimary else AlertRed,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Automatic Percentage Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ratioColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ratioColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Rasio Pengeluaran",
                            tint = ratioColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Pengeluaran",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                lineHeight = 11.sp
                            )
                            Text(
                                text = "$ratioPercentageFormatted% dari Masuk",
                                color = ratioColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DarkBorder.copy(alpha = 0.6f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Income & Expense Breakdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income Column
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MintGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Pemasukan",
                            tint = MintGreenLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pemasukan",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = Formatters.formatRupiah(monthlyIncome),
                            color = MintGreenLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Expense Column
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoseExpense.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Pengeluaran",
                            tint = RoseExpenseLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pengeluaran",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = Formatters.formatRupiah(monthlyExpense),
                            color = RoseExpenseLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cashflow status indicator bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceHover.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status Arus Kas: $ratioStatusText",
                        color = ratioColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (monthlyIncome > 0) "Otomatis dihitung" else "Input pemasukan",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
