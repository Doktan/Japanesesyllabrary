package com.example.japanesesyllabrary

import android.util.Log
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

class cryptData(var data: String) {

    var sks: SecretKeySpec? = null

    fun encryptData(){
        try {
            val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
            sr.setSeed("any data used as random seed".toByteArray())
            val kg: KeyGenerator = KeyGenerator.getInstance("AES")
            kg.init(128, sr)
            sks = SecretKeySpec(kg.generateKey().getEncoded(), "AES")
        } catch (e: Exception) {
            Log.e("Crypto", "AES secret key spec error")
        }
    }

    fun decryptData(){

    }

}