package api.giybat.uz.config;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private GeneralStatus status;

    public CustomUserDetails(ProfileEntity profile, List<ProfileRole> roleList) {

        this.password = profile.getPassword();
        this.status = profile.getStatus();
        this.username = profile.getUsername();
        this.name = profile.getName();
        this.id = profile.getId();

        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        for (ProfileRole role : roleList) {
            roles.add( new SimpleGrantedAuthority(role.name()));
        }
        this.authorities = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true ;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(GeneralStatus.ACTIVE);
    }
}
