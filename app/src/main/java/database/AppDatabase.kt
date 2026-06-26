package com.team.smartspend.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.team.smartspend.model.Budget
import com.team.smartspend.model.Transaction
import com.team.smartspend.model.User

@Database(
    entities = [User::class, Transaction::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Exemple de migration (de la version 1 vers la version 2).
         * Quand on modifiera le schéma (ex : ajouter une colonne), on
         * incrémentera `version` ci-dessus et on écrira la migration ici,
         * puis on l'ajoutera via `.addMigrations(MIGRATION_1_2)`.
         *
         * Cela permet de faire évoluer la base SANS perdre les données
         * déjà enregistrées par l'utilisateur.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Exemple : db.execSQL("ALTER TABLE transactions ADD COLUMN note TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartspend_database"
                )
                    .addMigrations(MIGRATION_1_2)        // migrations déclarées
                    .fallbackToDestructiveMigration()    // filet de sécurité si aucune migration ne correspond
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}