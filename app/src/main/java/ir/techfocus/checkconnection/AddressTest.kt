package ir.techfocus.checkconnection

import java.io.Serializable

class AddressTest(
    val ip: String,
    val port: Int,
    val shouldBeTested: Boolean,
    var reachable: Boolean,
    var testFailed: Long,
    var testSuccess: Long
) : Serializable