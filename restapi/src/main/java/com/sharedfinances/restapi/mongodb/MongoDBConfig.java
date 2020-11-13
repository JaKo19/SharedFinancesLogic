package com.sharedfinances.restapi.mongodb;

import com.sharedfinances.restapi.PersonRepository;
import com.sharedfinances.restapi.documents.Debtor;
import com.sharedfinances.restapi.documents.Person;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@Configuration
public class MongoDBConfig {
//    @Bean
//    CommandLineRunner commandLineRunner(PersonRepository personRepository) {
//        List<Debtor> debtors = new ArrayList<>();
//        debtors.add(new Debtor("Debtor", 20, 20));
//        return args -> personRepository.save(new Person("Test", 20, 20, debtors));
//    }
}
