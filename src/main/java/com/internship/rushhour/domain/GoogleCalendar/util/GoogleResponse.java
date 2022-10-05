package com.internship.rushhour.domain.GoogleCalendar.util;

import java.util.Date;

public record GoogleResponse(
        String accessToken,
        Date expirationDate
) {
}
