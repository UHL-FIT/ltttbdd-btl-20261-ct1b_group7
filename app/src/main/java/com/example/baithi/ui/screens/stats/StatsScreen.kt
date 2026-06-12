package com.example.baithi.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionUiState
import com.example.baithi.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(
    uiState: TransactionUiState,
    onBack: () -> Unit,
    onDeleteAll: () -> Unit
) {
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem("Tổng số bản ghi", "${uiState.transactions.size}")
            StatItem("Giá trị trung bình", String.format("%,.0f đ", uiState.avgTransaction))
            StatItem("Giá trị lớn nhất", String.format("%,.0f đ", uiState.maxTransaction), Color(0xFF2E7D32))
            StatItem("Giá trị nhỏ nhất", String.format("%,.0f đ", uiState.minTransaction), Color.Red)
            
            HorizontalDivider(color = Color(0xFF03A9F4).copy(alpha = 0.5f))
            
            Text("Cơ cấu thu chi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Thu nhập", color = Color.Black)
                Text(String.format("%,.0f đ", uiState.totalIncome), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Chi tiêu", color = Color.Black)
                Text(String.format("%,.0f đ", uiState.totalExpense), color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
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
        onDeleteAll = { viewModel.clearAllTransactions() }
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
                totalExpense = 50000.0
            ),
            onBack = {},
            onDeleteAll = {}
        )
    }
}

private val mockTransaction = com.example.baithi.data.local.entities.Transaction(
    title = "Mock", amount = 10.0, date = 0L, categoryId = 1L, note = null
)

@Composable
fun StatItem(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Black)
        Text(value, fontWeight = FontWeight.Bold, color = if (valueColor == Color.Black) Color.Black else valueColor)
    }
}
