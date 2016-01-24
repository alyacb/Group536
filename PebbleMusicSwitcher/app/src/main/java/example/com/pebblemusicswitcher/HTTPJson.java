package example.com.pebblemusicswitcher;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Created by dzklavier on 2016-01-23.
 */
public class HTTPJson {
    private String TAG_TEMPO = "tempo";
    private String TAG_RESPONSE = "response";
    private String TAG_TRACK = "track";
    private String TAG_AUDIOSUMMARY = "audio_summary";
    private String TAG_PROFILE_URL =
            "http://developer.echonest.com/api/v4/track/profile?" +
                    "api_key=FX1FEUL6L9FYUNGTU&format=json&id=";
    private String TAG_BUCKET = "&bucket=audio_summary";

    private String TAG_ID = "id";
    private String TAG_STATUS = "status";
    private String TAG_MESSAGE = "message";

    // Error Tags
    private final String TAG_HTTP_ERROR = "HTTP ERROR";
    private final String TAG_PARSE = "PARSE";
    private final String TAG_NULL_STRING = "NULL JSON STRING";

    private static HTTPJson instance;

    private HTTPJson() {

    }

    public static HTTPJson getInstance() {
        if (instance == null) {
            instance = new HTTPJson();
        }

        return instance;
    }

    public String connectHTTP(FileInputStream song) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            // Establishes connection with url
            // Create request to Echo Nest API, open the connection
            String request        = "http://developer.echonest.com/api/v4/track/upload" +
                    "?api_key=FILDTEOIK2HBORODV&filetype=mp3";
            URL    url            = new URL( request );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setUseCaches(false);

            try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                IOUtils.copy(song, wr);
            }

            // Read the page using input stream into a String
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();

            // If empty inputStream, do nothing
            if(inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine + "\n");
            }

            if(buffer.length() == 0) {
                // Stream empty, don't parse
                return null;
            }

            // Convert StringBuffer to String, return JSON String
            result = buffer.toString();
            return result;

        } catch (MalformedURLException e) {
            Log.d(TAG_HTTP_ERROR, e + "");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG_HTTP_ERROR, e + "");
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("HTTPJson", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    public String connectHTTP(String url) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            // Establishes connection with url
            // Create request to UW API, open the connection
            URL obj = new URL(url);
            urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the page using input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            // If empty inputStream, do nothing
            if(inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine + "\n");
            }

            if(buffer.length() == 0) {
                // Stream empty, don't parse
                return null;
            }

            // Convert StringBuffer to String, return JSON String
            result = buffer.toString();
            return result;

        } catch (MalformedURLException e) {
            Log.d(TAG_HTTP_ERROR, e + "");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG_HTTP_ERROR, e + "");
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("HTTPJson", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    public double readBPM(String jsonString) {
        String result = "";
        double tempo = 2.00;
        String id = readId(jsonString);
        String message = readMessage(jsonString);
        System.out.println(message);
        if (jsonString == null) {
            Log.d(TAG_NULL_STRING, "readJSONString method");
            System.out.println("JSONSTRING IS NULL");
        } else if (message == "pending") {
            String url = TAG_PROFILE_URL + id + TAG_BUCKET;
            String profile = connectHTTP(url);
            if (readMessage(profile).equalsIgnoreCase("Success")) {
                tempo = readBPM(profile);
                return tempo;
            } else {
                System.out.println(profile);
            }
        }
        else {
            jsonString = connectHTTP(TAG_PROFILE_URL + id + TAG_BUCKET);
            String newMessage = readMessage(jsonString);
            if (newMessage.equalsIgnoreCase("Success")) {
                try {
                    JSONObject reader = new JSONObject(jsonString);
                    JSONObject readerResponse = reader.getJSONObject(TAG_RESPONSE);
                    JSONObject readerTrack = readerResponse.getJSONObject(TAG_TRACK);
                    JSONObject readerAudio = readerTrack.getJSONObject(TAG_AUDIOSUMMARY);
                    result = readerAudio.getString(TAG_TEMPO);
                    tempo = Double.parseDouble(result);
                    return tempo;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                readBPM(jsonString);
            }

        }
        return tempo;
    }

    public String readId(String jsonString) {
        String id = "HAHAHAWRONGIDBRO";
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONObject readerResponse = reader.getJSONObject(TAG_RESPONSE);
            JSONObject readerTrack = readerResponse.getJSONObject(TAG_TRACK);
            id = readerTrack.getString(TAG_ID);
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String readMessage(String jsonString) {
        String message = "MRPANINOSSTATUS";
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONObject readerResponse = reader.getJSONObject(TAG_RESPONSE);
            JSONObject readerStatus = readerResponse.getJSONObject(TAG_STATUS);
            message = readerStatus.getString(TAG_MESSAGE);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }





}
