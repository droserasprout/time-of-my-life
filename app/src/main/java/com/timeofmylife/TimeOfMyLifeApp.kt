package com.timeofmylife

import android.app.Application
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.db.AppDatabase

class TimeOfMyLifeApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy {
        FinanceRepository(database.balanceDao(), database.budgetItemDao())
    }
}
