package com.dotgoing.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "person")
data class User(var firstName: String = "", val lastName: String = "") {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0
}