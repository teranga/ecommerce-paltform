package com.jalarbee.aleef.product.model;

import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
public enum  Unit {
    L("Ltr", "Ltrs", "Liter", "Litres"),
    B("Bid", "Bids", "Bidon", "Bidons"),
    C("Crt", "Ctrs", "Carton", "Cartons");

    public final String abbrevSingular;
    public final String abbrevPlural;
    public final String wordSingular;
    public final String wordPlural;

    Unit(String abbrevSingular, String abbrevPlural, String wordSingular, String wordPlural) {
        this.abbrevSingular = abbrevSingular;
        this.abbrevPlural = abbrevPlural;
        this.wordSingular = wordSingular;
        this.wordPlural = wordPlural;
    }

    public Optional<Unit> of(String symbol) {
        try {
            return Optional.of(Unit.valueOf(symbol));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
