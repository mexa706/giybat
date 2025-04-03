package api.giybat.uz.service;

import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileRoleService {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void save(ProfileRole role, Integer profileId) {
        ProfileRoleEntity roleEntity = new ProfileRoleEntity();
        roleEntity.setProfileId(profileId);
        roleEntity.setRoles(role);
        roleEntity.setCreatedDate(LocalDateTime.now());
        profileRoleRepository.save(roleEntity);
    }

    public void deleteRole(Integer profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }

}
