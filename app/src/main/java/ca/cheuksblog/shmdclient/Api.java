package ca.cheuksblog.shmdclient;

import android.util.JsonReader;
import android.util.JsonToken;
import androidx.core.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public interface Api {
    class QueryParams {
        public String artist = null;
        public String album = null;
        public String keyword = null;
        public int offset = 0;
        public int limit = 100;

        public String toURLParameters() {
            String params = "?offset=" + offset + "&limit=" + limit;

            if (artist != null) {
                params += "&artist=" + artist;
            }

            if (album != null) {
                params += "&album=" + album;
            }

            if (keyword != null) {
                params += "&keyword=" + keyword;
            }

            return params;
        }
    }

    class Status {
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

    class CountResponse {
        public int total;

        public static CountResponse fromStream(InputStream stream) throws IOException {
            final JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            final CountResponse response = new CountResponse();

            reader.beginObject();
            while (reader.hasNext()) {
                final String name = reader.nextName();

                if (name.equals("total")) {
                    response.total = reader.nextInt();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return response;
        }
    }

    class QueryResponse {
        public boolean success;
        public String error;
        public ArrayList<Media> data;

        public static QueryResponse fromStream(InputStream stream) throws IOException {
            final JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            final QueryResponse response = new QueryResponse();

            reader.beginObject();
            while (reader.hasNext()) {
                final String name = reader.nextName();

                if (name.equals("success")) {
                    response.success = reader.nextBoolean();
                } else if (name.equals("error") && reader.peek() == JsonToken.STRING) {
                    response.error = reader.nextString();
                } else if (name.equals("data")) {
                    response.data = Media.fromArray(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return response;
        }
    }

    String getAlbumThumbnailPath(final Media media);
    void countMedia(final QueryParams params, final Consumer<Result<CountResponse, Exception>> callback);
    void queryMedia(final QueryParams params, final Consumer<Result<QueryResponse, Exception>> callback);
    void getStatus(final Consumer<Result<Status, Exception>> callback);
}
