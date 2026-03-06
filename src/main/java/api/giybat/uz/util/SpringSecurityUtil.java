package api.giybat.uz.util;

import api.giybat.uz.config.CustomUserDetails;
import api.giybat.uz.enums.ProfileRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SpringSecurityUtil {

    public static CustomUserDetails getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        // System.out.println(user.getUsername());
        //Collection<GrantedAuthority> roles = (Collection<GrantedAuthority>) user.getAuthorities();
        // Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return user;
    }


    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getId();
    }

    public static boolean hasRole(ProfileRole requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(role ->
                role.getAuthority().equals(requiredRole.name()));
    }


}
