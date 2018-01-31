package com.dotgoing.repository;

import com.dotgoing.model.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

    List<Person> findByLastName(@Param("name") String name);

}