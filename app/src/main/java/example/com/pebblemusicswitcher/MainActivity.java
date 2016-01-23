package example.com.pebblemusicswitcher;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

public class MainActivity extends AppCompatActivity {

    private TextView t2;
    private double result;
    private String bpm;

    private FetchBPMTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task = new FetchBPMTask();
        task.execute();


    }

    @Override
    protected void onResume() {
        super.onResume();

//        // Construct output String
//        StringBuilder builder = new StringBuilder();
//        builder.append("Pebble Info\n\n");
//
//        // Is the watch connected?
//        boolean isConnected = PebbleKit.isWatchConnected(this);
//        builder.append("Watch connected: " + (isConnected ? "true" : "false")).append("\n");
//
//        // What is the firmware version?
//        PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(this);
//        builder.append("Firmware version: ");
//        builder.append(info.getMajor()).append(".");
//        builder.append(info.getMinor()).append("\n");
//
//        // Is AppMesage supported?
//        boolean appMessageSupported = PebbleKit.areAppMessagesSupported(this);
//        builder.append("AppMessage supported: " + (appMessageSupported ? "true" : "false"));
//
//        TextView textView = (TextView)findViewById(R.id.text_view);
//        textView.setText(builder.toString());

    }

    private class FetchBPMTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Retrieve JSON String with the help of the HTTPJson class
            HTTPJson httpJson = HTTPJson.getInstance();
//            String url = "http://developer.echonest.com/api/v4/track/profile" +
//                    "?api_key=FILDTEOIK2HBORODV" +
//                    "&format=json" +
//                    "&id=TRTLKZV12E5AC92E11" +
//                    "&bucket=audio_summary";
            String url = "http://developer.echonest.com/api/v4/track/upload?" +
                    "api_key=FILDTEOIK2HBORODV&filetype=mp3";

            String jsonString = httpJson.connectHTTP(url);

            // Parse JSON String, store bpm data
            try {
                result = httpJson.readBPM(jsonString);
                bpm = Double.toString(result);

            } catch(Exception e) {
                Log.d("udgaogfqoevq", e + "");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            t2 = (TextView) findViewById(R.id.text2);
            t2.setText(bpm);
        }
    }


}
