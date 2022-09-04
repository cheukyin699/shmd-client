package ca.cheuksblog.shmdclient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    private static final int INTERNET_REQUEST = 101;

    public StatusFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatusFragment.
     */
    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);

        TextView statusText = view.findViewById(R.id.statusTextView);
        Button browseButton = view.findViewById(R.id.browseButton);
        Button manageDownloadsButton = view.findViewById(R.id.manageDownloadsButton);
        Button settingsButton = view.findViewById(R.id.settingsButton);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        browseButton.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_statusFragment_to_browseFragment));
        manageDownloadsButton.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_statusFragment_to_manageDownloadsFragment));
        settingsButton.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_statusFragment_to_settingsFragment));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshStatusText(statusText, swipeRefreshLayout);
        });

        refreshStatusText(statusText, swipeRefreshLayout);

        return view;
    }

    private String buildStatusText(Status status, boolean online) {
        final int downloaded = 0;
        final int total = status == null ? 0 : status.total;
        return getString(
                R.string.server_status_text,
                getString(online ? R.string.online_text : R.string.offline_text),
                SHMDApi.getInstance().getIp()) +
                '\n' +
                getString(
                        R.string.download_status_text,
                        downloaded,
                        total
                );
    }

    private void refreshStatusText(TextView statusText, SwipeRefreshLayout swipeRefreshLayout) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            SHMDApi.getInstance().getStatus(statusResult -> {
                if (statusResult.hasError()) {
                    final Exception e = statusResult.getError();
                    Log.e("shmd", e.toString());
                    getActivity().runOnUiThread(() -> statusText.setText(buildStatusText(null, false)));
                } else {
                    final Status status = statusResult.getData();
                    getActivity().runOnUiThread(() -> statusText.setText(buildStatusText(status, true)));
                }
            });
        } else {
            Toast.makeText(getContext(), "Doesn't have internet permissions", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setRefreshing(false);
    }
}