package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.profile.ProfileDTO;
import api.giybat.uz.dto.auth.*;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;

import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.PhoneUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
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
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;


    public AppResponse<String> registaration(RegistrationDTO dto, AppLanguage language) {


        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRole(profile.getId());
                profileRopsitory.delete(profile);

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

        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsSendService.sendRegistrationSms(dto.getUsername(), language);
            return new AppResponse<>(bundleService.getMessage("sms.confirm.send", language));
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailSendingService.SendRegistrationEmail(dto.getUsername(), newProfile.getId(), language);
            return new AppResponse<>(bundleService.getMessage("email.confirm.send", language));
        }
        return new AppResponse<>(bundleService.getMessage("contact.format.invalid", language));
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

    public AppResponse<String> registrationSmsVerificationResend(SmsResendDTO dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("profile.not.found", language));
        }

        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new AppBadExeptions(bundleService.getMessage("verification.failed", language));
        }

        smsSendService.sendRegistrationSms(dto.getPhone(), language);

        return new AppResponse<>(bundleService.getMessage("sms.resent", language));
    }

    public AppResponse<String> resetPassword(ResetPasswordDTO dto, AppLanguage language) {

        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadExeptions(bundleService.getMessage("reset.failed", language));
        }

        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsSendService.sendResetPasswordSms(dto.getUsername(), language);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailSendingService.SendResetPasswordEmail(dto.getUsername(), language);
        }

        String response= String.format(bundleService.getMessage("reset.password.response", language), dto.getUsername());

        return new AppResponse<>(response);
    }

    public ProfileDTO getLogInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));

        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList()));

        return response;
    }

    public AppResponse<String> resetPasswordConfirm(@Valid ResetPasswordConfirmDTO dto, AppLanguage language) {

        Optional<ProfileEntity> optional = profileRopsitory.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profileEntity = optional.get();
        if (!profileEntity.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadExeptions(bundleService.getMessage("reset.failed", language));
        }

        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsHistoryService.check(dto.getUsername(), dto.getCode(), language);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailHistoryService.check(dto.getUsername(), dto.getCode(), language);
        }

        profileRopsitory.updatePassword(profileEntity.getId(),bCryptPasswordEncoder.encode(dto.getPassword()));

        return new AppResponse<>(bundleService.getMessage("reset.password.confirm.success", language));
    }
}
