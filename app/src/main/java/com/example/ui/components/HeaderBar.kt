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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.UserProfile
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeaderBar(
    userProfile: UserProfile,
    isOverBudget: Boolean,
    onOpenProfile: () -> Unit,
    onOpenBudgetDialog: () -> Unit,
    onSendTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile Clickable
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onOpenProfile() }
                .padding(4.dp)
                .testTag("user_profile_header")
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
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
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (userProfile.isGoogleLinked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Google Terhubung",
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = if (userProfile.isGoogleLinked) "Akun Google Aktif" else "Kelola Akun",
                    color = if (userProfile.isGoogleLinked) CyanAccent else TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Test Notification Bell
            Surface(
                shape = CircleShape,
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = onSendTestNotification,
                    modifier = Modifier.testTag("test_notification_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Uji Notifikasi Anggaran",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Budget Config Button
            Surface(
                shape = CircleShape,
                color = if (isOverBudget) AlertRed.copy(alpha = 0.2f) else DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOverBudget) AlertRed else DarkBorder
                ),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = onOpenBudgetDialog,
                    modifier = Modifier.testTag("budget_config_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Atur Batas Anggaran",
                        tint = if (isOverBudget) AlertRed else MintGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
