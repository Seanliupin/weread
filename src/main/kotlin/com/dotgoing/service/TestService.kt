package com.dotgoing.service

import com.dotgoing.model.User
import com.dotgoing.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * todo: 加不加 Transactional 好像都不能保证事务都完整性。需要查找一下原因
 *
 * */

@Service
class TestService(val userRepository: UserRepository) {

    fun users() = userRepository.findAll()

    fun createUser(user: User): User {
        val re = userRepository.save(user.copy(firstName = "added-first ${user.firstName}"))

        if (1 > 0) {
            throw Exception("this is not bad")
        }

        return re
    }
}