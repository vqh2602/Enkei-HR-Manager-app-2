package com.athsoftware.hrm.common

class Constants {
    interface Key {
        companion object {
            const val language = "language"
            const val user = "user"
            const val PREF_UNIQUE_ID = "DeviceID"
        }
    }

    interface Lang {
        companion object {
            const val vi = "vi"
            const val en = "en"
            const val cn = "zh"
        }
    }

    interface DateFormat {
        companion object {
            const val default = "yyyy-MM-dd'T'HH:mm:ss" //2018-05-18T00:00:00
            const val defaultFull = "yyyy-MM-dd'T'HH:mm:ss.S"
            const val defaultFullSavis = "yyyy-MM-dd'T'HH:mm:ss.SSS"
            const val requestServer = "yyyy-MM-dd'T'HH:mm:ss.S'Z'"
            const val dateTime = "yyyy-MM-dd'T'HH:mm:ss"
            const val date = "yyyy-MM-dd"
            const val dateVi = "dd/MM/yyyy"
            const val time = "HH:mm:ss"
            const val hourMinute = "HH:mm"
            const val dateTimePaymentHistory = "dd/MM/yyyy | HH:mm"
            const val dateSavis = "dd/MM/yyyy"
            const val dateTracking = "HH:mm:ss dd/MM/yyyy"
        }
    }

    interface Extra {
        companion object {
            const val weight = "Weight"
            const val OPEN_MESSAGE = "OPEN_MESSAGE"
        }
    }
}