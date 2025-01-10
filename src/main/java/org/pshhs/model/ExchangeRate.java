package org.pshhs.model;

import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    @Override
    public String toString() {
        return "ExchangeRateDTO{" +
                "id=" + id +
                ", baseCurrency=" + baseCurrency.getCode() +
                ", targetCurrency=" + targetCurrency.getCode() +
                ", rate=" + rate +
                '}';
    }
}


