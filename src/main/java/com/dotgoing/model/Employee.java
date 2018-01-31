package com.dotgoing.model;

import lombok.Data;

import javax.persistence.*;
/**
 * Author: Sean
 * Date: 31/01/2018
 * Time: 3:17 PM
 */
@Data
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "salary")
    private int salary;
}
