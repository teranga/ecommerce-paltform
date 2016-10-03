package com.jalarbee.aleef.hello.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Driver.class)
public interface AbstractDriver {
}
