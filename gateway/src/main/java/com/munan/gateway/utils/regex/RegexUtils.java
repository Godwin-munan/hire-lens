package com.munan.gateway.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static Matcher emailGroup(String source) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

        return emailPattern.matcher(source);
    }

    public static Matcher phoneContactGroup(String source) {
        Pattern phonePattern = Pattern.compile("\\+?[0-9. ()-]{10,}");
        return phonePattern.matcher(source);
    }

    public static Matcher linkedInLinkGroup(String source) {
        Pattern linkedInLinkPattern = Pattern.compile("^https://www\\.linkedin\\.com/in/[\\w-]+/?$");
        return linkedInLinkPattern.matcher(source);
    }

    public static Matcher githubLinkGroup(String source) {
        Pattern githubLinkPattern = Pattern.compile("^https://github\\.com/[\\w-]+/?$\n");
        return githubLinkPattern.matcher(source);
    }

    public static Matcher linkedInLinkGroupV2(String source) {
        Pattern linkedinPattern = Pattern.compile("https?://(www\\.)?linkedin\\.com/[^\\s]+");
        return linkedinPattern.matcher(source);
    }

    public static Matcher githubLinkGroupV2(String source) {
        Pattern githubPattern = Pattern.compile("https?://(www\\.)?github\\.com/[^\\s]+");
        return githubPattern.matcher(source);
    }
}
