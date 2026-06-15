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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Giới thiệu",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Text("Phiên bản 1.0.0", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Thành viên nhóm:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text("1. Nguyễn Hồng Việt", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("2. Nguyễn Thành Chung", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("3. Nguyễn Minh Tuấn", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("4. Hoàng Anh Vũ", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Task 3: Nút Xuất dữ liệu JSON
            Button(
                onClick = {
                    viewModel?.exportToJson(context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xuất dữ liệu JSON")
            }

            // Nút Nhập dữ liệu JSON
            Button(
                onClick = {
                    importLauncher.launch("*/*")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nhập dữ liệu JSON")
            }
            // Nút Xem hướng dẫn sử dụng (Cách 2: Mở qua link Online)
            Button(
                onClick = {
                    // 1. Thay link PDF thật của bạn vào đây (Google Drive, host riêng, v.v.)
                    val pdfUrl = "https://drive.google.com/file/d/12n_rSlWLe-v8YLc32wu2sE8NKnx4wqUE/view?usp=drive_link"

                    try {
                        // 2. Tạo Intent mở URL
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))

                        // 3. Kiểm tra và mở ứng dụng phù hợp (Trình duyệt hoặc trình đọc PDF)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // 4. Thông báo nếu máy không có ứng dụng nào mở được link/PDF
                        android.widget.Toast.makeText(
                            context,
                            "Không tìm thấy ứng dụng để mở tài liệu",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xem hướng dẫn sử dụng (PDF)")
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
