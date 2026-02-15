package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ConfirmCodeDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePhotoUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePswdUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
public class ProfileController {

@Autowired
private ProfileService profileService;

    @PutMapping("/detail")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO detailUpdateDTO ,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateDetail(detailUpdateDTO, language));
    }
    @PutMapping("/photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO photoUpdateDTO ,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updatePhoto(photoUpdateDTO, language));
    }
    @PutMapping("/updatePswd")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePswdUpdateDTO updatePswdDTO ,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updatePswd(updatePswdDTO, language));
    }

    @PutMapping("/username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO usernameDTO,
                                                              @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateUsername(usernameDTO, language));
    }

    @PutMapping("/username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody ConfirmCodeDTO confirmDTO,
                                                              @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateUsernameConfirm(confirmDTO, language));
    }
}
