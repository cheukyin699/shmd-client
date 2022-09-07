package ca.cheuksblog.shmdclient;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import androidx.core.util.Consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SHMDApi {
    public static class QueryParams {
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

    private static final String STATUS_PATH = "/status";
    private static final String DOWNLOAD_PATH = "/fs";
    private static final String MEDIA_PATH = "/media";
    private static final String COUNTMEDIA_PATH = "/mediacount";
    private static final String THUMBNAIL_PATH = "/thumbnail";

    private static SHMDApi instance;

    private String ip;
    private String port;
    private String baseUrl;
    private final Executor executor;

    /**
     * Constructor.
     *
     * Create new API instance. Populate static instance every time you do a construction.
     * @param ip
     * @param port
     * @param x
     */
    public SHMDApi(final String ip, final String port, final Executor x) {
        this.ip = ip;
        this.port = port;
        this.executor = x;

        updateBaseUrl();

        instance = this;
    }

    public static SHMDApi getInstance() {
        return instance;
    }

    public void setIp(String ip) {
        this.ip = ip;
        updateBaseUrl();
    }

    public void setPort(String port) {
        this.port = port;
        updateBaseUrl();
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

    public void updateBaseUrl() {
        this.baseUrl = "http://" + this.ip + ":" + this.port;
    }

    private static String inputStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    public String getAlbumThumbnailPath(final Media media) {
        if (media.album != null) {
            return this.baseUrl + THUMBNAIL_PATH + "?album=" + media.album;
        } else {
            return null;
        }
    }

    public void countMedia(final QueryParams params, final Consumer<Result<CountResponse, Exception>> callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(baseUrl + COUNTMEDIA_PATH + params.toURLParameters());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    callback.accept(new Result.Success<>(CountResponse.fromStream(connection.getInputStream())));
                } else {
                    callback.accept(new Result.Error<>(new Exception(inputStreamToString(connection.getErrorStream()))));
                }
            } catch (Exception e) {
                callback.accept(new Result.Error<>(e));
            }
        });
    }

    public void queryMedia(final QueryParams params, final Consumer<Result<QueryResponse, Exception>> callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(baseUrl + MEDIA_PATH + params.toURLParameters());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    callback.accept(new Result.Success<>(QueryResponse.fromStream(connection.getInputStream())));
                } else {
                    callback.accept(new Result.Error<>(new Exception(inputStreamToString(connection.getErrorStream()))));
                }
            } catch (Exception e) {
                callback.accept(new Result.Error<>(e));
            }
        });
    }

    public void getStatus(final Consumer<Result<Status, Exception>> callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(baseUrl + STATUS_PATH);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    callback.accept(new Result.Success<>(Status.fromStream(connection.getInputStream())));
                } else {
                    callback.accept(new Result.Error<>(new Exception(inputStreamToString(connection.getErrorStream()))));
                }
            } catch (Exception e) {
                callback.accept(new Result.Error<>(e));
            }
        });
    }

    public static class Status {
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

    public static class CountResponse {
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

    public static class QueryResponse {
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
}
