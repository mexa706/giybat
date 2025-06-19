package api.giybat.uz.util;

import java.util.regex.Pattern;

public class EmailUtil {
    public static Boolean isEmail(String email) {
        String EmailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return Pattern.matches(EmailRegex, email);
    }
}
