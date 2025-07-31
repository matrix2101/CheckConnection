package ir.techfocus.checkconnection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IPAddressLocal")
data class IPAddressLocal(
    @PrimaryKey val name: String,
    @ColumnInfo val ipAddress: String?,
    @ColumnInfo val port: Int?
)
