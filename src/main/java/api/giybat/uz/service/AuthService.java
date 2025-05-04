package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.AuthDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.dto.auth.SmsVerificationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;

import api.giybat.uz.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
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
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;


    public AppResponse<String> registaration(RegistrationDTO dto, AppLanguage language) {

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
                throw new AppBadExeptions(bundleService.getMessage("email.phone.exists", language));
            }
        }


        ProfileEntity newProfile = new ProfileEntity();
        newProfile.setName(dto.getName());
        newProfile.setUsername(dto.getUsername());
        newProfile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        newProfile.setStatus(GeneralStatus.IN_REGISTRATION);
        newProfile.setVisible(Boolean.TRUE);
        newProfile.setCreatedDate(LocalDateTime.now());
        profileRopsitory.save(newProfile);

        profileRoleService.save(ProfileRole.ROLE_USER, newProfile.getId());

        if (dto.getUsername().contains("+998")) {
            smsSendService.sendRegistrationSms(dto.getUsername());
        } else {
            emailSendingService.SendRegistrationEmail(dto.getUsername(), newProfile.getId(), language);
        }
        return new AppResponse<>(bundleService.getMessage("email.confirm.send", language));
    }

    public AppResponse<String> registrationEmailVerification(String token, AppLanguage language) {

        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRopsitory.changeStatus(profileId, GeneralStatus.ACTIVE);
                return new AppResponse<>(bundleService.getMessage("verification.finished", language));
            }
        } catch (JwtException e) {
            e.printStackTrace();
        }
        throw new AppBadExeptions(bundleService.getMessage("verification.failed", language));
    }

    public ProfileDTO login(AuthDTO dto, AppLanguage language) {

        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("username.password.wrong", language));
        }
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), optional.get().getPassword())) {
            throw new AppBadExeptions(bundleService.getMessage("username.password.wrong", language));

        }
        if (!optional.get().getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadExeptions(bundleService.getMessage("wrong.status", language));
        }

        ProfileEntity profile = optional.get();

        return getLogInResponse(profile);
    }

    public ProfileDTO registrationSmsVerification(SmsVerificationDTO dto, AppLanguage language) {

        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("profile.not.found", language));
        }

        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new AppBadExeptions(bundleService.getMessage("verification.failed", language));
        }

        smsHistoryService.check(dto.getPhone(), dto.getCode(), language);

        profileRopsitory.changeStatus(profileEntity.getId(), GeneralStatus.ACTIVE);

        return getLogInResponse(profileEntity);
    }

    public ProfileDTO getLogInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));

        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList()));

        return response;
    }
}
