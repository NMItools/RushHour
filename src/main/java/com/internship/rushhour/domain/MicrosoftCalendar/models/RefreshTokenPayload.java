package com.internship.rushhour.domain.MicrosoftCalendar.models;

public class RefreshTokenPayload {
    private String refresh_token;

    public RefreshTokenPayload(){}

    public RefreshTokenPayload(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
