package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import com.dotgoing.service.UserService
import com.dotgoing.utils.WXHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(val userRepository: UserRepository,
                                            val wxHelper: WXHelper,
                                            val userService: UserService) {

    @PostMapping("/create")
    fun create(): Long? {
        val user = User("first -2 safe " + System.currentTimeMillis(), "last")
        return userService.createUser(user).id
    }

    @GetMapping("/{userId}")
    fun getById(@PathVariable userId: Long): User {
        return userRepository.findOne(userId)
    }

    @RequestMapping("/all")
    fun all(): List<User> {
        return userRepository.findAll().toList()
    }

    @GetMapping("/login")
    fun login(@RequestParam(value = "code") code: String,
              @RequestParam(value = "encryptedData") encryptedData: String,
              @RequestParam(value = "iv") iv: String): UserInfo {

        val openid = wxHelper.getOpenId(code, encryptedData, iv)
        println("openid = ${openid}")

        return UserInfo(openid, 100, "this-is-fake-token")
    }

    data class UserInfo(val openid: String, val id: Int, val token: String)

}