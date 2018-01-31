package com.dotgoing.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Author: Sean
 * Date: 31/01/2018
 * Time: 3:10 PM
 */

@RestController
class HomeController {

    @RequestMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String): String {
        return "hello"
    }

}
