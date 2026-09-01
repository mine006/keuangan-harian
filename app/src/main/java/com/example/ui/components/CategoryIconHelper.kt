package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            "Restaurant" -> Icons.Default.Restaurant
            "DirectionsCar" -> Icons.Default.DirectionsCar
            "ShoppingBag" -> Icons.Default.ShoppingBag
            "Receipt" -> Icons.Default.Receipt
            "SportsEsports" -> Icons.Default.SportsEsports
            "LocalHospital" -> Icons.Default.LocalHospital
            "School" -> Icons.Default.School
            "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
            "Storefront" -> Icons.Default.Storefront
            "CardGiftcard" -> Icons.Default.CardGiftcard
            "TrendingUp" -> Icons.Default.TrendingUp
            "LaptopMac" -> Icons.Default.LaptopMac
            "AddCircleOutline" -> Icons.Default.AddCircleOutline
            "MoreHoriz" -> Icons.Default.MoreHoriz
            else -> Icons.Default.Category
        }
    }
}
