package com.jalarbee.aleef.account.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Abdoulaye Diallo
 */
public class Account {

    private final String id;
    private final Person owner;
    private final AccountStatus status;

    @JsonCreator
    public Account(@JsonProperty("id") String id, @JsonProperty("owner") Person owner, @JsonProperty("status") AccountStatus status) {
        this.id = id;
        this.owner = owner;
        this.status = status;
    }

    public Account(String id, Person owner) {
        this(id, owner, null);
    }

    public String getId() {
        return id;
    }

    public Person getOwner() {
        return owner;
    }

    public AccountStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("owner", owner)
                .add("status", status)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equal(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
