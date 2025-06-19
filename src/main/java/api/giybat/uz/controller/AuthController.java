package api.giybat.uz.controller;


import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registaration(dto, language));
    }
    @GetMapping("/registration/email-verification/{token}")
    public ResponseEntity<AppResponse<String>> emailVerification(@PathVariable("token") String token,
                                                                 @RequestParam(value = "lang",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationEmailVerification(token, language));
    }
    @PostMapping("/registration/sms-verification")
    public ResponseEntity<ProfileDTO> smsVerification(@Valid @RequestBody SmsVerificationDTO dto,
                                                      @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationSmsVerification(dto, language));
    }
    @PostMapping("/registration/sms-verification-resend")
    public ResponseEntity<AppResponse<String>> smsVerificationResend(@Valid @RequestBody SmsResendDTO dto,
                                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationSmsVerificationResend(dto, language));
    }

    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.login(dto, language));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                      @RequestHeader(value = "Accept-Language",defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.resetPassword(dto, language));
    }
}
