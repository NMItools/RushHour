package com.internship.rushhour.domain.MicrosoftCalendar.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MicrosoftCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( length = 100000 )
    private String refreshToken;

    private Date expiresAt;

    @Column( length = 100000 )
    private String accessToken;

    public MicrosoftCalendar(){};

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public MicrosoftCalendar(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
