package com.dotgoing.utils

import khttp.get
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WXHelper {

    @Value("\${app.id}")
    private val appid: String? = null

    @Value("\${app.secret}")
    private val secret: String? = null

    fun getOpenId(code: String, encryptedData: String, iv: String): String {
        val url = "https://api.weixin.qq.com/sns/jscode2session?appid=${appid}&secret=${secret}&js_code=${code}&grant_type=authorization_code"

        val response = get(url)

        println("this is response : ${response}")
        println("this is response jsonObject : ${response.jsonObject}")

//        response.jsonObject.get("openid")

        return "fake - opendi"

    }


}