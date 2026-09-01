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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.model.CategoryDefaults
import com.example.data.model.TransactionType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.RoseExpenseLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.Formatters

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    monthlyIncome: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = CategoryDefaults.getCategory(transaction.categoryId, transaction.type)
    val icon = CategoryIconHelper.getIcon(category.iconName)
    val catColor = Color(category.colorHex)

    val isExpense = transaction.type == TransactionType.EXPENSE
    val amountColor = if (isExpense) RoseExpenseLight else MintGreenLight
    val amountPrefix = if (isExpense) "- " else "+ "

    // Calculate percentage of this expense relative to monthly income
    val expensePercentage = if (isExpense && monthlyIncome > 0) {
        (transaction.amount / monthlyIncome * 100)
    } else 0.0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("transaction_item_${transaction.id}"),
        color = DarkSurfaceElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(catColor.copy(alpha = 0.15f))
                    .border(1.dp, catColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.name,
                    tint = catColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = transaction.categoryName,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "•",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = Formatters.formatRelativeDate(transaction.dateMillis),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.note,
                        color = TextTertiary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount & Percentage Tag
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = amountPrefix + Formatters.formatRupiah(transaction.amount),
                    color = amountColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                if (isExpense && monthlyIncome > 0 && expensePercentage >= 0.1) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${String.format("%.1f", expensePercentage)}% dr pemasukan",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
