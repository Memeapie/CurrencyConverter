package com.gre.assessment.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConvertListedItem {

    private Double fromRate;
    private Double toRate;
    private String date;

}
