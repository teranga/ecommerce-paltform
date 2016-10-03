package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Registration.class)
public interface AbstractRegistration {

    Login login();

    Optional<Person> owner();

}
