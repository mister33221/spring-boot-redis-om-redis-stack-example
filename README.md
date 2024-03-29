# Skeleton of Spring Boot + Redis OM + Redis Stack

The most important thing in the begining is [Redis OM](https://redis.io/docs/connect/clients/om-clients/) is still on Beta.
This project is a skeleton of Spring Boot + Redis OM + Redis stack.
The purpose of this project is to provide some demo code for the Redis OM and the Redis stack.

## Make your hands dirty

Follow the steps below to make a local copy of this project.

### Environment

- Java 17
- Spring boot 3.2.1
- Redis stack latest
- Docker desktop 4.26.1
- Redis OM 0.8.8
- Springdoc 2.0.2

### Installation

- You can just clone this repo by using the following command.
```sh
git clone https://github.com/mister33221/spring-boot-redis-om-redis-stack-example.git
```
- Or build a new project by yourself. Follow the steps below.
1. Create a new project by using [Spring Initializr](https://start.spring.io/).
* project: Maven
* Language: Java
* Spring Boot: 3.2.x
* Project Metadata: Depends on your project
* Java: 17
* Packaging: Jar
* Dependencies: Spring Web, Lombok, devtools
2. Add else dependencies we need in pom.xml
```xml
<!--redis om-->
<dependency>
   <groupId>com.redis.om</groupId>
   <artifactId>redis-om-spring</artifactId>
   <version>0.8.8</version>
</dependency>

<!--swagger-->
<dependency>
   <groupId>org.springdoc</groupId>
   <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
   <version>2.0.2</version>
</dependency>
```
3. Run Redis Stack by docker compose.
```yaml
version: '3'
services:
 redis-stack:
 image: redis/redis-stack:latest
 container_name: redis-stack
 ports:
   - "6379:6379"
   - "8001:8001"
 volumes:
   - /local-data/:/data
```
```sh
docker-compose up -d
```
4. Open a command line and use the following command to connect to Redis Stack as a monitor.
```sh
docker exec -it redis-stack redis-cli MONITOR
```
5. Add the following code to `application.properties`.
```properties
spring.redis.host=localhost
spring.redis.port=6379
```
6. Run Spring Boot application.
7. Use browser to access RedisInsight.
```url
http://localhost:8001
```
8. Use browser to access Swagger UI.
```url
http://localhost:8080/swagger-ui.html
```
9. Then the project is ready to go.

### Configuration

- Create a package named `config` under `com.redis.skeleton` and create a class named `SpringDocConfig` in it.
- Add the following code to the class.
```java
package com.redis.skeleton.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "This is a Redis OM Skeleton practice project",
                version = "1.0",
                description = "版本: \n\n " +
                        "Java : 17\n\n" +
                        "spring boot : 3.2.1\n\n" +
                        "Radis OM : 0.8.8\n\n" +
                        "springdoc-openapi-core : 2.0.2\n\n")
)
@Configuration
public class SpringDocConfig {
}
```
  - `@EnableRedisDocumentRepositories`: This only for Redis OM, scan the redis documents(model) and repository. Do not use `@EnableRedisRepositories` in the same time.
  - `com.redis.skeleton`: Is where the redis documents(model) and repository located.
```java
package com.redis.skeleton;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation will specify the package to scan for redis-om documents (including the repositories)
@EnableRedisDocumentRepositories(basePackages = "com.redis.skeleton")
@SpringBootApplication
public class SkeletonApplication {

  public static void main(String[] args) {
    SpringApplication.run(SkeletonApplication.class, args);
  }

}

```

### Start to code

- Create a package named `model` under `com.redis.skeleton` and create a class named `Person` in it.
  - `@Id`: An autogenerated String using ULIDs
  - `@Document`: The class is annotated with `@Document` to marks the object as a Redis entity to be persisted as JSON document by appropiate type of repository.
  - `@Indexed`: The field is annotated with `@Indexed` to marks the field as an indexable field.  
  In this case, for the Person class an index named com.redis.om.skeleton.models.PersonIdx will be created on application startup.   
  In the index schema, a search field will be added for each @Indexed annotated property. 
  RediSearch, the underlying search engine powering searches, supports Text (full-text searches), Tag (exact-match searches), Numeric (range queries), Geo (geographic range queries), and Vector (vector queries) fields.
  - `@Searchable`: Fields marked as @Searchable such as personalStatement in Person are reflected as Full-Text search fields in the search index schema.

```java
package com.redis.skeleton.model;

import java.util.Set;

import com.redis.skeleton.model.Address;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
  @Indexed
  @NonNull
  private String firstName;

  @Indexed
  @NonNull
  private String lastName;

  //Indexed for numeric matches
  @Indexed
  @NonNull
  private Integer age;

  //Indexed for Full Text matches
  @Searchable
  @NonNull
  private String personalStatement;

  //Indexed for Geo Filtering
  @Indexed
  @NonNull
  private Point homeLoc;

  // Nest indexed object
  @Indexed
  @NonNull
  private Address address;

  @Indexed
  @NonNull
  private Set<String> skills;
}
```
- Create a class named `Address` in `com.redis.skeleton.model` package.
```java
package com.redis.skeleton.model;

import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class Address {

  @NonNull
  @Indexed
  private String houseNumber;

  @NonNull
  @Searchable(nostem = true)
  private String street;

  @NonNull
  @Indexed
  private String city;

  @NonNull
  @Indexed
  private String state;

  @NonNull
  @Indexed
  private String postalCode;

  @NonNull
  @Indexed
  private String country;

}
```
- Create a package named `repository` under `com.redis.skeleton` and create a interface named `PersonRepository` in it.
  - `RedisDocumentRepository`: The RedisDocumentRepository extends PagingAndSortingRepository which extends CrudRepository to provide additional methods to retrieve entities using the pagination and sorting.

```java
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
```
- Create a package named `controller` under `com.redis.skeleton` and create a class named `PersonControllerV1` in it.

```java
package com.redis.skeleton.contoller;

import com.redis.skeleton.model.Address;
import com.redis.skeleton.model.Person;
import com.redis.skeleton.repository.PeopleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/people")
public class PeopleControllerV1 {

  private final PeopleRepository repo;

  public PeopleControllerV1(PeopleRepository repo) {
    this.repo = repo;
  }


  @PostMapping("init-data")
  @Operation(summary = "Initialize the data", tags = {"Create"})
  void initData() {
    repo.deleteAll();

    String thorSays = "The Rabbit Is Correct, And Clearly The Smartest One Among You.";
    String ironmanSays = "Doth mother know you weareth her drapes?";
    String blackWidowSays = "Hey, fellas. Either one of you know where the Smithsonian is? I’m here to pick up a fossil.";
    String wandaMaximoffSays = "You Guys Know I Can Move Things With My Mind, Right?";
    String gamoraSays = "I Am Going To Die Surrounded By The Biggest Idiots In The Galaxy.";
    String nickFurySays = "Sir, I’m Gonna Have To Ask You To Exit The Donut";

    // Serendipity, 248 Seven Mile Beach Rd, Broken Head NSW 2481, Australia
    Address thorsAddress = Address.of("248", "Seven Mile Beach Rd", "Broken Head", "NSW", "2481", "Australia");

    // 11 Commerce Dr, Riverhead, NY 11901
    Address ironmansAddress = Address.of("11", "Commerce Dr", "Riverhead", "NY", "11901", "US");

    // 605 W 48th St, New York, NY 10019
    Address blackWidowAddress = Address.of("605", "48th St", "New York", "NY", "10019", "US");

    // 20 W 34th St, New York, NY 10001
    Address wandaMaximoffsAddress = Address.of("20", "W 34th St", "New York", "NY", "10001", "US");

    // 107 S Beverly Glen Blvd, Los Angeles, CA 90024
    Address gamorasAddress = Address.of("107", "S Beverly Glen Blvd", "Los Angeles", "CA", "90024", "US");

    // 11461 Sunset Blvd, Los Angeles, CA 90049
    Address nickFuryAddress = Address.of("11461", "Sunset Blvd", "Los Angeles", "CA", "90049", "US");

    Person thor = Person.of("Chris", "Hemsworth", 38, thorSays, new Point(153.616667, -28.716667), thorsAddress, Set.of("hammer", "biceps", "hair", "heart"));
    Person ironman = Person.of("Robert", "Downey", 56, ironmanSays, new Point(40.9190747, -72.5371874), ironmansAddress, Set.of("tech", "money", "one-liners", "intelligence", "resources"));
    Person blackWidow = Person.of("Scarlett", "Johansson", 37, blackWidowSays, new Point(40.7215259, -74.0129994), blackWidowAddress, Set.of("deception", "martial_arts"));
    Person wandaMaximoff = Person.of("Elizabeth", "Olsen", 32, wandaMaximoffSays, new Point(40.6976701, -74.2598641), wandaMaximoffsAddress, Set.of("magic", "loyalty"));
    Person gamora = Person.of("Zoe", "Saldana", 43, gamoraSays, new Point(-118.399968, 34.073087), gamorasAddress, Set.of("skills", "martial_arts"));
    Person nickFury = Person.of("Samuel L.", "Jackson", 73, nickFurySays, new Point(-118.4345534, 34.082615), nickFuryAddress, Set.of("planning", "deception", "resources"));

    repo.saveAll(List.of(thor, ironman, blackWidow, wandaMaximoff, gamora, nickFury));
  }

  @GetMapping("all")
  @Operation(summary = "Query all the data", tags = {"Read"})
  Iterable<Person> all() {
    return repo.findAll();
  }

  @GetMapping("{id}")
  @Operation(summary = "Query the data by id", tags = {"Read"})
  Optional<Person> byId(@PathVariable String id) {
    return repo.findById(id);
  }

  @GetMapping("age_between")
  @Operation(summary = "Query the data by age between", tags = {"Read"})
  Iterable<Person> byAgeBetween(
          @Parameter(description = "min age", example = "30")
          @RequestParam("min") int min,
          @Parameter(description = "max age", example = "40")
          @RequestParam("max") int max) {
    return repo.findByAgeBetween(min, max);
  }

  @GetMapping("name")
  @Operation(summary = "Query the data by first name and last name", tags = {"Read"})
  Iterable<Person> byFirstNameAndLastName(
          @Parameter(description = "first name", example = "Chris")
          @RequestParam("first") String firstName,
          @Parameter(description = "last name", example = "Hemsworth")
          @RequestParam("last") String lastName) {
    return repo.findByFirstNameAndLastName(firstName, lastName);
  }

  //    Not working. Don't know why.
  @GetMapping("homeloc")
  @Operation(summary = "Query the data by home location ( not working, Redis Inc. has announced the end-of-life of RedisGraph. Check here https://redis.com/blog/redisgraph-eol/ ", tags = {"Read"})
  Iterable<Person> byHomeLoc(
          @Parameter(description = "latitude", example = "-28.716667")
          @RequestParam("lat") double lat,
          @Parameter(description = "longitude", example = "153.616667")
          @RequestParam("lon") double lon,
          @Parameter(description = "distance", example = "100")
          @RequestParam("d") double distance) {
    return repo.findByHomeLocNear(new Point(lon, lat), new Distance(distance, Metrics.MILES));
  }

  @GetMapping("statement")
  @Operation(summary = "Query the data by personal statement", tags = {"Read"})
  Iterable<Person> byPersonalStatement(
          @Parameter(description = "personal statement", example = "The Rabbit Is Correct, And Clearly The Smartest One Among You.")
          @RequestParam("q") String q) {
    return repo.searchByPersonalStatement(q);
  }

  @GetMapping("city")
  @Operation(summary = "Query the data by city", tags = {"Read"})
  Iterable<Person> byCity(
          @Parameter(description = "city", example = "New York")
          @RequestParam("city") String city) {
    return repo.findByAddress_City(city);
  }

  @GetMapping("skills")
  @Operation(summary = "Query the data by skills", tags = {"Read"})
  Iterable<Person> byAnySkills(
          @Parameter(description = "skills", example = "hammer")
          @RequestParam("skills") Set<String> skills) {
    return repo.findBySkills(skills);
  }

  @DeleteMapping("all")
  @Operation(summary = "Delete all the data", tags = {"Delete"})
  void deleteAll() {
    repo.deleteAll();
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Delete the data by id", tags = {"Delete"})
  void deleteById(@PathVariable String id) {
    repo.deleteById(id);
  }

  @PostMapping("{id}/{age}")
  @Operation(summary = "Update the age by id", tags = {"Update"})
  Person updateById(@PathVariable String id, @PathVariable int age) {
    Optional<Person> person = repo.findById(id);
    if (person.isPresent()) {
      Person p = person.get();
      p.setAge(age);
      return repo.save(p);
    }
    return null;
  }

}

```
- Then everything is ready to go. You can use the Swagger UI to test the API.
  - In the beginning, you have to run the `init-data` API to create some data in Redis Stack.


## NOTE

1. If you follow the official guide to use Redis OM, you will find that the official guide is not correct.
  - [Redis OM](https://redis.io/docs/connect/clients/om-clients/stack-spring/#nested-field-search-features)
  - findAll()  not exists
  - findById(id) not exists
  - deleteAll() not exists
2. If your redis om dependency version is 0.8.6, then you have to add the jadis like below
    ```xml
    <dependency>
       <groupId>redis.clients</groupId>
       <artifactId>jedis</artifactId>
       <version>4.3.1</version>
    </dependency>
    ```
  - If your redis om dependency version is 0.8.8, like our setting in pom.xml, then it's ok. It's include the jadis dependency.
4. When you run your application, we can inspect the console, redis om will scan the @Document and @Indexed to create the index for your json model. Open redis cli and use command below to check indexes
```
docker exec -it redis-stack redis-cli
ft._list
```
## Reference

- [Redis doc](https://redis.io/doc)
- [Redis OM](https://redis.io/docs/connect/clients/om-clients/stack-spring/#nested-field-search-features)
- [Redisdeveloper](https://developer.redis.com/develop/java/spring/redis-om/redis-om-spring-json/)
- [Redis commands](https://redis.io/commands)
- [MVNrepository](https://mvnrepository.com/artifact/com.redis.om/redis-om-spring/0.8.8)
- [redis-om-spring on github](https://github.com/redis/redis-om-spring)
