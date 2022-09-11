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

public class SHMDApi implements Api {

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

    @Override
    public String getAlbumThumbnailPath(final Media media) {
        if (media.album != null) {
            return this.baseUrl + THUMBNAIL_PATH + "?album=" + media.album;
        } else {
            return null;
        }
    }

    @Override
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

    @Override
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

    @Override
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
}
