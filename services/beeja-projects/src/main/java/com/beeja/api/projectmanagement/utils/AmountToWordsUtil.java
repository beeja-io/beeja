package com.beeja.api.projectmanagement.utils;
import com.ibm.icu.text.RuleBasedNumberFormat;
import java.util.Locale;

public class AmountToWordsUtil {
    public static String convertToWords(double amount) {
        RuleBasedNumberFormat formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);

        long whole = (long) amount;
        int fraction = (int) Math.round((amount - whole) * 100);

        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(formatter.format(whole)));

        if (fraction > 0) {
            sb.append(" and ").append(formatter.format(fraction));
        }

        return sb.toString();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
