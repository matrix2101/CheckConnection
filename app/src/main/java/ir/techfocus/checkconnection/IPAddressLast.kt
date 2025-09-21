package ir.techfocus.checkconnection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IPAddressLast")
data class IPAddressLast(
    @PrimaryKey val name: String,
    @ColumnInfo val ipAddress: String?,
    @ColumnInfo val port: String?
)