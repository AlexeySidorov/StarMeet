package starmeet.convergentmobile.com.starmeet.Helpers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public  class CurrentHelper {
    public static String getCurrencyFormat(double currency, boolean isUsd) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        boolean isWholeNumber = (currency == Math.round(currency));
        String pattern = isWholeNumber ? "#.##" : "#.00";

        DecimalFormat format = new DecimalFormat(pattern, formatSymbols);
        String value = isUsd ? " \u0024" : " \u20B9";
        return format.format(currency) + value;
    }
}
