package api.giybat.uz.controller;


import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.profile.ProfileDTO;
import api.giybat.uz.dto.auth.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController",description = "Controller for Authorization and authenticotion")
@Slf4j
public class AuthController {
    

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    @Operation(summary = "Profile registration", description = "Api used for registration new profile")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("Registration: name {} , username {}" ,dto.getUsername(),dto.getUsername());
        return ResponseEntity.ok().body(authService.registaration(dto, language));
    }


    @GetMapping("/registration/email-verification/{token}")
    @Operation(summary = "Email verification", description = "Api used for  registration  verification using email")
    public ResponseEntity<AppResponse<String>> emailVerification(@PathVariable("token") String token,
                                                                 @RequestParam(value = "lang",defaultValue = "RU") AppLanguage language) {
        log.info("Registration email-verification: token {}" , token);
        return ResponseEntity.ok().body(authService.registrationEmailVerification(token, language));
    }
    @PostMapping("/registration/sms-verification")
    @Operation(summary = "SMS verification", description = "Api used for  registration  verification using SMS")
    public ResponseEntity<ProfileDTO> smsVerification(@Valid @RequestBody SmsVerificationDTO dto,
                                                      @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("Registration sms-verification:  {} " , dto);
        return ResponseEntity.ok().body(authService.registrationSmsVerification(dto, language));
    }
    @PostMapping("/registration/sms-verification-resend")
    @Operation(summary = "SMS verification resend", description = "Api used for resend SMS verification code")
    public ResponseEntity<AppResponse<String>> smsVerificationResend(@Valid @RequestBody SmsResendDTO dto,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("SMS verification resend: phone {} " , dto.getPhone());
        return ResponseEntity.ok().body(authService.registrationSmsVerificationResend(dto, language));
    }

    @PostMapping("/login")
    @Operation(summary = "Login (Auth) api", description = "Api used for login to system")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("Login: {}" ,dto.getUsername());
        return ResponseEntity.ok().body(authService.login(dto, language));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Api used for password reset")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                      @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("Reset password: username{}" ,dto.getUsername());
        return ResponseEntity.ok().body(authService.resetPassword(dto, language));
    }

    @PostMapping("/reset-password-confirm")
    @Operation(summary = "Reset password confirm", description = "Api used for password reset confirm")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                             @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        log.info("Reset password confirm: username{} , confirmCode {}" ,dto.getUsername(), dto.getCode());
        return ResponseEntity.ok().body(authService.resetPasswordConfirm(dto, language));
    }
}
