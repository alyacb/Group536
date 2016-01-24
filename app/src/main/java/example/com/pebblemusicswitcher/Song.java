package example.com.pebblemusicswitcher;

/**
 * Created by dzklavier on 2016-01-23.
 */
public class Song {
    private String name;
    private String bpm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public Song(String name, String bpm) {
        this.name = name;
        this.bpm = bpm;
    }
}
