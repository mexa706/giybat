package api.giybat.uz.controller;


import api.giybat.uz.exps.AppBadExeptions;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ExeptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  @NotNull HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  @NotNull WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        List<String> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())//bu xato aniqlangan maydonning nomini qaytaradi. Masalan, agar maydon nomi email bo'lsa, bu method email ni qaytaradi.
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }










    @ExceptionHandler(AppBadExeptions.class)
    ResponseEntity<String> handle (AppBadExeptions e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> handle (RuntimeException e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

}
