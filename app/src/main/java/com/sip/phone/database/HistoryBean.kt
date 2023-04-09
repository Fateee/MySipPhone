package com.sip.phone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "history")
data class HistoryBean(@ColumnInfo(name = "user_id") var user_id : String,//当前登录的手机号
                       @ColumnInfo(name = "phone") var phone : String,//对方号码
                       @ColumnInfo(name = "name") var name : String?,//对方姓名
                       @ColumnInfo(name = "type") var type : Int,//来电去电 1去电 2来电
                       @ColumnInfo(name = "duration") var duration : Long,//通话时长
                       @ColumnInfo(name = "date") var date : Long,//通话时间
                       @ColumnInfo(name = "connect") var connect : Boolean,//是否接通
                       @ColumnInfo(name = "location") var location : String?,//归属地
                       @ColumnInfo(name = "company") var company : String?,//运行商
                       @ColumnInfo(name = "incall_show_time") var incall_show_time : Long=0) : Serializable{//来电响铃时间
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}