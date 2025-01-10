package org.pshhs.dto;

import lombok.*;
import org.pshhs.model.Currency;

import java.math.BigDecimal;

@Builder
@Data
public class ExchangeRateDTO {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
