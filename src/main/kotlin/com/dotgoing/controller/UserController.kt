package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    private val userRepository: UserRepository? = null

    @PostMapping("/create")
    fun create(): Long? {
        val p = User("first -1 " + System.currentTimeMillis(), "last")
        userRepository?.save(p)
        val x = userRepository!!.save(p)
        return x.id
    }

    @GetMapping("/{userId}")
    fun info(@PathVariable userId: Long): User {
        return userRepository!!.findOne(userId)
    }

    @RequestMapping("/all")
    fun persons(): List<User> {
        userRepository!!.findAll()

        val j = ArrayList<User>()
        userRepository.findAll().forEach { person -> j.add(person) }

        return j

    }

}