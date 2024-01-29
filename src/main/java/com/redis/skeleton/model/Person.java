package com.redis.skeleton.model;

import java.util.Set;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Document
public class Person {
    // Id Field, also indexed
    @Id
    @Indexed
    private String id;

    // Indexed for exact text matching
    @NonNull
    @Indexed
    private String firstName;

    @NonNull
    @Indexed
    private String lastName;

    //Indexed for numeric matches
    @NonNull
    @Indexed
    private Integer age;

    //Indexed for Full Text matches
    @Searchable
    @NonNull
    private String personalStatement;

    //Indexed for Geo Filtering
    @NonNull
    @Indexed
    private Point homeLoc;

    // Nest indexed object
    @NonNull
    @Indexed
    private Address address;

    @NonNull
    @Indexed
    private Set<String> skills;
}
