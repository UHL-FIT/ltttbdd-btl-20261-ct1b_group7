package com.example.baithi.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.baithi.data.local.entities.Transaction
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionUiState
import com.example.baithi.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhChinhContent(
    uiState: TransactionUiState,
    onThemGiaoDich: () -> Unit,
    onSuaGiaoDich: (Transaction) -> Unit,
    onXemGioiThieu: () -> Unit,
    onXemThongKe: () -> Unit,
    onXuatDuLieu: () -> Unit,
    onXoaGiaoDich: (Transaction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lí chi tiêu", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onXuatDuLieu) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Xuất JSON")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onXemThongKe,
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Thống kê") },
                    label = { Text("Thống kê") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onXemGioiThieu,
                    icon = { Icon(Icons.Default.Info, contentDescription = "Giới thiệu") },
                    label = { Text("Giới thiệu") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onThemGiaoDich) {
                Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TheTongQuan(
                thuNhap = uiState.totalIncome,
                chiTieu = uiState.totalExpense,
                soDu = uiState.balance
            )
            
            Text(
                "Giao dịch gần đây",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (uiState.transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bạn chưa có giao dịch nào bấm dấu \"+\" để thêm",
                        style = TextStyle(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.transactions) { transaction ->
                        val category = uiState.categories.find { it.id == transaction.categoryId }
                        DongGiaoDich(
                            transaction = transaction,
                            category = category,
                            onClick = { onSuaGiaoDich(transaction) },
                            onDelete = { onXoaGiaoDich(transaction) }
                        )
                    }
                }
            }
            
            // Thành phần ở dưới menu ở cuối (Footer)
            Text(
                text = "Bản quyền © 2026 - Nhóm 7 ",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ManHinhChinh(
    viewModel: TransactionViewModel,
    onThemGiaoDich: () -> Unit,
    onSuaGiaoDich: (Transaction) -> Unit,
    onXemGioiThieu: () -> Unit,
    onXemThongKe: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    ManHinhChinhContent(
        uiState = uiState,
        onThemGiaoDich = onThemGiaoDich,
        onSuaGiaoDich = onSuaGiaoDich,
        onXemGioiThieu = onXemGioiThieu,
        onXemThongKe = onXemThongKe,
        onXuatDuLieu = { viewModel.exportToJson(context) },
        onXoaGiaoDich = { viewModel.deleteTransaction(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ManHinhChinhPreview() {
    BaithiTheme {
        val sampleCategories = listOf(
            com.example.baithi.data.local.entities.Category(id = 1L, name = "Ăn uống", iconName = "", isExpense = true),
            com.example.baithi.data.local.entities.Category(id = 2L, name = "Lương", iconName = "", isExpense = false)
        )
        val sampleTransactions = listOf(
            Transaction(id = 1L, title = "Ăn trưa", amount = 50000.0, date = System.currentTimeMillis(), categoryId = 1L, note = "Phở"),
            Transaction(id = 2L, title = "Lương tháng 5", amount = 10000000.0, date = System.currentTimeMillis(), categoryId = 2L, note = null)
        )
        ManHinhChinhContent(
            uiState = TransactionUiState(
                transactions = sampleTransactions,
                categories = sampleCategories,
                totalIncome = 10000000.0,
                totalExpense = 50000.0,
                balance = 9950000.0
            ),
            onThemGiaoDich = {},
            onSuaGiaoDich = {},
            onXemGioiThieu = {},
            onXemThongKe = {},
            onXuatDuLieu = {},
            onXoaGiaoDich = {}
        )
    }
}

@Composable
fun TheTongQuan(thuNhap: Double, chiTieu: Double, soDu: Double) {
    val progress = if (thuNhap > 0) (chiTieu / thuNhap).coerceIn(0.0, 1.0).toFloat() else 0f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Số dư hiện tại",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = String.format(Locale.getDefault(), "%,.0f đ", soDu),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("Tổng thu", thuNhap)
                SummaryItem("Tổng chi", chiTieu)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: Double, isPercent: Boolean = false) {
    Column {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(
            if (isPercent) String.format("%.1f%%", value) else String.format(Locale.getDefault(), "%,.0f", value),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DongGiaoDich(
    transaction: Transaction,
    category: com.example.baithi.data.local.entities.Category?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val isExpense = category?.isExpense ?: true
    
    val amountColor = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
    val iconColor = if (isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val iconBgColor = if (isExpense) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Icon circle
            Surface(
                shape = RoundedCornerShape(50),
                color = iconBgColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Icon(
                        imageVector = if (isExpense) Icons.Default.BarChart else Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(transaction.title, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    Text(
                        text = String.format(Locale.getDefault(), "%,.0f đ", transaction.amount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${category?.name ?: "Khác"} • ${sdf.format(Date(transaction.date))}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete, 
                    contentDescription = "Xóa", 
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), 
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
