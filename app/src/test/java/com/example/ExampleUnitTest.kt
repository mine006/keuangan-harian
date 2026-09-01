package com.example

import com.example.data.model.BudgetPeriod
import com.example.util.Formatters
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun formatRupiah_formatsCorrectly() {
    val formatted = Formatters.formatRupiah(1500000.0)
    assertTrue(formatted.contains("1.500.000") || formatted.contains("1,500,000"))
  }

  @Test
  fun percentageCalculation_isAccurate() {
    val income = 10000000.0
    val expense = 3500000.0
    val percentage = (expense / income) * 100.0
    assertEquals(35.0, percentage, 0.001)
  }

  @Test
  fun overBudget_logicCorrect() {
    val budgetLimit = 5000000.0
    val currentExpense = 5200000.0
    val isOverBudget = currentExpense > budgetLimit
    assertTrue(isOverBudget)
  }
}
