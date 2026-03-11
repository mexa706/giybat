package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ConfirmCodeDTO;
import api.giybat.uz.dto.profile.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.ProfileService;
import api.giybat.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
@Tag(name = "ProfileController", description = "API set for working with Profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO detailUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateDetail(detailUpdateDTO, language));
    }

    @PutMapping("/photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO photoUpdateDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updatePhoto(photoUpdateDTO, language));
    }

    @PutMapping("/updatePswd")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePswdUpdateDTO updatePswdDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updatePswd(updatePswdDTO, language));
    }

    @PutMapping("/username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO usernameDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateUsername(usernameDTO, language));
    }

    @PutMapping("/username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody ConfirmCodeDTO confirmDTO,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language) {

        return ResponseEntity.ok().body(profileService.updateUsernameConfirm(confirmDTO, language));
    }

    @PostMapping("/filter")
    @Operation(summary = " profile filter", description = "Api used for filtering profile list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ProfileFilterResponseDTO>> filter(@RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language,
                                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "6") Integer size,
                                                                 @Valid @RequestBody ProfileFilterDTO profileFilterDTO) {
        return ResponseEntity.ok().body(profileService.filter(profileFilterDTO, language, PageUtil.page(page), size));
    }


    @PutMapping("/status/{id}")
    @Operation(summary = "Change profile status", description = "Api used for changing profile status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> status(@RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language,
                                                      @PathVariable("id") Integer id,
                                                      @Valid @RequestBody ProfileStatusDTO dto) {
        return ResponseEntity.ok().body(profileService.status(id, dto.getStatus(), language));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile ", description = "Api used for deleting profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> delete(@RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language,
                                                      @PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(profileService.delete(id,language));
    }




}
