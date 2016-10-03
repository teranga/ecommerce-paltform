package com.jalarbee.aleef.product.model;

import java.math.BigDecimal;

/**
 * @author Abdoulaye Diallo
 */
public final class Price {

    public final BigDecimal buy;
    public final BigDecimal sell;
    public final BigDecimal expenses;

    public Price(BigDecimal buy, BigDecimal sell, BigDecimal expenses) {
        this.buy = buy;
        this.sell = sell;
        this.expenses = expenses;
    }
}
