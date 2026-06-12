package com.example.baithi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.baithi.ui.navigation.AppNavHost
import com.example.baithi.ui.theme.BaithiTheme
import com.example.baithi.ui.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TransactionViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate: Ứng dụng được khởi tạo")
        
        // Chèn dữ liệu mẫu nếu cần
        viewModel.insertInitialCategories()
        
        enableEdgeToEdge()
        setContent {
            BaithiTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart: Ứng dụng bắt đầu hiển thị")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume: Người dùng bắt đầu tương tác")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause: Ứng dụng bị tạm dừng")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy: Ứng dụng bị hủy")
    }
}
