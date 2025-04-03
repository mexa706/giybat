package api.giybat.uz.service;

import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.AuthDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;

import api.giybat.uz.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private ProfileRopsitory profileRopsitory;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;


    public String registaration(RegistrationDTO dto) {

//1.validation
        //2.phone or email uje est
        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRole(profile.getId());
                profileRopsitory.delete(profile);

                // sen d sms/email

            } else {
                throw new AppBadExeptions("Username already exists");
            }
        }


        ProfileEntity newProfile = new ProfileEntity();
        newProfile.setName(dto.getUsername());
        newProfile.setUsername(dto.getUsername());
        newProfile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        newProfile.setStatus(GeneralStatus.IN_REGISTRATION);
        newProfile.setVisible(Boolean.TRUE);
        newProfile.setCreatedDate(LocalDateTime.now());
        profileRopsitory.save(newProfile);

        profileRoleService.save(ProfileRole.ROLE_USER, newProfile.getId());

        emailSendingService.SendRegistrationEmail(dto.getUsername(), newProfile.getId());

        return "Successfully registered";
    }


    public String regVerification(String token) {


        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRopsitory.changeStatus(profileId, GeneralStatus.ACTIVE);
                return "Verification successful";
            }
        } catch (JwtException e) {
        }
        throw new AppBadExeptions("Verification failed");
    }

    public ProfileDTO login(AuthDTO dto) {

        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadExeptions("Username or password is wrong");
        }
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), optional.get().getPassword())) {
            throw new AppBadExeptions("Username or password is wrong");

        }
        if (!optional.get().getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadExeptions("Wrong Status");
        }

        ProfileEntity profile = optional.get();

        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));

        response.setJwt(JwtUtil.encode(profile.getUsername() , profile.getId(),response.getRoleList()));

        return response;
    }

}
