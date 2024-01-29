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
