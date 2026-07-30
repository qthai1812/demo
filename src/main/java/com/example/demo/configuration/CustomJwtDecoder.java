package com.example.demo.configuration;

import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.service.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final AuthService authService;
    private NimbusJwtDecoder nimbusJwtDecoder=null;
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    @Override
    public Jwt decode(String token) throws JwtException {
        try{
            authService.vertifyToken(token);
        }
        catch (Exception e){
            throw new JwtException((e.getMessage()));
        }


       if(Objects.isNull(nimbusJwtDecoder)){
           SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
           nimbusJwtDecoder = NimbusJwtDecoder
                   .withSecretKey(secretKeySpec)
                   .macAlgorithm(MacAlgorithm.HS512)
                   .build();
       }

        return nimbusJwtDecoder.decode(token);
    }
}
