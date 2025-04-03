package api.giybat.uz.config;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRopsitory profileRopsitory;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);
        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        ProfileEntity profile = optional.get();
        List<ProfileRole> roleList =profileRoleRepository.getAllRolesListByProfileId(profile.getId());
        return new CustomUserDetails(profile, roleList);
    }
}
