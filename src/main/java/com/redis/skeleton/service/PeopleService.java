package com.redis.skeleton.service;

import com.redis.om.spring.search.stream.EntityStream;
import com.redis.skeleton.model.Person;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PeopleService {

    EntityStream entityStream;

    public PeopleService(EntityStream entityStream) {
        this.entityStream = entityStream;
    }

    // Find all people
    public Iterable<Person> findAllPeople(int minAge, int maxAge) {
        return entityStream //
                .of(Person.class) //
                .collect(Collectors.toList());
    }
}
