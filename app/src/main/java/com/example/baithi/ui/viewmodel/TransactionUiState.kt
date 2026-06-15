package com.example.baithi.ui.viewmodel

import com.example.baithi.data.local.entities.Category
import com.example.baithi.data.local.entities.Transaction

data class CategoryStat(
    val categoryName: String,
    val isExpense: Boolean,
    val total: Double,
    val avg: Double,
    val max: Double,
    val min: Double,
    val count: Int
)

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    
    // Thống kê chung Thu nhập
    val avgIncome: Double = 0.0,
    val maxIncome: Double = 0.0,
    val minIncome: Double = 0.0,
    
    // Thống kê chung Chi tiêu
    val avgExpense: Double = 0.0,
    val maxExpense: Double = 0.0,
    val minExpense: Double = 0.0,
    
    // Thống kê theo từng danh mục
    val categoryStats: List<CategoryStat> = emptyList(),

    val monthlyBudget: Double = 5000000.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMonth: Int = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
    val selectedYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
)
