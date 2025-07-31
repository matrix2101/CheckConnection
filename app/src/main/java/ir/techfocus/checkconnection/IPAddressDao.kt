package ir.techfocus.checkconnection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IPAddressDao {
    @Query("SELECT * FROM IPAddressLocal")
    suspend fun getAllIPAddressesLocal(): List<IPAddressLocal>?

    @Query("SELECT * FROM IPAddressLocal WHERE ipAddress = (:ipAddress)")
    suspend fun getIPAddressLocalByIpAddress(ipAddress: String): IPAddressLocal?

    @Query("SELECT MAX(name) FROM IPAddressLocal")
    suspend fun getGreatestIdNameLocal(): Int

    @Insert(entity = IPAddressLocal::class, OnConflictStrategy.REPLACE)
    suspend fun insertIPAddressLocal(ipAddress: IPAddressLocal)

    @Delete(entity = IPAddressLocal::class)
    suspend fun deleteIPAddressLocal(ipAddress: IPAddressLocal)

    @Delete(entity = IPAddressLocal::class)
    suspend fun deleteIPAddressLocal(ipAddresses: List<IPAddressLocal>)

    @Update(entity = IPAddressLocal::class)
    suspend fun updateUsersLocal(vararg ipAddress: IPAddressLocal)

    //====================================================

    @Query("SELECT * FROM IPAddressRemote")
    suspend fun getAllIPAddressesRemote(): List<IPAddressRemote>?

    @Query("SELECT * FROM IPAddressRemote WHERE ipAddress = (:ipAddress)")
    suspend fun getIPAddressRemoteByIpAddress(ipAddress: String): IPAddressRemote?

    @Query("DELETE FROM IPAddressRemote")
    suspend fun deleteAllIPAddressRemote()

    @Insert(entity = IPAddressRemote::class, OnConflictStrategy.REPLACE)
    suspend fun insertIPAddressRemote(ipAddress: IPAddressRemote)

    @Delete(entity = IPAddressRemote::class)
    suspend fun deleteIPAddressRemote(ipAddress: IPAddressRemote)

    @Update(entity = IPAddressRemote::class)
    suspend fun updateUsersRemote(ipAddress: IPAddressRemote)
}