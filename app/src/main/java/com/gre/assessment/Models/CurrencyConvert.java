package com.gre.assessment.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConvert extends IOErrorReadOut {

    private Double fromRate;
    private Double toRate;
    private String fromCurrency;
    private String toCurrency;
    private String fromCurrencyCode;
    private String toCurrencyCode;

    public CurrencyConvert(String error){
        super();
        this.setErrorOut(error);
    }
}
