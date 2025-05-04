package api.giybat.uz.controller;


import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.AuthDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.dto.auth.SmsVerificationDTO;
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
                                                               @RequestParam(value = "lang" , defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationEmailVerification(token, language));
    }
    @PostMapping("/registration/sms-verification")
    public ResponseEntity<ProfileDTO> smsVerification(@RequestBody SmsVerificationDTO dto,
                                                               @RequestParam(value = "lang" , defaultValue = "RU") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationSmsVerification(dto, language));
    }
    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok().body(authService.login(dto, language));
    }

}
