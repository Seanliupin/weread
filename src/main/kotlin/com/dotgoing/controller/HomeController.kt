package com.dotgoing.controller

import com.dotgoing.model.Person
import com.dotgoing.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Author: Sean
 * Date: 31/01/2018
 * Time: 3:10 PM
 */

@RestController
class HomeController {
    private val counter = AtomicLong()

    @Autowired
    private val personRepository: PersonRepository? = null

    @RequestMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String): Person {

        val p = Person("first -1", "last")


        Logger.getLogger("HomeController").log(Level.ALL, "==============")

        return p
    }

    @RequestMapping("/create")
    fun create(): Long? {
        val p = Person("first -1 " + System.currentTimeMillis(), "last")
        personRepository?.save(p)
        val x = personRepository!!.save(p)
        return x.id
    }

    @RequestMapping("/all")
    fun persons(): List<Person> {
        personRepository!!.findAll()

        val j = ArrayList<Person>()
        personRepository.findAll().forEach { person -> j.add(person) }

        return j

    }

    companion object {
        private val template = "Hello, %s!"
    }
}
