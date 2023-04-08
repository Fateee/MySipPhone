package com.sip.phone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [(HistoryBean::class)], version = 1)
abstract class HistoryDb : RoomDatabase(){

    abstract fun historyDao(): HistoryDao

    companion object {

        const val DB_NAME = "history.db"

        @Volatile
        private var INSTANCE: HistoryDb? = null

        @JvmStatic
        fun getInstance(context: Context): HistoryDb {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDb::class.java,
                    "history"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                    }
                }).build().also {
                    INSTANCE = it
                }
            }
        }
    }

}