package com.dotgoing.controller

import com.dotgoing.model.User
import com.dotgoing.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    private val personRepository: PersonRepository? = null

    @PostMapping("/create")
    fun create(): Long? {
        val p = User("first -1 " + System.currentTimeMillis(), "last")
        personRepository?.save(p)
        val x = personRepository!!.save(p)
        return x.id
    }

    @GetMapping("/{userId}")
    fun info(@PathVariable userId: Long): User {
        return personRepository!!.findOne(userId)
    }

    @RequestMapping("/all")
    fun persons(): List<User> {
        personRepository!!.findAll()

        val j = ArrayList<User>()
        personRepository.findAll().forEach { person -> j.add(person) }

        return j

    }

}