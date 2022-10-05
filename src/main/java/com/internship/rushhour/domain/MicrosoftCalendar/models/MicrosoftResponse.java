package com.internship.rushhour.domain.MicrosoftCalendar.models;

public class MicrosoftResponse {
    private String refreshToken;
    private String accessToken;
    private String expiresInSeconds;

    public MicrosoftResponse(){}

    public MicrosoftResponse(String refreshToken, String accessToken, String expiresInSeconds) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(String expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
