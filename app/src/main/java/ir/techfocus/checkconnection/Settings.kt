package ir.techfocus.checkconnection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MySettings")
data class MySettings(
    @PrimaryKey var key: String,
    @ColumnInfo var value: String
)
