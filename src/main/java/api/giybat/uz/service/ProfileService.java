package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ConfirmCodeDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePhotoUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePswdUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadExceptions;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;
import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.PhoneUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRopsitory profileRopoitory;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private AttachService attachService;

    public ProfileEntity getById(Integer id) {

        return profileRopoitory.findByIdAndVisibleTrue(id).orElseThrow(() -> new AppBadExceptions("Profile not found"));

    }


    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO detailUpdateDTO, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        profileRopoitory.updateDetail(profileId, detailUpdateDTO.getName());
        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", language));
    }

    public AppResponse<String> updatePhoto(ProfilePhotoUpdateDTO photoUpdateDTO, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        if (profile.getPhotoId() != null && !profile.getPhotoId().equals(photoUpdateDTO.getPhotoId())) {
            attachService.delete(profile.getPhotoId());
        }
        profileRopoitory.updatePhoto(photoUpdateDTO.getPhotoId(), profileId);
        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", language));
    }

    public AppResponse<String> updatePswd(ProfilePswdUpdateDTO updatePswdDTO, AppLanguage language) {

        Integer profileId = SpringSecurityUtil.getCurrentUserId();

        String encodedPassword = profileRopoitory.findPasswordById(profileId);

        if (!bCryptPasswordEncoder.matches(updatePswdDTO.getOldPassword(), encodedPassword)) {
            throw new AppBadExceptions(bundleService.getMessage("reset.failed", language));
        }

        String newEncodedPassword = bCryptPasswordEncoder.encode(updatePswdDTO.getNewPassword());
        profileRopoitory.updatePassword(profileId, newEncodedPassword);

        return new AppResponse<>(bundleService.getMessage("reset.password.confirm.success", language));
    }


    public AppResponse<String> updateUsername(ProfileUsernameUpdateDTO usernameUpdateDTO, AppLanguage language) {

        //check

        Optional<ProfileEntity> profile = profileRopoitory.findByUsernameAndVisibleTrue(usernameUpdateDTO.getUsername());

        if (profile.isPresent()) {
            throw new AppBadExceptions(bundleService.getMessage("email.phone.exists", language));
        }

        //save

        //profileRopsitor

        //send
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        if (PhoneUtil.isPhone(usernameUpdateDTO.getUsername())) {
            smsSendService.sendChangeUsernameSms(usernameUpdateDTO.getUsername(), language);
            profileRopoitory.updateTempUsername(profileId, usernameUpdateDTO.getUsername());
            String message = bundleService.getMessage("confirm.code.send", language).formatted(usernameUpdateDTO.getUsername());
            return new AppResponse<>(message);
        } else if (EmailUtil.isEmail(usernameUpdateDTO.getUsername())) {
            emailSendingService.SendChangeUsernameEmail(usernameUpdateDTO.getUsername(), language);
            profileRopoitory.updateTempUsername(profileId, usernameUpdateDTO.getUsername());
            String message = bundleService.getMessage("confirm.code.send", language).formatted(usernameUpdateDTO.getUsername());
            return new AppResponse<>(message);
        } else {
            throw new AppBadExceptions(bundleService.getMessage("contact.format.invalid", language));
        }

    }

    public AppResponse<String> updateUsernameConfirm(ConfirmCodeDTO confirmCodeDTO, AppLanguage language) {

        ProfileEntity entity = getById(SpringSecurityUtil.getCurrentUserId());
        String tempUsername = entity.getTempUsername();
        if (PhoneUtil.isPhone(tempUsername)) {
            smsHistoryService.check(tempUsername, confirmCodeDTO.getCode(), language);
        } else if (EmailUtil.isEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, confirmCodeDTO.getCode(), language);
        }

        profileRopoitory.updateUsername(entity.getId(), entity.getTempUsername());

        List<ProfileRole> roles = profileRoleRepository.getAllRolesListByProfileId(entity.getId());
        String jwt = JwtUtil.encode(tempUsername, entity.getId(), roles);


        return new AppResponse<>(jwt, bundleService.getMessage("profile.username.update.success", language));
    }
}