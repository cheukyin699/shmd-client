package ca.cheuksblog.shmdclient;

import androidx.core.util.Consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class SHMDApi {
    private static final String STATUS_PATH = "/status";
    private static final String DOWNLOAD_PATH = "/fs";
    private static final String MEDIA_PATH = "/media";
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
    }

    public void setPort(String port) {
        this.port = port;
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

    public void getStatus(final Consumer<Result<Status, Exception>> callback) {
        // Use Activity.runOnUiThread
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
