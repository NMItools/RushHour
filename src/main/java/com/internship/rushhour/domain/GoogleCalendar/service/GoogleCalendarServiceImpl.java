package com.internship.rushhour.domain.GoogleCalendar.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.internship.rushhour.domain.GoogleCalendar.util.GoogleResponse;
import com.internship.rushhour.infrastructure.encryptors.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService{

    private final String refreshToken;
    private final String clientId;
    private final String clientSecret;
    private final String calendarId;
    private final EncryptionUtil encryptionUtil;
    private final String grantType;
    private final String appName;
    private final String summaryFormat;

    @Autowired
    public  GoogleCalendarServiceImpl(@Value("${google.service.token}") String refreshToken,
                                      @Value("${google.service.client.id}") String clientId,
                                      @Value("${google.service.client.secret}") String clientSecret,
                                      @Value("${google.service.calendar.id}") String calendarId,
                                      @Value("${google.service.grant.type}") String grantType,
                                      @Value("${google.service.app.name}") String appName,
                                      @Value("${google.service.summary.format}") String summaryFormat,
                                      EncryptionUtil encryptionUtil){
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.calendarId = calendarId;
        this.encryptionUtil = encryptionUtil;
        this.grantType = grantType;
        this.appName = appName;
        this.summaryFormat = summaryFormat;
    }

    @Override
    public String saveEvent(Collection<String> attendees, String location, LocalDateTime startTime, LocalDateTime endTime)
            throws GeneralSecurityException, IOException, ParseException {

        Calendar calendar = createCalendar();

        Event event = createEvent(attendees, location, startTime, endTime);

        return calendar.events().insert(calendarId, event).execute().getId();
    }

    @Override
    public void updateEvent(String eventId, LocalDateTime startTime, LocalDateTime endTime, String employee)
            throws GeneralSecurityException, IOException, ParseException {

        if(startTime == null && endTime == null && employee == null) return;

        Calendar calendar = createCalendar();

        Event event = calendar.events().get(calendarId, eventId).execute();

        if(startTime != null)
            event.setStart(new EventDateTime().setDateTime(createDateTime(startTime)));

        if(endTime != null)
            event.setEnd(new EventDateTime().setDateTime(createDateTime(endTime)));

        if(employee != null)
            event.getAttendees().set(0, new EventAttendee().setEmail(employee));

        calendar.events().update(calendarId, eventId, event).execute();
    }

    @Override
    public void deleteEvent(String eventId) throws GeneralSecurityException, IOException {
        Calendar calendar = createCalendar();

        calendar.events().delete(calendarId, eventId).execute();
    }

    private GoogleResponse updateAccessInfo(TokenResponse response){
        String accessToken = response.getAccessToken();
        Long expiresIn = response.getExpiresInSeconds();
        Date expirationDate = Date.from(Instant.now().plusSeconds(expiresIn));
        return new GoogleResponse(accessToken, expirationDate);
    }

    private GoogleResponse refreshAccessToken() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        String decryptedToken = encryptionUtil.doAESDecryption(refreshToken);

        return updateAccessInfo(new GoogleRefreshTokenRequest(new NetHttpTransport(), new GsonFactory(),
                decryptedToken, clientId, clientSecret).setGrantType(grantType).execute());
    }

    private HttpRequestInitializer createRequestFromAccessToken(String accessToken, Date expirationDate){
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, expirationDate));

        return new HttpCredentialsAdapter(credentials);
    }

    private Calendar createCalendar() throws IOException, GeneralSecurityException {
        GoogleResponse googleResponse = refreshAccessToken();

        HttpRequestInitializer httpRequestInitializer = createRequestFromAccessToken(googleResponse.accessToken(), googleResponse.expirationDate());

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GsonFactory jsonFactory = new GsonFactory();

        return new  Calendar.Builder(httpTransport, jsonFactory, httpRequestInitializer).setApplicationName(appName).build();
    }

    private DateTime createDateTime(LocalDateTime time) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        Date date = isoFormat.parse(time.toString());

        return new DateTime(date);
    }

    private Event createEvent(Collection<String> attendees, String location, LocalDateTime startTime, LocalDateTime endTime) throws ParseException {
        Event event = new Event()
                .setSummary(String.format(summaryFormat, location))
                .setLocation(location);

        event.setStart(new EventDateTime().setDateTime(createDateTime(startTime)));
        event.setEnd(new EventDateTime().setDateTime(createDateTime(endTime)));

        List<EventAttendee> eventAttendees = attendees.stream().map((email) -> new EventAttendee().setEmail(email)).toList();

        event.setAttendees(eventAttendees);

        return event;
    }

}