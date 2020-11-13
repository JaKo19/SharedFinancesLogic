package com.sharedfinances.restapi;

import com.sharedfinances.restapi.documents.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    public PersonController(PersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/list")
    List<Person> all() {
        return repository.findAll();
    }

    @PostMapping("/list")
    Person newPerson(@RequestBody Person newPerson) {
        return repository.save(newPerson);
    }

    @GetMapping("/list/{name}")
    Person one(@PathVariable String name) {
        return repository.findByName(name).get();
    }

    @PutMapping("/list/{name}")
    Person replacePerson(@RequestBody Person newPerson, @PathVariable String name) {
        return repository.findByName(name).map(person -> {
            person.setTotal(newPerson.getTotal());
            person.setAmount(newPerson.getAmount());
            person.setDebtors(newPerson.getDebtors());
            return repository.save(person);
        }).get();
    }

}
