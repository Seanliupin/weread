package com.dotgoing.repository

import com.dotgoing.model.Person
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface PersonRepository : PagingAndSortingRepository<Person, Long> {

    fun findByLastName(@Param("name") name: String): List<Person>

}