package ir.techfocus.checkconnection

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IPAddressLocal::class, IPAddressRemote::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): IPAddressDao
}