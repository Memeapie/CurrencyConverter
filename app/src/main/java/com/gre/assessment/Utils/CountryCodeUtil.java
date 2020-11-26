package com.gre.assessment.Utils;

public class CountryCodeUtil {

    public static final String GBP = "GBP";
    public static final String USD = "USD";
    public static final String AUD = "AUD";
    public static final String CAD = "CAD";
    public static final String EUR = "EUR";

    public static final String GBP_NAME = "British Pounds";
    public static final String USD_NAME = "American Dollars";
    public static final String AUD_NAME = "Australian Dollars";
    public static final String CAD_NAME = "Canadian Dollars";
    public static final String EUR_NAME = "Euros";

    public static String nameToCC(String name){
        switch (name){
            case USD_NAME:
                return USD;
            case AUD_NAME:
                return AUD;
            case CAD_NAME:
                return CAD;
            case EUR_NAME:
                return EUR;
            default:
                return GBP;
        }
    }
}
