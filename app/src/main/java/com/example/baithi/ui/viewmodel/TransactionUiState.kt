package com.example.baithi.ui.viewmodel

import com.example.baithi.data.local.entities.Category
import com.example.baithi.data.local.entities.Transaction

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val avgTransaction: Double = 0.0,
    val maxTransaction: Double = 0.0,
    val minTransaction: Double = 0.0,
    val monthlyBudget: Double = 5000000.0, // Default budget: 5,000,000 VND
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMonth: Int = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
    val selectedYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
)
