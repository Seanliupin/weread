package com.dotgoing.controller;


import com.dotgoing.model.Person;
import com.dotgoing.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Sean
 * Date: 31/01/2018
 * Time: 3:10 PM
 */

@RestController
public class HomeController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping("/greeting")
    public Person greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

        Person p = new Person();
        p.setFirstName("first -1 ");
        p.setLastName("last");

        Logger.getLogger("HomeController").log(Level.ALL, "==============");

        return p;
    }

    @RequestMapping("/create")
    public Long create() {
        Person p = new Person();
        p.setFirstName("first -1 " + System.currentTimeMillis());
        p.setLastName("last");
        Person x = personRepository.save(p);
        return x.getId();
    }

    @RequestMapping("/all")
    public List<Person> persons() {
        personRepository.findAll();

        List<Person> j = new ArrayList<>();
        personRepository.findAll().forEach(person -> j.add(person));

        return j;

    }
}
