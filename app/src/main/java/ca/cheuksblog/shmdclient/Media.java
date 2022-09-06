package ca.cheuksblog.shmdclient;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

public class Media {
    public int id = 0;
    public String title = "";
    public String artist = null;
    public String album = null;
    public String location = null;

    public static Media fromObject(final JsonReader reader) throws IOException {
        Media m = new Media();

        reader.beginObject();
        while (reader.hasNext()) {
            final String name = reader.nextName();

            if (name.equals("id") && reader.peek() == JsonToken.NUMBER) {
                m.id = reader.nextInt();
            } else if (name.equals("title")) {
                m.title = reader.nextString();
            } else if (name.equals("artist") && reader.peek() == JsonToken.STRING) {
                m.artist = reader.nextString();
            } else if (name.equals("album") && reader.peek() == JsonToken.STRING) {
                m.album = reader.nextString();
            } else if (name.equals("location")) {
                m.location = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return m;
    }

    public static ArrayList<Media> fromArray(final JsonReader reader) throws IOException {
        ArrayList<Media> a = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            a.add(Media.fromObject(reader));
        }
        reader.endArray();

        return a;
    }
}
