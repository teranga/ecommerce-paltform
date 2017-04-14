package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jalarbee.aleef.account.api.model.Person;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

import java.util.Optional;

@Data
public class PPerson implements Jsonable {

    private final String firstName;
    private final String lastName;
    private final Optional<String> middleName;

    private final String cellPhone;
    private final String email;

    @JsonCreator
    public PPerson(String firstName, String lastName, Optional<String> middleName, String cellPhone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.cellPhone = cellPhone;
        this.email = email;
    }

    public Person toPerson() {
        return new Person(firstName, lastName, middleName, cellPhone, email);
    }

    public static PPerson fromPerson(Person person) {
        assert person != null;
        return new PPerson(person.getFirstName(), person.getLastName(), person.getMiddleName(), person.getCellPhone(), person.getEmail());
    }
}
