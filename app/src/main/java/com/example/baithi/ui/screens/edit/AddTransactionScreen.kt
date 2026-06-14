package com.example.baithi.ui.screens.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.example.baithi.data.local.entities.Category
import com.example.baithi.data.local.entities.Transaction
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    transaction: Transaction? = null,
    categories: List<Category>,
    onSave: (id: Long?, title: String, amount: Double, date: Long, categoryName: String, isExpense: Boolean, note: String?) -> Unit,
    onBack: () -> Unit
) {
    val sampleIncome = listOf("Tiền lương", "Thưởng")
    val sampleExpense = listOf("Ăn uống", "Di chuyển")

    var selectedTabIndex by remember {
        mutableIntStateOf(
            if (transaction != null) {
                val cat = categories.find { it.id == transaction.categoryId }
                if (cat?.isExpense == false) 0 else 1
            } else 1
        )
    }

    val currentSamples = if (selectedTabIndex == 0) sampleIncome else sampleExpense
    
    val initialCategoryName = transaction?.let { t ->
        categories.find { it.id == t.categoryId }?.name
    }

    var selectedCategoryName by remember {
        mutableStateOf(
            if (initialCategoryName != null && initialCategoryName in currentSamples) {
                initialCategoryName
            } else if (initialCategoryName != null) {
                "Khác"
            } else {
                null
            }
        )
    }

    var customCategoryName by remember {
        mutableStateOf(
            if (initialCategoryName != null && initialCategoryName !in currentSamples) {
                initialCategoryName
            } else {
                ""
            }
        )
    }

    var title by remember { mutableStateOf(transaction?.title ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.let { if (it == 0.0) "" else it.toInt().toString() } ?: "") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var customCategoryError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (transaction == null) "Thêm giao dịch" else "Sửa giao dịch", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                color = MaterialTheme.colorScheme.surface
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            selectedCategoryName = null
                            customCategoryName = ""
                        },
                        text = { 
                            Text(
                                "THU NHẬP", 
                                color = MaterialTheme.colorScheme.onSurface, 
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            selectedCategoryName = null
                            customCategoryName = ""
                        },
                        text = { 
                            Text(
                                "CHI TIÊU", 
                                color = MaterialTheme.colorScheme.onSurface, 
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = it.isBlank()
                },
                label = { Text("Tiêu đề") },
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { if (titleError) Text("Tiêu đề không được để trống", color = MaterialTheme.colorScheme.error) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    amountError = it.toDoubleOrNull() == null || it.toDouble() <= 0
                },
                label = { Text("Số tiền") },
                isError = amountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { if (amountError) Text("Vui lòng nhập số tiền hợp lệ (> 0)", color = MaterialTheme.colorScheme.error) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Text(
                "Danh mục",
                color = if (categoryError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentSamples.forEach { name ->
                    FilterChip(
                        selected = selectedCategoryName == name,
                        onClick = {
                            selectedCategoryName = name
                            categoryError = false
                        },
                        label = { Text(name) }
                    )
                }
                FilterChip(
                    selected = selectedCategoryName == "Khác",
                    onClick = {
                        selectedCategoryName = "Khác"
                        categoryError = false
                    },
                    label = { Text("Khác") }
                )
            }
            
            if (selectedCategoryName == "Khác") {
                OutlinedTextField(
                    value = customCategoryName,
                    onValueChange = {
                        customCategoryName = it
                        customCategoryError = it.isBlank()
                    },
                    label = { Text("Tên danh mục tự nhập") },
                    isError = customCategoryError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { if (customCategoryError) Text("Vui lòng nhập tên danh mục", color = MaterialTheme.colorScheme.error) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            if (categoryError) {
                Text(
                    "Vui lòng chọn một danh mục",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    titleError = title.isBlank()
                    amountError = amountDouble == null || amountDouble <= 0
                    categoryError = selectedCategoryName == null
                    customCategoryError = selectedCategoryName == "Khác" && customCategoryName.isBlank()

                    if (!titleError && !amountError && !categoryError && !customCategoryError) {
                        val finalCategoryName = if (selectedCategoryName == "Khác") customCategoryName else selectedCategoryName!!
                        onSave(
                            transaction?.id,
                            title,
                            amountDouble!!,
                            transaction?.date ?: System.currentTimeMillis(),
                            finalCategoryName,
                            selectedTabIndex == 1, // isExpense
                            note
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Vui lòng nhập đầy đủ thông tin hợp lệ!")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (transaction == null) "Lưu giao dịch" else "Cập nhật thay đổi")
            }
        }
    }
}

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    transactionId: Long? = null,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val transaction = remember(transactionId, uiState.transactions) {
        uiState.transactions.find { it.id == transactionId }
    }

    AddTransactionContent(
        transaction = transaction,
        categories = uiState.categories,
        onSave = { id, t, a, d, catName, isExpense, n ->
            if (id == null) {
                viewModel.addTransactionWithCategoryName(t, a, d, catName, isExpense, n)
            } else {
                viewModel.updateTransactionWithCategoryName(id, t, a, d, catName, isExpense, n)
            }
            onBack()
        },
        onBack = onBack
    )
}

@Preview(showBackground = true)
@Composable
fun AddTransactionPreview() {
    BaithiTheme {
        AddTransactionContent(
            categories = listOf(
                Category(id = 1, name = "Ăn uống", iconName = "", isExpense = true),
                Category(id = 2, name = "Lương", iconName = "", isExpense = false)
            ),
            onSave = { _, _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
