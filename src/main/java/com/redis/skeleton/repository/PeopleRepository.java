package com.redis.skeleton.repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.redis.skeleton.model.Person;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.util.Set;

public interface PeopleRepository extends RedisDocumentRepository<Person, String>{

    Iterable<Person> findByFirstNameAndLastName(String firstName, String lastName);

    Iterable<Person> findByAgeBetween(int minAge, int maxAge);

//    Use this method to find people who live within a certain distance of a given point
    Iterable<Person> findByHomeLocNear(Point point, Distance distance);

    Iterable<Person> searchByPersonalStatement(String text);

    Iterable<Person> findByAddress_City(String city);

    Iterable<Person> findBySkills(Set<String> skills);

}
