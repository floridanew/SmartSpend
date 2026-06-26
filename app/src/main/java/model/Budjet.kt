package com.team.smartspend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val montantMax: Double,
    val mois: String    // format "2026-06" par exemple
)