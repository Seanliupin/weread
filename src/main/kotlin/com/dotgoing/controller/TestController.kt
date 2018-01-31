package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import com.dotgoing.service.TestService
import com.dotgoing.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController @Autowired constructor(val userRepository: UserRepository,
                                            val userService: UserService,
                                            val testService: TestService) {

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
}