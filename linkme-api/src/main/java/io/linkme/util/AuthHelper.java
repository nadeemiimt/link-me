package io.linkme.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    public static String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String userName = (String.valueOf(authentication.getPrincipal()));
            return userName;
        }

        return null; // Or throw an exception, or handle it based on your application's logic
    }
}
