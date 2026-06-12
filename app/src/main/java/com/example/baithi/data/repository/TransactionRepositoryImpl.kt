package com.example.baithi.data.repository

import com.example.baithi.data.local.dao.TransactionDao
import com.example.baithi.data.local.entities.Category
import com.example.baithi.data.local.entities.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(private val transactionDao: TransactionDao) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
    
    fun getAllCategories(): Flow<List<Category>> = transactionDao.getAllCategories()
    
    suspend fun insertCategory(category: Category): Long = transactionDao.insertCategory(category)
}
