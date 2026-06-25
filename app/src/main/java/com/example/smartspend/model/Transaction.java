// app/src/main/java/com/example/smartspend/model/Transaction.java
package com.example.smartspend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String type;         // "INCOME" ou "EXPENSE"
    public String category;     // "Transport", "Nourriture", etc.
    public double amount;
    public long date;           // timestamp en millisecondes
    public String description;
    public int userId;

    // Constructeur complet
    public Transaction(String type, String category, double amount,
                       long date, String description, int userId) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.userId = userId;
    }
}