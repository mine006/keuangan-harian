package com.example.data.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class BudgetPeriod(val label: String, val shortLabel: String) {
    WEEKLY("Mingguan", "Minggu ini"),
    MONTHLY("Bulanan", "Bulan ini")
}

data class TransactionCategory(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconName: String,
    val colorHex: Long
)

object CategoryDefaults {
    val expenseCategories = listOf(
        TransactionCategory("food", "Makanan & Minuman", TransactionType.EXPENSE, "Restaurant", 0xFFF59E0B),
        TransactionCategory("transport", "Transportasi", TransactionType.EXPENSE, "DirectionsCar", 0xFF3B82F6),
        TransactionCategory("shopping", "Belanja & Kebutuhan", TransactionType.EXPENSE, "ShoppingBag", 0xFFEC4899),
        TransactionCategory("bills", "Tagihan & Utilitas", TransactionType.EXPENSE, "Receipt", 0xFF8B5CF6),
        TransactionCategory("entertainment", "Hiburan & Rekreasi", TransactionType.EXPENSE, "SportsEsports", 0xFF10B981),
        TransactionCategory("health", "Kesehatan & Medis", TransactionType.EXPENSE, "LocalHospital", 0xFFEF4444),
        TransactionCategory("education", "Pendidikan", TransactionType.EXPENSE, "School", 0xFF06B6D4),
        TransactionCategory("other_expense", "Pengeluaran Lain", TransactionType.EXPENSE, "MoreHoriz", 0xFF64748B)
    )

    val incomeCategories = listOf(
        TransactionCategory("salary", "Gaji & Upah", TransactionType.INCOME, "AccountBalanceWallet", 0xFF10B981),
        TransactionCategory("business", "Bisnis & Usaha", TransactionType.INCOME, "Storefront", 0xFF06B6D4),
        TransactionCategory("bonus", "Bonus & Hadiah", TransactionType.INCOME, "CardGiftcard", 0xFFF59E0B),
        TransactionCategory("investment", "Investasi & Dividen", TransactionType.INCOME, "TrendingUp", 0xFF8B5CF6),
        TransactionCategory("freelance", "Proyek Freelance", TransactionType.INCOME, "LaptopMac", 0xFF3B82F6),
        TransactionCategory("other_income", "Pemasukan Lain", TransactionType.INCOME, "AddCircleOutline", 0xFF64748B)
    )

    fun getCategory(categoryId: String, type: TransactionType): TransactionCategory {
        val list = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories
        return list.find { it.id == categoryId } ?: list.last()
    }
}
