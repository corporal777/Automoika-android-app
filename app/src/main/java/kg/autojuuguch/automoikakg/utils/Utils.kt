package kg.autojuuguch.automoikakg.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import io.reactivex.Maybe

object Utils {

    fun formatMobilePhone(phone: String?): String {
        return if (phone.isNullOrEmpty()) ""
        else if (phone.length == 13) {
            StringBuilder(phone)
                .insert(4, " ")
                .insert(8, " ")
                .insert(12, " ")
                .toString()
        } else phone
    }

    fun isPhone(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[0-9]+")
        return regex.containsMatchIn(text)
    }

    fun isContainsNumbers(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[0-9]+")
        return regex.containsMatchIn(text)
    }

    fun isContainLetters(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[A-Za-z]+")
        return regex.containsMatchIn(text)
    }


    fun isPhoneNumberValid(text: Any?): Boolean {
        var valid = true
        val phone = if (text is Editable) text.toString() else (text as String)
        if (!phone.isNullOrEmpty()) {
            if (phone.contains("+")) {
                if (phone.length == 13) {
                    if (phone.substring(0, 4) != "+996") valid = false
                } else valid = false
            } else {
                if (phone.length == 10) {
                    val firstNumber = phone.substring(0, 1)
                    if (firstNumber != "0") valid = false
                } else valid = false
            }
        } else {
            valid = false
        }
        return valid
    }

    fun validatePhoneBeforeSend(phone: String): String {
        if (phone == "") return ""
        val phoneResult = if (!phone.contains("+")) "+$phone" else phone
        val sb = StringBuilder(phoneResult)
        if (phone.substring(0, 1) == "8")
            sb.setCharAt(1, '7')
        return sb.toString()
    }

    fun getCityDistricts(): List<String> {
        return listOf("Ленинский", "Октябрьский", "Первомайский", "Свердловский")
    }

    fun getCityDistrict(text : String?): String {
        return if (getCityDistricts().contains(text)) text ?: "" else ""
    }
}