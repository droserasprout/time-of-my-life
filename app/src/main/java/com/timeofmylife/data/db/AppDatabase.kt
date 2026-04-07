package com.timeofmylife.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.BudgetItem

@Database(entities = [Balance::class, BudgetItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
    abstract fun budgetItemDao(): BudgetItemDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE budget_items ADD COLUMN lastAmount REAL NOT NULL DEFAULT 0.0")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance.db"
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
    }
}
