package com.example.demo.controller;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.respone.ApiRespone;
import com.example.demo.dto.respone.AuthRespone;
import com.example.demo.dto.respone.IntrospectRespone;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.service.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping
    ApiRespone<AuthRespone> authUser(@RequestBody AuthRequest authRequest){

        return ApiRespone.<AuthRespone>builder()
                .result(authService.authUser(authRequest))
                .build();
    }
    @PostMapping("/token")
    ApiRespone<IntrospectRespone> authToken(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException
    {
       return ApiRespone.<IntrospectRespone>builder()
               .result(authService.authToken(introspectRequest))
               .build();
    }

    @PostMapping("/token/logout")
    ApiRespone<Void> logoutToken(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException
    {
        authService.logout(introspectRequest);
        return ApiRespone.<Void>builder()
                .build();
    }

}
