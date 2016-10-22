package org.magictvapi.channel.m6replay.service;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;

import org.magictvapi.Config;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thomas on 13/03/2016.
 *
 * Load current played video (direct)
 */
public class SfrTvLoger  {
    // start date format : yyyyddmmhhss
    private final static String LOGON_URL = "https://wscustomercare.nctvservices.fr/CustomerCare.svc/authenticate";
    private final static String LOGON_BODY = "{\"login\":\"%s\",\"password\":\"%s\",\"apiVersion\":\"1.5\",\"param\":{\"DeviceSoftVersion\":\"MRA58K\",\"DeviceModel\":\"%s\",\"DeviceOs\":\"android\"}}";

    private final static String REFRESH_URL = "https://wscustomercare.nctvservices.fr/CustomerCare.svc/profiletokenauthenticate";
    private final static String REFRESH_BODY = "{\"ProfileToken\": \"%s\",\"apiVersion\": \"1.5\",\"param\": {\"DeviceModel\": \"%s\",\"DeviceOs\": \"android\"}}";

    private final static String PREFS_NAME = "SFR_AUTHENTICATION";
    private final static String PREF_TOKEN = "SFR_AUTHENTICATION_TOKEN";

    private final static String TAG = SfrTvLoger.class.getName();

    private String profileToken = null;

    public static final SfrTvLoger INSTANCE = new SfrTvLoger();

    private SfrTvLoger() {
    }

    public String logon(String login, String password) {
        this.profileToken = null;
        HttpURLConnection client = null;
        Log.d(TAG, "Logon to sfr");
        try {
            client = (HttpURLConnection) new URL(LOGON_URL).openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            BufferedOutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            outputPost.write(String.format(LOGON_BODY, new Object[]{login, password, Build.MODEL}).getBytes());
            outputPost.flush();
            outputPost.close();

            JsonReader jsonReader = new JsonReader(new InputStreamReader(client.getInputStream()));
            Log.d(TAG, "response OK");
            readResult(jsonReader);

            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }

        return this.profileToken;
    }

    public String getToken() {
        refreshToken();
        return this.profileToken;
    }

    private void refreshToken() {
        SharedPreferences settings = Config.context.getSharedPreferences(PREFS_NAME, 0);
        String token = settings.getString(PREF_TOKEN, "");

        HttpURLConnection client = null;
        try {
            client = (HttpURLConnection) new URL(REFRESH_URL).openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            BufferedOutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            outputPost.write(String.format(REFRESH_BODY, new Object[]{token, Build.MODEL}).getBytes());
            outputPost.flush();
            outputPost.close();

            JsonReader jsonReader = new JsonReader(new InputStreamReader(client.getInputStream()));
            readResult(jsonReader);

            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    private void readResult(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("Data".equals(name)) {
                readData(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }

    private void readData(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("Profiles".equals(name)) {
                readProfiles(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }

    private void readProfiles(JsonReader jsonReader) throws IOException {
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            readProfile(jsonReader);
            jsonReader.endObject();
        }
        jsonReader.endArray();
    }

    private void readProfile(JsonReader jsonReader) throws IOException {
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("ProfileToken".equals(name)) {
                profileToken = jsonReader.nextString();
                SharedPreferences settings = Config.context.getSharedPreferences(PREFS_NAME, 0);
                settings.edit().putString(PREF_TOKEN, profileToken).commit();
            } else {
                jsonReader.skipValue();
            }
        }
    }

}
