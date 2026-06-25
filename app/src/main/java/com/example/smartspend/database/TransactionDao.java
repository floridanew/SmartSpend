// app/src/main/java/com/example/smartspend/database/TransactionDao.java
package com.example.smartspend.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartspend.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    // Toutes les transactions triées par date décroissante
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions(int userId);

    // Filtre par plage de dates
    @Query("SELECT * FROM transactions WHERE userId = :userId " +
            "AND date BETWEEN :start AND :end ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByDateRange(int userId, long start, long end);

    // Recherche par description ou catégorie
    @Query("SELECT * FROM transactions WHERE userId = :userId " +
            "AND (description LIKE '%' || :query || '%' " +
            "OR category LIKE '%' || :query || '%') ORDER BY date DESC")
    LiveData<List<Transaction>> searchTransactions(int userId, String query);
}