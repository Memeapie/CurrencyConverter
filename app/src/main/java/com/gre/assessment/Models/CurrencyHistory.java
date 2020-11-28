package com.gre.assessment.Models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CurrencyHistory extends IOErrorReadOut {

    private String fromCurrency;
    private String toCurrency;
    private String fromCurrencyCode;
    private String toCurrencyCode;
    private String startDate;
    private String endDate;

    List<CurrencyConvertListedItem> currencyRates;

    public CurrencyHistory(String error){
        super();
        this.setErrorOut(error);
    }
}
