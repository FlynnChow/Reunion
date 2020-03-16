package com.example.reunion.repostory.local_resource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.SystemMessageBean
import com.example.reunion.repostory.bean.TopicKeyword

@Database(entities = [TopicKeyword::class,ImMessageBean::class,ImMessageIndex::class,SystemMessageBean::class],version = 2)
abstract class AppDataBase:RoomDatabase() {
    companion object{

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(MyApplication.app,AppDataBase::class.java,"Reunion.db")
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .build()
        }

        private val MIGRATION_1_2 = object :Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS 'im_index'(
                    'tableId' TEXT PRIMARY KEY,
                    'toUid' TEXT)
                """.trimIndent())
            }

        }

    }


    abstract fun getTopicKeywordDao():TopicKeywordDao

    abstract fun getImMessageDao():ImMessageDao

    abstract fun getIndexDao():MessageIndexDao

    abstract fun getSysMessageDao():SystemMessageDao
}