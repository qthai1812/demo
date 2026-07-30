package com.example.demo.service;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.respone.AuthRespone;
import com.example.demo.dto.respone.IntrospectRespone;
import com.example.demo.entity.InvalidToken;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.InvalidTokenRepository;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidTokenRepository invalidTokenRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;



    public AuthRespone authUser(AuthRequest authRequest){
        User user = userRepository.getUserByUsername(authRequest.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean authented = passwordEncoder.matches(authRequest.getPassword(),user.getPassword());

        if(!authented)
            throw new AppException(ErrorCode.USER_NOT_AUTHENTED);

        var token = generateToken(user);

        return  AuthRespone.builder()
                .token(token)
                .authented(authented)
                .build();

    }
    public IntrospectRespone authToken(IntrospectRequest introspectRequest) {
        var token = introspectRequest.getToken();
        boolean isValid = true;

        try {
            // Cố gắng giải mã và xác thực token
            vertifyToken(token);
        } catch (AppException | JOSEException | ParseException e) {
            // Nếu token sai, hết hạn, hoặc bị lỗi parse -> ném ra lỗi -> catch được ở đây
            isValid = false;
        }

        // Trả về kết quả true/false một cách êm ái
        return IntrospectRespone.builder()
                .valid(isValid)
                .build();
    }

    public void logout(IntrospectRequest request) throws JOSEException, ParseException {

        var token = request.getToken();
        var signJWT = vertifyToken(token);

        InvalidToken invalidToken = InvalidToken.builder()
                .id(signJWT.getJWTClaimsSet().getJWTID())
                .expiryTime(signJWT.getJWTClaimsSet().getExpirationTime())
                .build();
        invalidTokenRepository.save(invalidToken);
    }

    public SignedJWT vertifyToken(String token) throws JOSEException, ParseException
    {
        var verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.USER_NOT_AUTHENTED);

        if(invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID().toString()))
            throw new AppException(ErrorCode.USER_NOT_AUTHENTED);
        return signedJWT;
    }


    public String generateToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("quocthai.com")
                .issueTime(new Date())
                .expirationTime( new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                        ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermission()))
                    role.getPermission()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

}
