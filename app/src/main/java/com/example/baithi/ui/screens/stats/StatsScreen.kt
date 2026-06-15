package com.example.baithi.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionUiState
import com.example.baithi.ui.viewmodel.TransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(
    uiState: TransactionUiState,
    onBack: () -> Unit,
    onDeleteAll: () -> Unit,
    onMonthSelected: (Int, Int) -> Unit
) {
    var showMonthPicker by remember { mutableStateOf(false) }
    var showTransactionList by remember { mutableStateOf(false) }
    var showCategoryStatsDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: Chi tiêu, 1: Thu nhập
    val filters = listOf("Chi tiêu", "Thu nhập")
    
    val months = listOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")
    
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear + 5).toList()

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Xác nhận xóa tất cả") },
            text = { Text("Bạn có chắc chắn muốn xóa tất cả giao dịch không? Hành động này không thể hoàn tác.") },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAll()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Có")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Không")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Thống Kê Thu Chi ",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showDeleteAllDialog = true }) {
                        Text("Xoá tất cả", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cảnh báo vượt ngân sách
            if (uiState.totalExpense > uiState.monthlyBudget) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Cảnh báo: Bạn đã vượt ngân sách chi tiêu tháng này (${String.format("%,.0f đ", uiState.monthlyBudget)})!",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Bộ chọn tháng
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { showMonthPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${months[uiState.selectedMonth]} - Năm ${uiState.selectedYear}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Default.ArrowDropDown, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (showMonthPicker) {
                var tempMonth by remember { mutableIntStateOf(uiState.selectedMonth) }
                var tempYear by remember { mutableIntStateOf(uiState.selectedYear) }

                AlertDialog(
                    onDismissRequest = { showMonthPicker = false },
                    title = { Text("Chọn Thời Gian") },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    text = {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                // Year Picker (Simplified)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Năm", fontWeight = FontWeight.Bold)
                                    Box(modifier = Modifier.height(200.dp).width(100.dp).verticalScroll(rememberScrollState())) {
                                        Column {
                                            years.forEach { year ->
                                                Text(
                                                    text = year.toString(),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { tempYear = year }
                                                        .padding(8.dp),
                                                    color = if (tempYear == year) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = if (tempYear == year) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                                // Month Picker
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Tháng", fontWeight = FontWeight.Bold)
                                    Box(modifier = Modifier.height(200.dp).width(120.dp).verticalScroll(rememberScrollState())) {
                                        Column {
                                            months.forEachIndexed { index, month ->
                                                Text(
                                                    text = month,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { tempMonth = index }
                                                        .padding(8.dp),
                                                    color = if (tempMonth == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = if (tempMonth == index) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            onMonthSelected(tempMonth, tempYear)
                            showMonthPicker = false
                        }) {
                            Text("Xác nhận")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showMonthPicker = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }

            StatItem(
                label = "Số bản ghi trong tháng", 
                value = "${uiState.transactions.size}",
                modifier = Modifier.clickable { showTransactionList = true }
            )

            // Bộ lọc Chi tiêu / Thu nhập (Combobox-like filter)
            var expandedFilter by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { expandedFilter = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Loại thống kê: ${filters[selectedFilterIndex]}",
                            fontWeight = FontWeight.Medium
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { expandedFilter = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    filters.forEachIndexed { index, filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = {
                                selectedFilterIndex = index
                                expandedFilter = false
                            }
                        )
                    }
                }
            }

            if (showTransactionList) {
                AlertDialog(
                    onDismissRequest = { showTransactionList = false },
                    title = { Text("Danh sách giao dịch tháng", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    text = {
                        Box(modifier = Modifier.heightIn(max = 400.dp)) {
                            if (uiState.transactions.isEmpty()) {
                                Text("Không có giao dịch nào.")
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn {
                                    items(uiState.transactions.size) { index ->
                                        val transaction = uiState.transactions[index]
                                        val category = uiState.categories.find { it.id == transaction.categoryId }
                                        val isExpense = category?.isExpense ?: true
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(transaction.title, fontWeight = FontWeight.Bold)
                                                Text(
                                                    category?.name ?: "Khác", 
                                                    fontSize = 12.sp, 
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "${if (isExpense) "-" else "+"}${String.format("%,.0f đ", transaction.amount)}",
                                                color = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (index < uiState.transactions.size - 1) {
                                            HorizontalDivider(
                                                thickness = 0.5.dp, 
                                                color = MaterialTheme.colorScheme.outlineVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showTransactionList = false }) {
                            Text("Đóng")
                        }
                    }
                )
            }

            // Tab hoặc tiêu đề phân biệt Thu nhập / Chi tiêu
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            
            if (selectedFilterIndex == 0) {
                // --- PHẦN CHI TIÊU ---
                Text(
                    "Thống kê Chi tiêu", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatItem("Tổng chi", String.format("%,.0f đ", uiState.totalExpense), MaterialTheme.colorScheme.error)
                        StatItem("Trung bình", String.format("%,.0f đ", uiState.avgExpense))
                        StatItem("Lớn nhất", String.format("%,.0f đ", uiState.maxExpense))
                        StatItem("Nhỏ nhất", String.format("%,.0f đ", uiState.minExpense))
                    }
                }
            } else {
                // --- PHẦN THU NHẬP ---
                Text(
                    "Thống kê Thu nhập", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatItem("Tổng thu", String.format("%,.0f đ", uiState.totalIncome), Color(0xFF2E7D32))
                        StatItem("Trung bình", String.format("%,.0f đ", uiState.avgIncome))
                        StatItem("Lớn nhất", String.format("%,.0f đ", uiState.maxIncome))
                        StatItem("Nhỏ nhất", String.format("%,.0f đ", uiState.minIncome))
                    }
                }
            }

            // --- THỐNG KÊ THEO DANH MỤC ---
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            OutlinedCard(
                onClick = { showCategoryStatsDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Xem chi tiết theo danh mục", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            if (showCategoryStatsDialog) {
                var dialogFilterIndex by remember { mutableIntStateOf(selectedFilterIndex) }
                AlertDialog(
                    onDismissRequest = { showCategoryStatsDialog = false },
                    title = { Text("Thống kê theo danh mục", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            TabRow(
                                selectedTabIndex = dialogFilterIndex,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Tab(
                                    selected = dialogFilterIndex == 0,
                                    onClick = { dialogFilterIndex = 0 },
                                    text = { Text("Chi tiêu") }
                                )
                                Tab(
                                    selected = dialogFilterIndex == 1,
                                    onClick = { dialogFilterIndex = 1 },
                                    text = { Text("Thu nhập") }
                                )
                            }
                            
                            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                                val filteredStats = uiState.categoryStats.filter { it.isExpense == (dialogFilterIndex == 0) }
                                if (filteredStats.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        Text("Không có dữ liệu trong tháng này", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                } else {
                                    androidx.compose.foundation.lazy.LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(filteredStats.size) { index ->
                                            CategoryStatItem(filteredStats[index])
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showCategoryStatsDialog = false }) {
                            Text("Đóng")
                        }
                    }
                )
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            
            Text(
                "Cơ cấu thu chi tháng", 
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                color = MaterialTheme.colorScheme.onBackground
            )

            // Biểu đồ tròn
            if (uiState.totalIncome > 0 || uiState.totalExpense > 0) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SimplePieChart(
                        income = uiState.totalIncome.toFloat(),
                        expense = uiState.totalExpense.toFloat()
                    )
                }
            } else {
                Text(
                    "Không có dữ liệu trong tháng này", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(12.dp), color = Color(0xFF2E7D32)) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thu nhập", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Text(String.format("%,.0f đ", uiState.totalIncome), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(12.dp), color = MaterialTheme.colorScheme.error) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chi tiêu", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Text(String.format("%,.0f đ", uiState.totalExpense), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(income: Float, expense: Float) {
    val total = income + expense
    if (total == 0f) return

    val incomeAngle = (income / total) * 360f
    val expenseAngle = (expense / total) * 360f
    
    val incomeColor = Color(0xFF2E7D32)
    val expenseColor = MaterialTheme.colorScheme.error

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = incomeColor,
            startAngle = -90f,
            sweepAngle = incomeAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )
        drawArc(
            color = expenseColor,
            startAngle = -90f + incomeAngle,
            sweepAngle = expenseAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )
    }
}

@Composable
fun StatsScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    StatsContent(
        uiState = uiState,
        onBack = onBack,
        onDeleteAll = { viewModel.clearAllTransactions() },
        onMonthSelected = { m, y -> viewModel.onMonthYearSelected(m, y) }
    )
}

@Composable
fun CategoryStatItem(stat: com.example.baithi.ui.viewmodel.CategoryStat) {
    val color = if (stat.isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stat.categoryName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = String.format("%,.0f đ", stat.total),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Trung bình: ${String.format("%,.0f đ", stat.avg)}", fontSize = 12.sp)
                Text("Giao dịch: ${stat.count}", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsPreview() {
    BaithiTheme {
        StatsContent(
            uiState = TransactionUiState(
                transactions = List(5) { mockTransaction },
                totalIncome = 200000.0,
                totalExpense = 50000.0,
                avgIncome = 40000.0,
                maxIncome = 100000.0,
                minIncome = 10000.0,
                avgExpense = 10000.0,
                maxExpense = 20000.0,
                minExpense = 5000.0,
                selectedMonth = 4,
                selectedYear = 2024
            ),
            onBack = {},
            onDeleteAll = {},
            onMonthSelected = { _, _ -> }
        )
    }
}

private val mockTransaction = com.example.baithi.data.local.entities.Transaction(
    title = "Mock", amount = 10.0, date = 0L, categoryId = 1L, note = null
)

@Composable
fun StatItem(
    label: String, 
    value: String, 
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor, fontSize = 18.sp)
    }
}
