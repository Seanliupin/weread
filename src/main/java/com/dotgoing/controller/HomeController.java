package com.dotgoing.controller;


import com.dotgoing.model.Person;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Author: Sean
 * Date: 31/01/2018
 * Time: 3:10 PM
 */

@RestController
public class HomeController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Person greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

        Person p = new Person();
        p.setFirstName("first");
        p.setLastName("last");

        return p;
    }
}
