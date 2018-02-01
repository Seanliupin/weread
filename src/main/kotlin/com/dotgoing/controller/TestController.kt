package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import com.dotgoing.service.TestService
import com.dotgoing.service.UserService
import com.dotgoing.utils.WXHelper
import com.dotgoing.wx.parser.Div
import com.dotgoing.wx.parser.H1
import com.dotgoing.wx.parser.Item
import com.dotgoing.wx.parser.Text
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController @Autowired constructor(val userRepository: UserRepository,
                                            val userService: UserService,
                                            val wxHelper: WXHelper,
                                            val testService: TestService) {

    @Value("\${app.id}")
    private val appid: String? = null

    @Value("\${app.secret}")
    private val secret: String? = null


    @GetMapping("/")
    fun index(): String {
        return "this is test"
    }

    @GetMapping("/not_safe_create")
    fun create_test(): Long? {
        val user = User("first -2 not safe" + System.currentTimeMillis(), "last")
        return testService.createUser(user).id
    }

    @GetMapping("/safe_create")
    fun createSafe(): Long? {
        val user = User("first -2 safe " + System.currentTimeMillis(), "last")
        return userService.createUser(user).id
    }

    @GetMapping("/{userId}")
    fun info(@PathVariable userId: Long): User {
        return userRepository.findOne(userId)
    }


    @GetMapping("/json")
    fun json(): Item {
        return Text("this is text")
    }


    @GetMapping("/json_1", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun jsonmy(): String {
        val json = JSONObject()
        val subJson = JSONObject()
        subJson.put("key1", "value1")
        json.put("key2", subJson)

        return json.toString()
    }


    @GetMapping("/node")
    fun node(): List<Item> {

        val chi = listOf(Text("this is text"), H1("strong"))
        val x = listOf(Text("this is text"), Div("div_class", chi))
        return x
    }
}