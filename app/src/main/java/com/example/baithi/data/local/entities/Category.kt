package com.example.baithi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String, // Tên icon hiển thị
    val isExpense: Boolean // True: Chi phí, False: Thu nhập
)
