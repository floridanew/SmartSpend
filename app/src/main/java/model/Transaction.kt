package com.team.smartspend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val montant: Double,
    val type: String,        // "DEPENSE" ou "REVENU"
    val categorie: String,
    val date: Long,          // timestamp en millisecondes
    val description: String = ""
)