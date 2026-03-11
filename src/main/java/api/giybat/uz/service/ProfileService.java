package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ConfirmCodeDTO;
import api.giybat.uz.dto.profile.*;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.mapper.ProfileDetailMapper;
import api.giybat.uz.repository.PostRopsitory;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.repository.ProfileRopsitory;
import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.PhoneUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    @Autowired
    private PostRopsitory postRopsitory;

    public ProfileEntity getById(Integer id) {

        return profileRopoitory.findByIdAndVisibleTrue(id).orElseThrow(() -> {
            log.error("getById failed: id {} ", id);
            throw new AppBadException("Profile not found");

        });

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
            log.warn("Reset password failed ");
            throw new AppBadException(bundleService.getMessage("reset.failed", language));
        }

        String newEncodedPassword = bCryptPasswordEncoder.encode(updatePswdDTO.getNewPassword());
        profileRopoitory.updatePassword(profileId, newEncodedPassword);

        return new AppResponse<>(bundleService.getMessage("reset.password.confirm.success", language));
    }


    public AppResponse<String> updateUsername(ProfileUsernameUpdateDTO usernameUpdateDTO, AppLanguage language) {

        //check

        Optional<ProfileEntity> profile = profileRopoitory.findByUsernameAndVisibleTrue(usernameUpdateDTO.getUsername());

        if (profile.isPresent()) {
            log.warn("Email or phone exists : username {} ", usernameUpdateDTO.getUsername());
            throw new AppBadException(bundleService.getMessage("email.phone.exists", language));
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
            log.warn("Contact format invalid : username {} ", usernameUpdateDTO.getUsername());
            throw new AppBadException(bundleService.getMessage("contact.format.invalid", language));
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


    public Page<ProfileFilterResponseDTO> filter(ProfileFilterDTO dto, AppLanguage language, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProfileDetailMapper> resultList = null;
        if (dto == null || dto.getQuery() == null) {
            resultList = profileRopoitory.filter(pageRequest);
        } else {
             resultList = profileRopoitory.filter(dto.getQuery(), pageRequest);
        }

        List<ProfileFilterResponseDTO> List = resultList.stream().map(entity -> toDTO(entity)).toList();
        return new PageImpl<>(List, PageRequest.of(page, size), resultList.getTotalElements());
    }

    public AppResponse<String> status(Integer id, GeneralStatus status, AppLanguage language) {
        if (status.equals(GeneralStatus.BLOCK)) {
            profileRopoitory.changeStatus(id, GeneralStatus.ACTIVE);
        } else if (status.equals(GeneralStatus.ACTIVE)) {
            profileRopoitory.changeStatus(id, GeneralStatus.BLOCK);
        } else {
            throw new AppBadException(bundleService.getMessage("wrong.status", language));
        }
        return new AppResponse<>(bundleService.getMessage("profile.status.update.success", language));
    }

    public AppResponse<String> delete(Integer id, AppLanguage language) {
        if (getById(id) != null) {
            profileRopoitory.delete(id);
        }
        return new AppResponse<>(bundleService.getMessage("profile.status.update.success", language));
    }

    /*private ProfileFilterResponseDTO toDTO(ProfileEntity profileEntity) {
        ProfileFilterResponseDTO dto = new ProfileFilterResponseDTO();
        dto.setId(profileEntity.getId());
        dto.setUsername(profileEntity.getUsername());
        dto.setName(profileEntity.getName());
        if (profileEntity.getPhotoId() != null) {
            dto.setPhoto(attachService.attachDTO(profileEntity.getPhotoId()));
        }
        if (profileEntity.getRoleList() != null) {
            List<ProfileRoleEntity> roles = profileEntity.getRoleList();
            dto.setRoles(roles.stream().map(ProfileRoleEntity::getRoles).toList());
        }
        dto.setCreatedDate(profileEntity.getCreatedDate());
        dto.setStatus(profileEntity.getStatus());
        dto.setPostCount(postRopsitory.getPostCountByProfileId(profileEntity.getId()));
        return dto;
    }*/

    private ProfileFilterResponseDTO toDTO(ProfileDetailMapper detailMapper) {
        ProfileFilterResponseDTO dto = new ProfileFilterResponseDTO();
        dto.setId(detailMapper.getId());
        dto.setUsername(detailMapper.getUsername());
        dto.setName(detailMapper.getName());
        if (detailMapper.getPhotoId() != null) {
            dto.setPhoto(attachService.attachDTO(detailMapper.getPhotoId()));
        }
        if(detailMapper.getRoles()!=null) {
           List<ProfileRole> list= Arrays.stream(detailMapper.getRoles().split(","))
                    .map(ProfileRole::valueOf).toList();
        dto.setRoles(list);
        }
        dto.setCreatedDate(detailMapper.getCreatedDate());
        dto.setStatus(detailMapper.getStatus());
        dto.setPostCount(postRopsitory.getPostCountByProfileId(detailMapper.getId()));
        return dto;
    }

}
