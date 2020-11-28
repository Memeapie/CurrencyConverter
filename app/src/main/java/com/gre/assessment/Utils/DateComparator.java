package com.gre.assessment.Utils;

import com.gre.assessment.Models.CurrencyConvertListedItem;

import java.util.Comparator;

public class DateComparator implements Comparator<CurrencyConvertListedItem> {
    public int compare(CurrencyConvertListedItem p, CurrencyConvertListedItem q) {
        if (p.getDate().before(q.getDate())) {
            return -1;
        } else if (p.getDate().after(q.getDate())) {
            return 1;
        } else {
            return 0;
        }
    }
}