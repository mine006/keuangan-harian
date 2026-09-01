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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.UserProfile
import com.example.data.model.BudgetPeriod
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHover
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.Formatters

@Composable
fun BudgetAndProfileView(
    budgetConfig: BudgetConfig,
    currentPeriodExpense: Double,
    isOverBudget: Boolean,
    userProfile: UserProfile,
    onOpenBudgetDialog: () -> Unit,
    onOpenProfileDialog: () -> Unit,
    onToggleGoogleLink: () -> Unit,
    onSendTestNotification: () -> Unit,
    onToggleWeeklyNotification: (Boolean) -> Unit,
    onToggleBudgetEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Akun & Integrasi Google
        item {
            Text(
                text = "Akun & Profil Pengguna",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
                color = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (userProfile.isGoogleLinked) CyanAccent.copy(alpha = 0.2f)
                                        else MintGreen.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (userProfile.isGoogleLinked) CyanAccent else MintGreen,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.name.take(1).uppercase(),
                                    color = if (userProfile.isGoogleLinked) CyanAccent else MintGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = userProfile.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = userProfile.email,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButtonCustom(
                            icon = Icons.Default.Edit,
                            contentDescription = "Edit Profil",
                            onClick = onOpenProfileDialog
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Account Link Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceHover)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "G",
                                            color = Color(0xFF4285F4),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Akun Google",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        if (userProfile.isGoogleLinked) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Terkait",
                                                tint = CyanAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (userProfile.isGoogleLinked) "Terkait: ${userProfile.email}" else "Tautkan akun untuk backup",
                                        color = if (userProfile.isGoogleLinked) CyanAccent else TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = onToggleGoogleLink,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (userProfile.isGoogleLinked) DarkBorder else CyanAccent.copy(alpha = 0.2f),
                                    contentColor = if (userProfile.isGoogleLinked) TextSecondary else CyanAccent
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("google_link_toggle_btn")
                            ) {
                                Text(
                                    text = if (userProfile.isGoogleLinked) "Putuskan" else "Tautkan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Pengaturan Batas Anggaran (Budget Limit)
        item {
            Text(
                text = "Batas Pengeluaran & Anggaran",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.5.dp,
                        if (isOverBudget) AlertRed else DarkBorder,
                        RoundedCornerShape(20.dp)
                    ),
                color = if (isOverBudget) AlertRedContainer else DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isOverBudget) AlertRed.copy(alpha = 0.2f)
                                        else MintGreen.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isOverBudget) Icons.Default.Warning else Icons.Default.Tune,
                                    contentDescription = "Batas Anggaran",
                                    tint = if (isOverBudget) AlertRed else MintGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Batas Pengeluaran ${budgetConfig.period.label}",
                                    color = if (isOverBudget) AlertRed else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (budgetConfig.isEnabled) Formatters.formatRupiah(budgetConfig.amount) else "Fitur dinonaktifkan",
                                    color = if (isOverBudget) AlertRed.copy(alpha = 0.9f) else TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = budgetConfig.isEnabled,
                            onCheckedChange = onToggleBudgetEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MintGreen,
                                checkedTrackColor = MintGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkBorder
                            ),
                            modifier = Modifier.testTag("budget_enable_switch")
                        )
                    }

                    if (budgetConfig.isEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Pengeluaran ${budgetConfig.period.shortLabel}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = Formatters.formatRupiah(currentPeriodExpense),
                                    color = if (isOverBudget) AlertRed else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isOverBudget) "Status: Melebihi Batas!" else "Status: Aman & Terkendali",
                                    color = if (isOverBudget) AlertRed else MintGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                val diff = budgetConfig.amount - currentPeriodExpense
                                Text(
                                    text = if (diff >= 0) "Sisa: ${Formatters.formatRupiah(diff)}" else "Kelebihan: +${Formatters.formatRupiah(-diff)}",
                                    color = if (isOverBudget) AlertRed.copy(alpha = 0.9f) else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onOpenBudgetDialog,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("change_budget_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOverBudget) AlertRed else MintGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Ubah Batas & Periode Anggaran",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: Notifikasi Pengingat Batas Anggaran Akhir Minggu
        item {
            Text(
                text = "Pengingat & Notifikasi",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
                color = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyanAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notifikasi",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pengingat Akhir Minggu",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Evaluasi batas anggaran setiap hari Minggu malam",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = budgetConfig.notifyWeekly,
                            onCheckedChange = onToggleWeeklyNotification,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyanAccent,
                                checkedTrackColor = CyanAccent.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkBorder
                            ),
                            modifier = Modifier.testTag("weekly_notification_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Sistem akan menghitung otomatis rasio pengeluaran dari jumlah pemasukan dan memberikan peringatan jika anggaran terlampaui.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onSendTestNotification,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_notification_action_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyanAccent
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kirim Notifikasi Pengingat Sekarang (Uji Coba)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Section: Tentang & Hak Cipta
        item {
            CopyrightCard()
        }

        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}

@Composable
private fun IconButtonCustom(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = DarkSurfaceHover,
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = CyanAccent,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
