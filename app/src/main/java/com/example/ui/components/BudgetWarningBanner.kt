package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BudgetConfig
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBorder
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.Formatters

@Composable
fun BudgetWarningBanner(
    budgetConfig: BudgetConfig,
    currentPeriodExpense: Double,
    isOverBudget: Boolean,
    onOpenBudgetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!budgetConfig.isEnabled) {
        // If budget tracking is disabled
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onOpenBudgetSettings() }
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
                .testTag("budget_disabled_card"),
            color = DarkSurfaceElevated
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkBorder.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Batas Anggaran",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Batas Anggaran Nonaktif",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ketuk untuk mengaktifkan batas pengeluaran",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Anggaran",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        return
    }

    val progress = if (budgetConfig.amount > 0) {
        (currentPeriodExpense / budgetConfig.amount).toFloat().coerceIn(0f, 1f)
    } else 0f

    val percentageUsed = if (budgetConfig.amount > 0) {
        (currentPeriodExpense / budgetConfig.amount * 100).toInt()
    } else 0

    val periodLabel = budgetConfig.period.shortLabel

    // Dynamic warning colors when over-budget vs normal
    val cardBackground by animateColorAsState(
        targetValue = if (isOverBudget) AlertRedContainer else DarkSurfaceElevated,
        animationSpec = tween(durationMillis = 400),
        label = "card_bg"
    )
    val cardBorder by animateColorAsState(
        targetValue = if (isOverBudget) AlertRedBorder else DarkBorder,
        animationSpec = tween(durationMillis = 400),
        label = "card_border"
    )
    val progressColor by animateColorAsState(
        targetValue = if (isOverBudget) AlertRed else if (percentageUsed > 80) AmberWarning else MintGreen,
        animationSpec = tween(durationMillis = 400),
        label = "progress_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, cardBorder, RoundedCornerShape(20.dp))
            .clickable { onOpenBudgetSettings() }
            .testTag("budget_status_card"),
        color = cardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isOverBudget) AlertRed.copy(alpha = 0.2f)
                                else MintGreen.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOverBudget) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = "Status Anggaran",
                            tint = if (isOverBudget) AlertRed else MintGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isOverBudget) "⚠️ PERINGATAN: MELEBIHI ANGGARAN" else "Batas Anggaran $periodLabel",
                            color = if (isOverBudget) AlertRed else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = if (isOverBudget) 0.5.sp else 0.sp
                        )
                        Text(
                            text = if (isOverBudget) "Pengeluaran melampaui batas yang ditentukan" else "Status pengeluaran terkendali",
                            color = if (isOverBudget) AlertRed.copy(alpha = 0.85f) else TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOverBudget) AlertRed.copy(alpha = 0.3f) else DarkBorder.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$percentageUsed%",
                            color = if (isOverBudget) AlertRed else MintGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Terpakai: ${Formatters.formatRupiah(currentPeriodExpense)}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Batas: ${Formatters.formatRupiah(budgetConfig.amount)} (${budgetConfig.period.label})",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (isOverBudget) {
                        val overAmount = currentPeriodExpense - budgetConfig.amount
                        Text(
                            text = "Kelebihan: +${Formatters.formatRupiah(overAmount)}",
                            color = AlertRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Harap batasi belanja",
                            color = AlertRed.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    } else {
                        val remaining = budgetConfig.amount - currentPeriodExpense
                        Text(
                            text = "Sisa: ${Formatters.formatRupiah(remaining)}",
                            color = MintGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kondisi stabil & aman",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Warning Action Box if over budget
            AnimatedVisibility(
                visible = isOverBudget,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AlertRed.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Pengeluaran ${budgetConfig.period.shortLabel} telah melampaui batas anggaran. Evaluasi pengeluaran atau sesuaikan batas anggaran Anda.",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
