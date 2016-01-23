package example.com.pebblemusicswitcher;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dzklavier on 2016-01-23.
 */
public class HTTPJson {
    private String TAG_TEMPO = "tempo";
    private String TAG_RESPONSE = "response";
    private String TAG_TRACK = "track";
    private String TAG_AUDIOSUMMARY = "audio_summary";

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

    public String connectHTTP(String url) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            // Establishes connection with url
            // Create request to Echo Nest API, open the connection
            URL obj = new URL(url);
            urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("CURL");
            urlConnection.setRequestProperty("Content-Type",
                    "application/octet-stream");
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
        double tempo = 0.00;
        if (jsonString == null) {
            Log.d(TAG_NULL_STRING, "readJSONString method");
        }
        else {
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

        }
        return tempo;
    }

}
