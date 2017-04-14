package com.jalarbee.aleef.account.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Optional;

public final class Person {

    private final String firstName;
    private final String lastName;
    private final Optional<String> middleName;

    private final String cellPhone;
    private final String email;


    @JsonCreator
    public Person(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName, @JsonProperty("middleName") Optional<String> middleName, @JsonProperty("cellPhone") String cellPhone, @JsonProperty("email") String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.cellPhone = cellPhone;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Optional<String> getMiddleName() {
        return middleName;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("middleName", middleName)
                .add("cellPhone", cellPhone)
                .add("email", email)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equal(firstName, person.firstName) &&
                Objects.equal(lastName, person.lastName) &&
                Objects.equal(cellPhone, person.cellPhone) &&
                Objects.equal(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(firstName, lastName, cellPhone, email);
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private Optional<String> middleName;
        private String cellPhone;
        private String email;

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setMiddleName(Optional<String> middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder setCellPhone(String cellPhone) {
            this.cellPhone = cellPhone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Person createPerson() {
            return new Person(firstName, lastName, middleName, cellPhone, email);
        }
    }

}
