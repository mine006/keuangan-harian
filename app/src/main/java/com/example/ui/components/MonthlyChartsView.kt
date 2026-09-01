package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BudgetConfig
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
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.CategoryExpenseSummary
import com.example.ui.viewmodel.DailyChartItem
import com.example.util.Formatters
import java.util.Calendar

enum class LineChartFilterMode(val label: String) {
    ALL("Semua Arus"),
    EXPENSE_ONLY("Pengeluaran"),
    INCOME_ONLY("Pemasukan")
}

@Composable
fun MonthlyChartsView(
    selectedMonth: Calendar,
    monthlyIncome: Double,
    monthlyExpense: Double,
    monthlyNetBalance: Double,
    expenseToIncomeRatio: Double,
    dailyChartData: List<DailyChartItem>,
    categorySummaries: List<CategoryExpenseSummary>,
    budgetConfig: BudgetConfig,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterMode by remember { mutableStateOf(LineChartFilterMode.ALL) }
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    // Reset selected day when month changes
    val currentMonthKey = remember(selectedMonth) {
        "${selectedMonth.get(Calendar.YEAR)}-${selectedMonth.get(Calendar.MONTH)}"
    }
    remember(currentMonthKey) {
        selectedDayIndex = -1
        true
    }

    val selectedDayItem = if (selectedDayIndex in dailyChartData.indices) {
        dailyChartData[selectedDayIndex]
    } else {
        null
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Month Selector Header
        item {
            MonthSelectorCard(
                calendar = selectedMonth,
                onPrev = {
                    selectedDayIndex = -1
                    onPrevMonth()
                },
                onNext = {
                    selectedDayIndex = -1
                    onNextMonth()
                }
            )
        }

        // 2. Summary Mini Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Income Mini Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = MintGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Total Masuk", color = TextSecondary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Formatters.formatRupiah(monthlyIncome),
                            color = MintGreenLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Expense Mini Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = RoseExpense,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Total Keluar", color = TextSecondary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Formatters.formatRupiah(monthlyExpense),
                            color = RoseExpenseLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Ratio Mini Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Rasio Beban", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%.1f", expenseToIncomeRatio)}%",
                            color = if (expenseToIncomeRatio > 100) AlertRed else if (expenseToIncomeRatio > 80) AmberWarning else CyanAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 3. Primary Line Chart Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
                color = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header & Filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Grafik Tren Garis Harian",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Geser atau sentuh titik untuk rincian tanggal",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Line Mode Toggle Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LineChartFilterMode.values().forEach { mode ->
                            val isSelected = selectedFilterMode == mode
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedFilterMode = mode },
                                color = if (isSelected) {
                                    when (mode) {
                                        LineChartFilterMode.ALL -> CyanAccent.copy(alpha = 0.2f)
                                        LineChartFilterMode.EXPENSE_ONLY -> RoseExpense.copy(alpha = 0.2f)
                                        LineChartFilterMode.INCOME_ONLY -> MintGreen.copy(alpha = 0.2f)
                                    }
                                } else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        when (mode) {
                                            LineChartFilterMode.ALL -> CyanAccent
                                            LineChartFilterMode.EXPENSE_ONLY -> RoseExpense
                                            LineChartFilterMode.INCOME_ONLY -> MintGreen
                                        }
                                    } else DarkBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (mode) {
                                                    LineChartFilterMode.ALL -> CyanAccent
                                                    LineChartFilterMode.EXPENSE_ONLY -> RoseExpense
                                                    LineChartFilterMode.INCOME_ONLY -> MintGreen
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = mode.label,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // The Ultra-Stable Custom Line Chart Canvas
                    StableLineChart(
                        dailyData = dailyChartData,
                        filterMode = selectedFilterMode,
                        selectedIndex = selectedDayIndex,
                        onSelectIndex = { index ->
                            selectedDayIndex = index
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .testTag("stable_line_chart")
                    )

                    // Selected Day Detail Box
                    AnimatedVisibility(
                        visible = selectedDayItem != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        selectedDayItem?.let { day ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSurfaceHover)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Tanggal ${day.dayNumber} ${Formatters.formatMonthYear(selectedMonth)}",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Masuk: ${Formatters.formatRupiah(day.incomeAmount)}",
                                            color = MintGreenLight,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Keluar: ${Formatters.formatRupiah(day.expenseAmount)}",
                                            color = RoseExpenseLight,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        val net = day.incomeAmount - day.expenseAmount
                                        Text(
                                            text = "Sisa Harian: ${if (net >= 0) "+" else ""}${Formatters.formatRupiah(net)}",
                                            color = if (net >= 0) TextSecondary else AlertRed,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Category Breakdown List
        if (categorySummaries.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rincian Kategori Pengeluaran (${categorySummaries.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            items(categorySummaries) { cat ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                    color = DarkSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(cat.colorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cat.categoryName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${cat.transactionCount} transaksi",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Formatters.formatRupiah(cat.amount),
                                color = RoseExpenseLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${String.format("%.1f", cat.percentage)}% dr total keluar",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Financial Ratio & Cashflow Insight Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (expenseToIncomeRatio > 100) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = "Insight",
                            tint = if (expenseToIncomeRatio > 100) AlertRed else MintGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analisis Rasio Keuangan",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val insightText = when {
                        monthlyIncome == 0.0 && monthlyExpense > 0 ->
                            "Belum ada catatan pemasukan bulan ini. Catat gaji atau pemasukan usaha Anda untuk mengukur rasio kesehatan finansial secara akurat."
                        expenseToIncomeRatio > 100 ->
                            "Pengeluaran bulan ini melampaui 100% total pemasukan (Defisit). Anda menggunakan tabungan atau dana cadangan sebesar ${Formatters.formatRupiah(monthlyExpense - monthlyIncome)}."
                        expenseToIncomeRatio > 80 ->
                            "Pengeluaran telah mencapai ${String.format("%.1f", expenseToIncomeRatio)}% dari pemasukan. Tingkat tabungan menipis, pertimbangkan untuk menunda pembelian non-pokok."
                        expenseToIncomeRatio > 50 ->
                            "Pengeluaran stabil di ${String.format("%.1f", expenseToIncomeRatio)}% dari pemasukan. Anda memiliki ruang tabungan sekitar ${String.format("%.1f", 100 - expenseToIncomeRatio)}%."
                        else ->
                            "Luar biasa! Pengeluaran hanya ${String.format("%.1f", expenseToIncomeRatio)}% dari pemasukan. Anda memiliki kapasitas tabungan dan investasi sangat sehat."
                    }

                    Text(
                        text = insightText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // 6. Copyright
        item {
            SimpleCopyrightText()
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun MonthSelectorCard(
    calendar: Calendar,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        color = DarkSurfaceElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrev,
                modifier = Modifier.testTag("prev_month_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Bulan Sebelumnya",
                    tint = TextPrimary
                )
            }

            Text(
                text = Formatters.formatMonthYear(calendar),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            IconButton(
                onClick = onNext,
                modifier = Modifier.testTag("next_month_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Bulan Berikutnya",
                    tint = TextPrimary
                )
            }
        }
    }
}

/**
 * High-performance, rock-solid, responsive Line Chart with anti-aliased paths,
 * gradient shadow areas, interactive scrub support, and zero-safe coordinate calculations.
 */
@Composable
fun StableLineChart(
    dailyData: List<DailyChartItem>,
    filterMode: LineChartFilterMode,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dailyData.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada data untuk ditampilkan",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        return
    }

    val maxVal = remember(dailyData, filterMode) {
        val peak = dailyData.maxOfOrNull { item ->
            when (filterMode) {
                LineChartFilterMode.ALL -> maxOf(item.incomeAmount, item.expenseAmount)
                LineChartFilterMode.EXPENSE_ONLY -> item.expenseAmount
                LineChartFilterMode.INCOME_ONLY -> item.incomeAmount
            }
        } ?: 0.0
        if (peak <= 0.0) 100000.0 else peak * 1.15 // 15% headroom for clean breathing room
    }

    Canvas(
        modifier = modifier
            .pointerInput(dailyData) {
                detectTapGestures { offset ->
                    val total = dailyData.size
                    if (total > 0) {
                        val leftPadding = 12f
                        val rightPadding = 12f
                        val availableWidth = (size.width - leftPadding - rightPadding).coerceAtLeast(1f)
                        val relativeX = (offset.x - leftPadding).coerceIn(0f, availableWidth)
                        val idx = ((relativeX / availableWidth) * (total - 1)).toInt().coerceIn(0, total - 1)
                        onSelectIndex(idx)
                    }
                }
            }
            .pointerInput(dailyData) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val total = dailyData.size
                    if (total > 0) {
                        val leftPadding = 12f
                        val rightPadding = 12f
                        val availableWidth = (size.width - leftPadding - rightPadding).coerceAtLeast(1f)
                        val relativeX = (change.position.x - leftPadding).coerceIn(0f, availableWidth)
                        val idx = ((relativeX / availableWidth) * (total - 1)).toInt().coerceIn(0, total - 1)
                        onSelectIndex(idx)
                    }
                }
            }
    ) {
        val total = dailyData.size
        val leftPad = 12f
        val rightPad = 12f
        val topPad = 16f
        val bottomPad = 28f // room for day labels

        val chartWidth = (size.width - leftPad - rightPad).coerceAtLeast(1f)
        val chartHeight = (size.height - topPad - bottomPad).coerceAtLeast(1f)

        // 1. Draw 4 horizontal dashed reference gridlines
        val gridCount = 3
        for (g in 0..gridCount) {
            val y = topPad + chartHeight * (g.toFloat() / gridCount)
            drawLine(
                color = Color(0xFF334155).copy(alpha = 0.45f),
                start = Offset(leftPad, y),
                end = Offset(size.width - rightPad, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        }

        // Helper to compute (x, y) coordinate safely
        fun getX(index: Int): Float {
            return if (total <= 1) {
                leftPad + chartWidth / 2f
            } else {
                leftPad + (index.toFloat() / (total - 1)) * chartWidth
            }
        }

        fun getY(amount: Double): Float {
            val ratio = (amount / maxVal).coerceIn(0.0, 1.0)
            return (topPad + chartHeight - (ratio * chartHeight).toFloat())
        }

        // 2. Draw Income Line & Gradient Fill (if enabled)
        if (filterMode == LineChartFilterMode.ALL || filterMode == LineChartFilterMode.INCOME_ONLY) {
            val incomePoints = dailyData.mapIndexed { index, item ->
                Offset(getX(index), getY(item.incomeAmount))
            }

            // Fill under curve
            if (incomePoints.size > 1) {
                val fillPath = Path().apply {
                    moveTo(incomePoints.first().x, topPad + chartHeight)
                    lineTo(incomePoints.first().x, incomePoints.first().y)
                    for (i in 1 until incomePoints.size) {
                        val p0 = incomePoints[i - 1]
                        val p1 = incomePoints[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                    lineTo(incomePoints.last().x, topPad + chartHeight)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MintGreen.copy(alpha = 0.22f),
                            MintGreen.copy(alpha = 0.01f)
                        ),
                        startY = topPad,
                        endY = topPad + chartHeight
                    )
                )
            }

            // Line curve
            if (incomePoints.size > 1) {
                val linePath = Path().apply {
                    moveTo(incomePoints.first().x, incomePoints.first().y)
                    for (i in 1 until incomePoints.size) {
                        val p0 = incomePoints[i - 1]
                        val p1 = incomePoints[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = MintGreen,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Income data dots
            incomePoints.forEachIndexed { idx, pt ->
                if (dailyData[idx].incomeAmount > 0) {
                    drawCircle(
                        color = DarkSurface,
                        radius = 4f,
                        center = pt
                    )
                    drawCircle(
                        color = MintGreen,
                        radius = 2.5f,
                        center = pt
                    )
                }
            }
        }

        // 3. Draw Expense Line & Gradient Fill (if enabled)
        if (filterMode == LineChartFilterMode.ALL || filterMode == LineChartFilterMode.EXPENSE_ONLY) {
            val expensePoints = dailyData.mapIndexed { index, item ->
                Offset(getX(index), getY(item.expenseAmount))
            }

            // Fill under curve
            if (expensePoints.size > 1) {
                val fillPath = Path().apply {
                    moveTo(expensePoints.first().x, topPad + chartHeight)
                    lineTo(expensePoints.first().x, expensePoints.first().y)
                    for (i in 1 until expensePoints.size) {
                        val p0 = expensePoints[i - 1]
                        val p1 = expensePoints[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                    lineTo(expensePoints.last().x, topPad + chartHeight)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            RoseExpense.copy(alpha = 0.25f),
                            RoseExpense.copy(alpha = 0.01f)
                        ),
                        startY = topPad,
                        endY = topPad + chartHeight
                    )
                )
            }

            // Line curve
            if (expensePoints.size > 1) {
                val linePath = Path().apply {
                    moveTo(expensePoints.first().x, expensePoints.first().y)
                    for (i in 1 until expensePoints.size) {
                        val p0 = expensePoints[i - 1]
                        val p1 = expensePoints[i]
                        val cx = (p0.x + p1.x) / 2f
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = RoseExpense,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Expense data dots
            expensePoints.forEachIndexed { idx, pt ->
                if (dailyData[idx].expenseAmount > 0) {
                    drawCircle(
                        color = DarkSurface,
                        radius = 4f,
                        center = pt
                    )
                    drawCircle(
                        color = RoseExpense,
                        radius = 2.5f,
                        center = pt
                    )
                }
            }
        }

        // 4. X-Axis Day Markers (e.g. 1, 5, 10, 15, 20, 25, end of month)
        dailyData.forEachIndexed { idx, item ->
            val isMarker = item.dayNumber == 1 || item.dayNumber % 5 == 0 || item.dayNumber == total
            if (isMarker) {
                val markerX = getX(idx)
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(markerX, topPad + chartHeight + 2f),
                    end = Offset(markerX, topPad + chartHeight + 7f),
                    strokeWidth = 1.5f
                )
            }
        }

        // 5. Active selected day vertical indicator line & focus rings
        if (selectedIndex in dailyData.indices) {
            val selX = getX(selectedIndex)
            val selItem = dailyData[selectedIndex]

            // Vertical indicator beam
            drawLine(
                color = CyanAccent.copy(alpha = 0.8f),
                start = Offset(selX, topPad),
                end = Offset(selX, topPad + chartHeight + 10f),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Focus point for income
            if ((filterMode == LineChartFilterMode.ALL || filterMode == LineChartFilterMode.INCOME_ONLY) && selItem.incomeAmount > 0) {
                val incY = getY(selItem.incomeAmount)
                drawCircle(color = MintGreen.copy(alpha = 0.3f), radius = 9f, center = Offset(selX, incY))
                drawCircle(color = Color.White, radius = 5f, center = Offset(selX, incY))
                drawCircle(color = MintGreen, radius = 3.5f, center = Offset(selX, incY))
            }

            // Focus point for expense
            if ((filterMode == LineChartFilterMode.ALL || filterMode == LineChartFilterMode.EXPENSE_ONLY) && selItem.expenseAmount > 0) {
                val expY = getY(selItem.expenseAmount)
                drawCircle(color = RoseExpense.copy(alpha = 0.3f), radius = 9f, center = Offset(selX, expY))
                drawCircle(color = Color.White, radius = 5f, center = Offset(selX, expY))
                drawCircle(color = RoseExpense, radius = 3.5f, center = Offset(selX, expY))
            }
        }
    }
}
