package com.project.market.modules.account.util;

public class PhoneUtils {

    static public String trim(String phone) {
        char[] chars = phone.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
