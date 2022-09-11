package ca.cheuksblog.shmdclient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

public class LocalApi implements Api {
    public static final int PERMS_REQUEST_CODE = 1;
    private Activity rootActivity;

    public LocalApi(final Activity activity) {
        rootActivity = activity;
        requestAllPermissions(rootActivity);
    }

    private static void requestAllPermissions(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMS_REQUEST_CODE
            );
        }
    }

    @Override
    public String getAlbumThumbnailPath(Media media) {
        return null;
    }

    @Override
    public void countMedia(QueryParams params, Consumer<Result<CountResponse, Exception>> callback) {
        callback.accept(new Result.Success<>(new CountResponse()));
    }

    @Override
    public void queryMedia(QueryParams params, Consumer<Result<QueryResponse, Exception>> callback) {
        callback.accept(new Result.Success<>(new QueryResponse()));
    }

    @Override
    public void getStatus(Consumer<Result<Status, Exception>> callback) {
        callback.accept(new Result.Success<>(new Status()));
    }
}
