package com.kikbak.rest.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AbstractController {

	protected long getCurrentUserId() {
        // Get the principal from the Spring security context
        final SecurityContext secCtx = SecurityContextHolder.getContext();
        final Authentication auth = (null == secCtx) ? null : secCtx.getAuthentication();
        final Object principal = (null == auth) ? null : auth.getPrincipal();

        // Most Spring authentication mechanisms return an instance of UserDetails as the principal.
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (null != principal) {
            username = principal.toString();
        }

        return Long.parseLong(username);
    }

}
