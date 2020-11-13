package com.sharedfinances.restapi;

import com.sharedfinances.restapi.documents.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends MongoRepository<Person, String> {
    Optional<Person> findByName(@Param("name") String name);
}
