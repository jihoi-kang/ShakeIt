package com.example.kjh.shakeit.utils;

import java.text.DecimalFormat;

public class CurrencyUnitUtil {

    private static DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static String toCurrency(int amount) {
        return decimalFormat.format(amount);
    }

    public static String toCurrency(String amount) {
        return decimalFormat.format(Integer.parseInt(amount.replaceAll("," , "")));
    }

    public static int toAmount(String currency) {
        return Integer.parseInt(currency.replaceAll("," , ""));
    }

}
