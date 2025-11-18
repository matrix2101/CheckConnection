package ir.techfocus.checkconnection

import java.net.URL

class Constants {
    companion object {
        const val IP_INFO_TOKEN: String = "79a507afa98648"

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

        const val LANGUAGE_FA: String = "fa"
        const val LANGUAGE_EN: String = "en"
        const val LANGUAGE_DEFAULT: String = "fa"

        const val LANGUAGE: String = "language"

        const val OVERLAY_X_KEY: String = "overlayX"
        const val OVERLAY_Y_KEY: String = "overlayY"

        const val OVERLAY_KEY: String = "overlay"
        const val OVERLAY_TOP_LEFT_KEY: String = "topLeft"
        const val OVERLAY_TOP_RIGHT_KEY: String = "topRight"
        const val OVERLAY_BOTTOM_LEFT_KEY: String = "bottomLeft"
        const val OVERLAY_BOTTOM_RIGHT_KEY: String = "bottomRight"
        const val OVERLAY_DEFAULT: String = "topLeft"

        const val OVERLAY_PERCENTAGE_X_START: Int = 10
        const val OVERLAY_PERCENTAGE_X_END: Int = 83
        const val OVERLAY_PERCENTAGE_Y_START: Int = 5
        const val OVERLAY_PERCENTAGE_Y_END: Int = 83

        const val OVERLAY_UPDATE: String = "ir.techfocus.OVERLAY_UPDATE"

        const val IPV4: String = "IPv4"
        const val IPV6: String = "IPv6"
        const val DOMAIN: String = "Domain"
    }
}