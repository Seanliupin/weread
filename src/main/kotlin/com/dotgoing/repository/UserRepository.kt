package com.dotgoing.repository

import com.dotgoing.model.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface UserRepository : PagingAndSortingRepository<User, Long> {
    fun findByLastName(@Param("name") name: String): List<User>
}