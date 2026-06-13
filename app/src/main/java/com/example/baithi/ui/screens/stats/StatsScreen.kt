package com.example.baithi.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
    val months = listOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")
    
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear + 5).toList()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Thống kê báo cáo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onDeleteAll) {
                        Text("Xoá tất cả", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF03A9F4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Cảnh báo: Bạn đã vượt ngân sách chi tiêu tháng này (${String.format("%,.0f đ", uiState.monthlyBudget)})!",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Bộ chọn tháng
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
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
                        color = Color(0xFF0288D1)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF0288D1))
                }
            }

            if (showMonthPicker) {
                var tempMonth by remember { mutableIntStateOf(uiState.selectedMonth) }
                var tempYear by remember { mutableIntStateOf(uiState.selectedYear) }

                AlertDialog(
                    onDismissRequest = { showMonthPicker = false },
                    title = { Text("Chọn Thời Gian") },
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
                                                    color = if (tempYear == year) Color(0xFF03A9F4) else Color.Black,
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
                                                    color = if (tempMonth == index) Color(0xFF03A9F4) else Color.Black,
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

            if (showTransactionList) {
                AlertDialog(
                    onDismissRequest = { showTransactionList = false },
                    title = { Text("Danh sách giao dịch tháng", fontWeight = FontWeight.Bold) },
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
                                                    color = Color.Gray
                                                )
                                            }
                                            Text(
                                                text = "${if (isExpense) "-" else "+"}${String.format("%,.0f đ", transaction.amount)}",
                                                color = if (isExpense) Color.Red else Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (index < uiState.transactions.size - 1) {
                                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
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
            StatItem("Trung bình tháng", String.format("%,.0f đ", uiState.avgTransaction))
            StatItem("Lớn nhất tháng", String.format("%,.0f đ", uiState.maxTransaction), Color(0xFF2E7D32))
            StatItem("Nhỏ nhất tháng", String.format("%,.0f đ", uiState.minTransaction), Color.Red)
            
            HorizontalDivider(color = Color(0xFF03A9F4).copy(alpha = 0.5f))
            
            Text(
                "Cơ cấu thu chi tháng", 
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black
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
                    color = Color.Gray, 
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(12.dp), color = Color(0xFF2E7D32)) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thu nhập", color = Color.Black)
                    }
                    Text(String.format("%,.0f đ", uiState.totalIncome), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(12.dp), color = Color.Red) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chi tiêu", color = Color.Black)
                    }
                    Text(String.format("%,.0f đ", uiState.totalExpense), color = Color.Red, fontWeight = FontWeight.Bold)
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = Color(0xFF2E7D32),
            startAngle = -90f,
            sweepAngle = incomeAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )
        drawArc(
            color = Color.Red,
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

@Preview(showBackground = true)
@Composable
fun StatsPreview() {
    BaithiTheme {
        StatsContent(
            uiState = TransactionUiState(
                transactions = List(5) { mockTransaction },
                avgTransaction = 50000.0,
                maxTransaction = 100000.0,
                minTransaction = 10000.0,
                totalIncome = 200000.0,
                totalExpense = 50000.0,
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
    valueColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black, fontSize = 16.sp)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor, fontSize = 18.sp)
    }
}
