package com.jalarbee.aleef.ami.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = AmiCard.class)
public interface AbstractAmiCard {

    @Value.Parameter
    String accountId();

    @Value.Parameter
    Period period();

    @Value.Parameter
    List<DebitOp> debits();

    @Value.Parameter
    List<CreditOp> credits();

    @Value.Parameter
    int delinquencyInDays();

    @Value.Parameter
    String comments();

}
