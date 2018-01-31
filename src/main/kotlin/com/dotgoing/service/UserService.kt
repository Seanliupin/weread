package com.dotgoing.service

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(var userRepository: UserRepository) {

    fun users() = userRepository.findAll()

    fun createUser(user: User): User {
        val re = userRepository.save(user.copy(firstName = "added-first ${user.firstName}"))

        if (1 > 0) {
            throw Exception("this is not bad")
        }

        return re
    }
}