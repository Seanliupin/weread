package com.dotgoing.controller

import com.dotgoing.model.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class UserController {
    val counter = AtomicLong()

    @GetMapping("/user")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String): User {
        return User("Hello, dddd $name", counter.incrementAndGet().toInt())
    }

}