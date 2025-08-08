package api.giybat.uz.service;

import api.giybat.uz.config.SpringConfig;
import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePswdUpdateDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.ProfileRopsitory;
import api.giybat.uz.util.SpringSecurityUtil;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRopsitory profileRopsitory;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ResourceBundleService bundleService;

    public ProfileEntity getById(Integer id) {
        return profileRopsitory.findByIdAndVisibleTrue(id).orElseThrow(() ->
                new AppBadExeptions("Profile not found")
        );

    }


    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO detailUpdateDTO, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        profileRopsitory.updateDetail(profileId, detailUpdateDTO.getName());
        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", language));
    }


    public AppResponse<String> updatePswd(ProfilePswdUpdateDTO updatePswdDTO, AppLanguage language) {

        Integer profileId = SpringSecurityUtil.getCurrentUserId();

        String encodedPassword = profileRopsitory.findPasswordById(profileId);

        if (!bCryptPasswordEncoder.matches(updatePswdDTO.getOldPassword(), encodedPassword)) {
            return new AppResponse<>(bundleService.getMessage("reset.failed", language));
        }

        String newEncodedPassword = bCryptPasswordEncoder.encode(updatePswdDTO.getNewPassword());
        profileRopsitory.updatePassword(profileId, newEncodedPassword);

        return new AppResponse<>(bundleService.getMessage("reset.password.confirm.success", language));
    }

}