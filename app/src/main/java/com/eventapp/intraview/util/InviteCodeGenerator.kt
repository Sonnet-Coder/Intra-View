package com.eventapp.intraview.util

import kotlin.random.Random

object InviteCodeGenerator {
    private val charset = ('A'..'Z') + ('0'..'9')
    
    fun generate(length: Int = Constants.INVITE_CODE_LENGTH): String {
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    
    fun generateQRToken(): String {
        return java.util.UUID.randomUUID().toString()
    }
}


