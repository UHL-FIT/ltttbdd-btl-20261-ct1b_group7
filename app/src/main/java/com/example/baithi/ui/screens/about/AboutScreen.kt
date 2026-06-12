package com.example.baithi.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.tooling.preview.Preview
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    viewModel: TransactionViewModel? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val reader = inputStream.bufferedReader()
                    val jsonString = reader.readText()
                    viewModel?.importFromJson(jsonString, context)
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Lỗi đọc file: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Giới thiệu",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF03A9F4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Ứng dụng Quản lý Thu Chi Cá Nhân", 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text("Phiên bản 1.0.0", fontSize = 18.sp, color = Color.Black)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Thành viên nhóm:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text("1. Nguyễn Hồng Việt", fontSize = 18.sp, color = Color.Black)
            Text("2. Nguyễn Thành Chung", fontSize = 18.sp, color = Color.Black)
            Text("3. Nguyễn Minh Tuấn", fontSize = 18.sp, color = Color.Black)
            Text("4. Hoàng Anh Vũ", fontSize = 18.sp, color = Color.Black)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Task 3: Nút Xuất dữ liệu JSON
            Button(
                onClick = {
                    viewModel?.exportToJson(context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xuất dữ liệu JSON", color = Color.White)
            }

            // Nút Nhập dữ liệu JSON
            Button(
                onClick = {
                    importLauncher.launch("application/json")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nhập dữ liệu JSON", color = Color.White)
            }

            Button(
                onClick = {
                    // Giả lập mở file PDF hướng dẫn sử dụng
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse("https://www.example.com/guide.pdf"), "application/pdf")
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle case where no PDF app is installed
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xem hướng dẫn sử dụng (PDF)", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    BaithiTheme {
        AboutScreen(onBack = {})
    }
}
