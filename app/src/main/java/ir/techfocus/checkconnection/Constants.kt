package ir.techfocus.checkconnection

import java.net.URL

class Constants {
    companion object {
        const val CONNECTIVITY_UPDATE: String = "ir.techfocus.CONNECTIVITY_UPDATE"
        const val ADDRESS_TEST_KEY: String = "addressTest"
        const val DELAY: String = "delay"
        const val TIME_OUT: String = "timeOut"
        const val DEFAULT_IP: String = "127.0.0.1"
        const val DEFAULT_PORT: Int = 443
        const val IP_ADDRESS_NAME_1: String = "ipAddress1"
        const val IP_ADDRESS_NAME_2: String = "ipAddress2"
        const val IP_ADDRESS_NAME_3: String = "ipAddress3"

        val CHANNEL_ID = "connectivity_channel"
        val NOTIFICATION_ID = 1
        val clients3Url = URL("https://clients3.google.com/generate_204")
        val clients3Url2 = URL("https://connectivitycheck.gstatic.com/generate_204")

        const val CONTEXT = "context"
        const val IS_CANCELABLE = "isCancelable"
        const val ALERT_TYPE = "alertType"

        const val NORMAL_TYPE: Int = 0
        const val SUCCESS_TYPE: Int = 1
        const val ERROR_TYPE: Int = 2
        const val WARNING_TYPE: Int = 3

        const val DEFAULT_DELAY: Long = 1200
        const val DEFAULT_TIMEOUT: Int = 1000
    }
}