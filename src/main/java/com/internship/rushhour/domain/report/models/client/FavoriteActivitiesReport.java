package com.internship.rushhour.domain.report.models.client;

public class FavoriteActivitiesReport implements Report{

    String provider;

    String activity;

    Long numberOfActivities;

    public FavoriteActivitiesReport(String provider, String activity, Long numberOfActivities) {
        this.provider = provider;
        this.activity = activity;
        this.numberOfActivities = numberOfActivities;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Long getNumberOfActivities() {
        return numberOfActivities;
    }

    public void setNumberOfActivities(Long numberOfActivities) {
        this.numberOfActivities = numberOfActivities;
    }
}
