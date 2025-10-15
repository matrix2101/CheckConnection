package ir.techfocus.checkconnection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IPAddressDao {
    @Query("SELECT * FROM IPAddressLocal")
    suspend fun getAllIPAddressesLocal(): MutableList<IPAddressLocal>?

    @Query("SELECT * FROM IPAddressLocal WHERE ipAddress = (:ipAddress)")
    suspend fun getIPAddressLocalByIpAddress(ipAddress: String): IPAddressLocal?

    @Query("SELECT MAX(name) FROM IPAddressLocal")
    suspend fun getGreatestIdNameLocal(): Int

    @Query("DELETE FROM IPAddressLocal WHERE name = (:name)")
    suspend fun deleteIPAddressLocalByName(name: String)

    @Insert(entity = IPAddressLocal::class, OnConflictStrategy.REPLACE)
    suspend fun insertIPAddressLocal(ipAddress: IPAddressLocal)

    //====================================================

    @Query("SELECT * FROM IPAddressRemote")
    suspend fun getAllIPAddressesRemote(): List<IPAddressRemote>?

    @Query("SELECT * FROM IPAddressRemote WHERE ipAddress = (:ipAddress)")
    suspend fun getIPAddressRemoteByIpAddress(ipAddress: String): IPAddressRemote?

    @Query("DELETE FROM IPAddressRemote")
    suspend fun deleteAllIPAddressRemote()

    @Insert(entity = IPAddressRemote::class, OnConflictStrategy.REPLACE)
    suspend fun insertIPAddressRemote(ipAddress: IPAddressRemote)

    //====================================================

    @Query("SELECT * FROM IPAddressLast WHERE name = (:name)")
    suspend fun getIPAddressLastByName(name: String): IPAddressLast?

    @Insert(entity = IPAddressLast::class, OnConflictStrategy.REPLACE)
    suspend fun insertIPAddressLast(ipAddress: IPAddressLast)

    @Query("DELETE FROM IPAddressLast")
    suspend fun deleteAllIPAddressLast()

    //====================================================

    @Query("SELECT * FROM MySettings WHERE `key` = (:key)")
    fun getSettingByName(key: String): MySettings

    @Insert(entity = MySettings::class, OnConflictStrategy.REPLACE)
    suspend fun insertSetting(mySettings: MySettings)

    @Query("DELETE FROM MySettings WHERE `key` = (:key)")
    suspend fun deleteSetting(key: String)
}