package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.model.CategoryDefaults
import com.example.data.model.TransactionType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    initialTransaction: TransactionEntity?,
    monthlyIncome: Double,
    onDismiss: () -> Unit,
    onSave: (id: Long, title: String, amount: Double, type: TransactionType, categoryId: String, categoryName: String, dateMillis: Long, note: String) -> Unit,
    onDelete: (TransactionEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var transactionType by remember {
        mutableStateOf(initialTransaction?.type ?: TransactionType.EXPENSE)
    }
    var title by remember {
        mutableStateOf(initialTransaction?.title ?: "")
    }
    var amountText by remember {
        mutableStateOf(if (initialTransaction != null) initialTransaction.amount.toLong().toString() else "")
    }
    var selectedCategoryId by remember {
        mutableStateOf(
            initialTransaction?.categoryId ?: if (transactionType == TransactionType.EXPENSE) "food" else "salary"
        )
    }
    var selectedDateMillis by remember {
        mutableStateOf(initialTransaction?.dateMillis ?: System.currentTimeMillis())
    }
    var note by remember {
        mutableStateOf(initialTransaction?.note ?: "")
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val categories = if (transactionType == TransactionType.EXPENSE) {
        CategoryDefaults.expenseCategories
    } else {
        CategoryDefaults.incomeCategories
    }

    // Amount value
    val amountValue = amountText.toDoubleOrNull() ?: 0.0

    // Auto percentage calculation
    val percentageOfIncome = if (transactionType == TransactionType.EXPENSE && monthlyIncome > 0 && amountValue > 0) {
        (amountValue / monthlyIncome * 100)
    } else 0.0

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih", color = MintGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTransaction == null) "Tambah Transaksi Baru" else "Edit Transaksi",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Type Segmented Switch (Pemasukan / Pengeluaran)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .padding(4.dp)
            ) {
                // Expense Option
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            transactionType = TransactionType.EXPENSE
                            selectedCategoryId = "food"
                        }
                        .testTag("type_expense_btn"),
                    color = if (transactionType == TransactionType.EXPENSE) RoseExpense.copy(alpha = 0.25f) else Color.Transparent
                ) {
                    Text(
                        text = "Pengeluaran",
                        color = if (transactionType == TransactionType.EXPENSE) RoseExpenseLight else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                // Income Option
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            transactionType = TransactionType.INCOME
                            selectedCategoryId = "salary"
                        }
                        .testTag("type_income_btn"),
                    color = if (transactionType == TransactionType.INCOME) MintGreen.copy(alpha = 0.25f) else Color.Transparent
                ) {
                    Text(
                        text = "Pemasukan",
                        color = if (transactionType == TransactionType.INCOME) MintGreenLight else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nominal Input
            Text(
                text = "Nominal (Rupiah)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        amountText = input
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount"),
                prefix = {
                    Text(
                        text = "Rp ",
                        color = if (transactionType == TransactionType.EXPENSE) RoseExpenseLight else MintGreenLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                placeholder = { Text("0", color = TextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (transactionType == TransactionType.EXPENSE) RoseExpense else MintGreen,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Quick Nominal Chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(20000L to "20rb", 50000L to "50rb", 100000L to "100rb", 500000L to "500rb").forEach { (value, label) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceHover,
                        modifier = Modifier
                            .clickable {
                                val current = amountText.toLongOrNull() ?: 0L
                                amountText = (current + value).toString()
                            }
                    ) {
                        Text(
                            text = "+$label",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Real-time automatic percentage calculation preview
            if (transactionType == TransactionType.EXPENSE && monthlyIncome > 0 && amountValue > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanAccent.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "⚡ Otomatis dihitung: Nominal ini mengambil ${String.format("%.1f", percentageOfIncome)}% dari total pemasukan bulan ini (${Formatters.formatRupiah(monthlyIncome)}).",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nama Transaksi Input
            Text(
                text = "Nama / Keterangan Transaksi",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_title"),
                placeholder = {
                    Text(
                        if (transactionType == TransactionType.EXPENSE) "Contoh: Makan Siang, Bensin..." else "Contoh: Gaji, Usaha...",
                        color = TextSecondary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Kategori Selector Grid
            Text(
                text = "Pilih Kategori",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategoryId == cat.id
                    val icon = CategoryIconHelper.getIcon(cat.iconName)
                    val catColor = Color(cat.colorHex)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) catColor.copy(alpha = 0.25f) else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) catColor else DarkBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedCategoryId = cat.id }
                            .testTag("category_chip_${cat.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = cat.name,
                                tint = if (isSelected) catColor else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.name,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tanggal Transaksi Picker
            Text(
                text = "Tanggal Transaksi",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .clickable { showDatePicker = true }
                    .testTag("select_date_btn"),
                color = DarkSurfaceElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = Formatters.formatDateFull(selectedDateMillis),
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Pilih Tanggal",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Catatan Tambahan (Optional)
            Text(
                text = "Catatan Tambahan (Opsional)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_note"),
                placeholder = { Text("Tuliskan catatan singkat jika ada...", color = TextSecondary) },
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (initialTransaction != null) {
                    OutlinedButton(
                        onClick = { onDelete(initialTransaction) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("delete_transaction_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        val finalCategory = CategoryDefaults.getCategory(selectedCategoryId, transactionType)
                        val finalAmount = amountText.toDoubleOrNull() ?: 0.0
                        if (finalAmount > 0) {
                            onSave(
                                initialTransaction?.id ?: 0L,
                                title.ifBlank { finalCategory.name },
                                finalAmount,
                                transactionType,
                                finalCategory.id,
                                finalCategory.name,
                                selectedDateMillis,
                                note
                            )
                        }
                    },
                    enabled = amountValue > 0,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_transaction_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == TransactionType.EXPENSE) RoseExpense else MintGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (initialTransaction == null) "Simpan Transaksi" else "Perbarui Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
