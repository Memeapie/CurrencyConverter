package com.gre.assessment.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
