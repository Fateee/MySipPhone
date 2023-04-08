package com.sip.phone.database

import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryBean(bean : HistoryBean?)

    @Delete
    fun deleteHistoryBean(bean: List<HistoryBean>?)

    @Delete
    fun deleteHistoryItem(bean: HistoryBean?)

    @Query("SELECT * FROM history WHERE user_id = :userId AND type = :type ORDER BY date DESC LIMIT 20 OFFSET :pageIndex*20")
    fun getHistoryByPage(pageIndex : Int, userId : String?, type : Int) : List<HistoryBean>?

    @Query("SELECT * FROM history WHERE user_id = :userId ORDER BY date")
    fun getAllHistory(userId : String?) : List<HistoryBean>?

    @Query("DELETE FROM history WHERE user_id = :userId")
    fun deleteMyHistoryData(userId: String?)

//    @Query("UPDATE history SET date = :date WHERE user_id = :userId AND date = :date")
//    fun updateHistoryBean(userId : String?, duration : String?, date : Long?)
//
//    @Query("SELECT * FROM history WHERE user_id = :userId AND wid = :wid")
//    fun getHistoryByWid(userId : String?, wid : String?) : List<HistoryBean>?

    @Update
    fun updateHistoryBean(bean : HistoryBean)

    @Query("SELECT * FROM history WHERE user_id = :userId AND type = :type")
    fun getAllHistoryByType(userId: String?, type: Int): List<HistoryBean>?
}