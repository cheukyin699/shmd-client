package ca.cheuksblog.shmdclient;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Status {
    public int total = 0;

    public static Status fromStream(InputStream stream) throws IOException {
        final JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
        final Status s = new Status();

        reader.beginObject();
        while (reader.hasNext()) {
            final String name = reader.nextName();

            if (name.equals("total")) {
                s.total = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return s;
    }
}
