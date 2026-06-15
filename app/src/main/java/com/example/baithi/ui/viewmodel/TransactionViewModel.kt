package com.example.baithi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.baithi.data.local.AppDatabase
import com.example.baithi.data.local.entities.Category
import com.example.baithi.data.local.entities.Transaction
import com.example.baithi.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepositoryImpl
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepositoryImpl(db.transactionDao())
        
        observeData()
    }

    private fun observeData() {
        combine(
            repository.getAllTransactions(),
            repository.getAllCategories()
        ) { transactions, categories ->
            _uiState.update { it.copy(categories = categories, allTransactions = transactions) }
            filterAndCalculateStats()
        }.launchIn(viewModelScope)
    }

    fun onMonthYearSelected(month: Int, year: Int) {
        _uiState.update { it.copy(selectedMonth = month, selectedYear = year) }
        filterAndCalculateStats()
    }

    private fun filterAndCalculateStats() {
        val state = _uiState.value
        val calendar = java.util.Calendar.getInstance()
        
        val filteredTransactions = state.allTransactions.filter { t ->
            calendar.timeInMillis = t.date
            val m = calendar.get(java.util.Calendar.MONTH)
            val y = calendar.get(java.util.Calendar.YEAR)
            m == state.selectedMonth && y == state.selectedYear
        }

        calculateStats(filteredTransactions, state.categories)
    }

    private fun calculateStats(transactions: List<Transaction>, categories: List<Category>) {
        if (transactions.isEmpty()) {
            _uiState.update { 
                it.copy(
                    transactions = emptyList(),
                    totalIncome = 0.0,
                    totalExpense = 0.0,
                    balance = 0.0,
                    avgIncome = 0.0,
                    maxIncome = 0.0,
                    minIncome = 0.0,
                    avgExpense = 0.0,
                    maxExpense = 0.0,
                    minExpense = 0.0,
                    categoryStats = emptyList()
                ) 
            }
            return
        }

        // Tách riêng Thu nhập và Chi tiêu
        val incomeTransactions = transactions.filter { t -> 
            categories.find { it.id == t.categoryId }?.isExpense == false 
        }
        
        val expenseTransactions = transactions.filter { t -> 
            categories.find { it.id == t.categoryId }?.isExpense == true 
        }

        val totalIncome = incomeTransactions.sumOf { it.amount }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        // Thống kê thu nhập
        val avgIncome = if (incomeTransactions.isNotEmpty()) incomeTransactions.map { it.amount }.average() else 0.0
        val maxIncome = if (incomeTransactions.isNotEmpty()) incomeTransactions.maxOf { it.amount } else 0.0
        val minIncome = if (incomeTransactions.isNotEmpty()) incomeTransactions.minOf { it.amount } else 0.0

        // Thống kê chi tiêu
        val avgExpense = if (expenseTransactions.isNotEmpty()) expenseTransactions.map { it.amount }.average() else 0.0
        val maxExpense = if (expenseTransactions.isNotEmpty()) expenseTransactions.maxOf { it.amount } else 0.0
        val minExpense = if (expenseTransactions.isNotEmpty()) expenseTransactions.minOf { it.amount } else 0.0

        // Thống kê theo từng danh mục
        val categoryStats = transactions.groupBy { it.categoryId }.map { (catId, transList) ->
            val cat = categories.find { it.id == catId }
            CategoryStat(
                categoryName = cat?.name ?: "Khác",
                isExpense = cat?.isExpense ?: true,
                total = transList.sumOf { it.amount },
                avg = transList.map { it.amount }.average(),
                max = transList.maxOf { it.amount },
                min = transList.minOf { it.amount },
                count = transList.size
            )
        }.sortedByDescending { it.total }

        _uiState.update { 
            it.copy(
                transactions = transactions,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = totalIncome - totalExpense,
                avgIncome = avgIncome,
                maxIncome = maxIncome,
                minIncome = minIncome,
                avgExpense = avgExpense,
                maxExpense = maxExpense,
                minExpense = minExpense,
                categoryStats = categoryStats
            ) 
        }
    }

    fun clearAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val transactionsToDelete = _uiState.value.transactions
            if (transactionsToDelete.isEmpty()) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(getApplication(), "Không có giao dịch nào để xóa", android.widget.Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            transactionsToDelete.forEach {
                repository.deleteTransaction(it)
            }
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã xóa tất cả giao dịch trong tháng", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addTransaction(title: String, amount: Double, date: Long, categoryId: Long, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = Transaction(title = title, amount = amount, date = date, categoryId = categoryId, note = note)
            repository.insertTransaction(transaction)
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã thêm giao dịch thành công", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addTransactionWithCategoryName(title: String, amount: Double, date: Long, categoryName: String, isExpense: Boolean, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = _uiState.value.categories.find { it.name.equals(categoryName, true) && it.isExpense == isExpense }
            val catId: Long = if (existing != null) {
                existing.id
            } else {
                repository.insertCategory(Category(name = categoryName, iconName = "category", isExpense = isExpense))
            }
            val transaction = Transaction(title = title, amount = amount, date = date, categoryId = catId, note = note)
            repository.insertTransaction(transaction)
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã thêm giao dịch thành công", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTransaction(id: Long, title: String, amount: Double, date: Long, categoryId: Long, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = Transaction(id = id, title = title, amount = amount, date = date, categoryId = categoryId, note = note)
            repository.updateTransaction(transaction)
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã cập nhật giao dịch", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTransactionWithCategoryName(id: Long, title: String, amount: Double, date: Long, categoryName: String, isExpense: Boolean, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = _uiState.value.categories.find { it.name.equals(categoryName, true) && it.isExpense == isExpense }
            val catId: Long = if (existing != null) {
                existing.id
            } else {
                repository.insertCategory(Category(name = categoryName, iconName = "category", isExpense = isExpense))
            }
            val transaction = Transaction(id = id, title = title, amount = amount, date = date, categoryId = catId, note = note)
            repository.updateTransaction(transaction)
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã cập nhật giao dịch", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(getApplication(), "Đã xóa giao dịch", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exportToJson(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transactions = _uiState.value.transactions
                val categories = _uiState.value.categories
                
                val jsonArray = org.json.JSONArray()
                transactions.forEach { t ->
                    val cat = categories.find { it.id == t.categoryId }
                    val obj = org.json.JSONObject().apply {
                        put("title", t.title)
                        put("amount", t.amount)
                        put("date", t.date)
                        put("category", cat?.name ?: "Khác")
                        put("isExpense", cat?.isExpense ?: true)
                        put("note", t.note ?: org.json.JSONObject.NULL)
                    }
                    jsonArray.put(obj)
                }
                
                val jsonString = jsonArray.toString(2)
                
                withContext(Dispatchers.Main) {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, jsonString)
                    }
                    val chooser = android.content.Intent.createChooser(shareIntent, "Xuất dữ liệu JSON")
                    chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                    android.widget.Toast.makeText(context, "Đã chuẩn bị dữ liệu JSON để chia sẻ", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Lỗi xuất JSON: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    fun importFromJson(jsonString: String, context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonArray = org.json.JSONArray(jsonString)
                val allCategories = repository.getAllCategories().first().toMutableList()
                var count = 0
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val title = obj.getString("title")
                    val amount = obj.getDouble("amount")
                    val date = obj.getLong("date")
                    val categoryName = obj.getString("category")
                    val isExpense = obj.optBoolean("isExpense", true)
                    val note = if (obj.isNull("note")) null else obj.getString("note")

                    // Tìm hoặc tạo category
                    val existing = allCategories.find { 
                        it.name.equals(categoryName, true) && it.isExpense == isExpense 
                    }
                    val catId: Long = if (existing != null) {
                        existing.id
                    } else {
                        val newCat = Category(name = categoryName, iconName = "category", isExpense = isExpense)
                        val id = repository.insertCategory(newCat)
                        allCategories.add(newCat.copy(id = id))
                        id
                    }

                    val transaction = Transaction(title = title, amount = amount, date = date, categoryId = catId, note = note)
                    repository.insertTransaction(transaction)
                    count++
                }
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Đã nhập thành công $count giao dịch", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Lỗi nhập JSON: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun insertInitialCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val initialCategories = listOf(
                Category(name = "Ăn uống", iconName = "restaurant", isExpense = true),
                Category(name = "Di chuyển", iconName = "directions_bus", isExpense = true),
                Category(name = "Tiền lương", iconName = "payments", isExpense = false),
                Category(name = "Thưởng", iconName = "redeem", isExpense = false)
            )
            initialCategories.forEach { repository.insertCategory(it) }
        }
    }

    fun exportToCSV(applicationContext: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val transactions = _uiState.value.transactions
                val csvContent = StringBuilder("Title,Amount,Date,Note\n")
                transactions.forEach { 
                    csvContent.append("${it.title},${it.amount},${it.date},${it.note ?: ""}\n")
                }
                
                val file = java.io.File(applicationContext.filesDir, "transactions.csv")
                file.writeText(csvContent.toString())
                
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, error = "Exported to ${file.absolutePath}") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, error = "Export failed: ${e.message}") }
                }
            }
        }
    }
}
