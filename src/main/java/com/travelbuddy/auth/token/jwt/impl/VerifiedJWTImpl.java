package com.travelbuddy.auth.token.jwt.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.travelbuddy.auth.token.jwt.VerifiedJWT;

import java.util.List;
import java.util.Optional;

public class VerifiedJWTImpl implements VerifiedJWT {
    private final DecodedJWT decodedJWT;

    public VerifiedJWTImpl(DecodedJWT decodedJWT) {
        this.decodedJWT = decodedJWT;
    }

    @Override
    public String getUsername() {
        return decodedJWT.getSubject();
    }

    @Override
    public <T> Optional<T> getClaim(String claim, Class<T> clazz) {
        return Optional.ofNullable(decodedJWT.getClaim(claim).as(clazz));
    }

    @Override
    public <T> Optional<List<T>> getListClaim(String claim, Class<T> clazz) {
        return Optional.ofNullable(decodedJWT.getClaim(claim).asList(clazz));
    }

    @Override
    public boolean hasClaim(String claim) {
        return !decodedJWT.getClaim(claim).isNull();
    }
}
