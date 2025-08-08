package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.PostDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePswdUpdateDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.ProfileService;
import api.giybat.uz.util.SpringSecurityUtil;
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
    @PutMapping("/updatePswd")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePswdUpdateDTO updatePswdDTO ,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updatePswd(updatePswdDTO, language));
    }
}
