package com.internship.rushhour.domain.provider.models;

import java.time.LocalTime;
import java.util.Set;

public record ProviderDTOResponse(
        Long id,
        String name,
        String website,
        String businessDomain,
        String phone,
        LocalTime businessHoursStart,
        LocalTime businessHoursEnd,
        Set<String> workingDays)
{
}
