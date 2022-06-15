package com.example.demo.Ulti;

import com.example.demo.exception.BlogApiException;
import com.example.demo.security.service.UserDetailsImpl;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;

public class AppUtils {
	public static void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BlogApiException(HttpStatus.BAD_REQUEST, "Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BlogApiException(HttpStatus.BAD_REQUEST, "Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BlogApiException(HttpStatus.BAD_REQUEST, "Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
	public static boolean isAdmin(UserDetailsImpl currentUser) {
        var roles = currentUser.getAuthorities();
        for (GrantedAuthority role : roles) {
            if (role.getAuthority().equals("ROLE_AMIN"))
                return true;
        }
        return false;
    }
	public static boolean isModerator(UserDetailsImpl currentUser) {
        var roles = currentUser.getAuthorities();
        for (GrantedAuthority role : roles) {
            if (role.getAuthority().equals("ROLE_MODERATOR"))
                return true;
        }
        return false;
    }
}