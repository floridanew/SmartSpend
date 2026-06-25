// app/src/main/java/com/example/smartspend/database/AppDatabase.java
package com.example.smartspend.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smartspend.model.Transaction;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TransactionDao transactionDao();

    // Singleton thread-safe
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "smartspend_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}