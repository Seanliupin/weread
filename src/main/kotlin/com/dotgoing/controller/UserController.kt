package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import com.dotgoing.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(val userRepository: UserRepository,
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

}