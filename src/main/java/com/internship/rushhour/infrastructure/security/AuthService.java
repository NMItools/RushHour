package com.internship.rushhour.infrastructure.security;

import com.internship.rushhour.infrastructure.security.models.AuthenticationRequest;
import com.internship.rushhour.infrastructure.security.models.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil){

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        final String jwt = jwtUtil.createToken(authentication);
        return new AuthenticationResponse(jwt);
    }

}
