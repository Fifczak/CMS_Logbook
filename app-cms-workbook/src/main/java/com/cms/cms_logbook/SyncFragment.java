package com.cms.cms_logbook;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileReader;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cms.cms_logbook.databinding.FragmentSyncBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SyncFragment extends Fragment {

    private Animation slideRight;
    private Animation slideLeft;

    private static final String deviceId = "deviceId";

    private String mdeviceId;

    public SyncFragment() {
        // Required empty public constructor
    }

    public LayoutInflater public_inflater;
    public ViewGroup public_container;
    private FragmentSyncBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
        }

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        public_inflater = inflater;
        public_container = container;
        binding = FragmentSyncBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        slideLeft = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_anim);
        slideRight = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_from_right);

        Boolean if_internet = hasActiveInternetConnection(view.getContext());
        if (if_internet){
            binding.uploadDataButton.setVisibility(View.VISIBLE);
            binding.downloadStructureButton.setVisibility(View.VISIBLE);
            binding.downloadManualsButton.setVisibility(View.VISIBLE);
            binding.uploadDataButton.clearAnimation();
            binding.downloadStructureButton.clearAnimation();
            binding.downloadManualsButton.clearAnimation();
            binding.uploadDataButton.startAnimation(slideLeft);
            binding.downloadStructureButton.startAnimation(slideRight);
            binding.downloadManualsButton.startAnimation(slideLeft);
            binding.noInternetInfo.setVisibility(View.INVISIBLE);
        } else {
            binding.uploadDataButton.setVisibility(View.INVISIBLE);
            binding.downloadStructureButton.setVisibility(View.INVISIBLE);
            binding.downloadManualsButton.setVisibility(View.INVISIBLE);
            binding.noInternetInfo.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.downloadStructureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.uploadDataButton.setEnabled(false);
                binding.downloadStructureButton.setEnabled(false);
                binding.downloadManualsButton.setEnabled(false);
                binding.loadGif.setVisibility(View.VISIBLE);
                getRequest(view);

            }
        });
        binding.uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(view);
            }
        });
    }


    public List<JSONArray> getRequest(View v){
        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);

        RequestQueue volleyQueue = Volley.newRequestQueue(this.getContext());
        String url = "https://api.info-marine.com/api/sync/qrdata/" + authToken;
        List<JSONArray> reponse_l = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONArray>) response -> {
                    try {
                        getFiles(response);
                    } finally{}
                },
                (Response.ErrorListener) error -> {
                    binding.loadGif.setVisibility(View.INVISIBLE);
                    binding.uploadDataButton.setEnabled(true);
                    binding.downloadStructureButton.setEnabled(true);
                    binding.downloadManualsButton.setEnabled(true);
                    Log.e("sync error", error.toString());

                    String no_access = "Authorization failed. Please check auth token.";
                    Snackbar mySnackbar = Snackbar.make(v, no_access, Snackbar.LENGTH_LONG);
                    final View snackView = mySnackbar.getView();
                    final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("apikey", authToken);
                return headers;
            }
        };


        volleyQueue.add(jsonArrayRequest);

        return reponse_l;
    }


    private void sendRequest(View v) {
        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);


        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://api.info-marine.com/api/sync/send_qrdata";
        JSONArray jsonObjectArray = new JSONArray();

        try {
            FileReader f = new FileReader(basePath + "/qrdata.json");
            BufferedReader br = new BufferedReader(f);
            String currentJSONString  = "";
            while ((currentJSONString = br.readLine()) != null) {
                jsonObjectArray = new JSONArray(currentJSONString);

            }
        }catch(Exception e){

        }

        JsonArrayRequest putRequest = new JsonArrayRequest(Request.Method.PUT, url, jsonObjectArray,
                (Response.Listener<JSONArray>) response -> {
                    try {
                        getFiles(response);
                    } finally{}
                },
                (Response.ErrorListener) error -> {
                    if (error.toString().contains("AuthFailureError")){
                        Log.e("sync error", error.toString());
                        String no_access = "Authorization failed. Please check auth token.";
                        Snackbar mySnackbar = Snackbar.make(v, no_access, Snackbar.LENGTH_LONG);
                        final View snackView = mySnackbar.getView();
                        final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                        mySnackbar.setDuration(4000);
                        mySnackbar.show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("apikey", authToken);
                return headers;
            }
        };

        queue.add(putRequest);
    }



    public void getFiles(JSONArray response){
        JSONArray jsonArray = response;
        downloadFile("qrdata.json");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject explrObject = jsonArray.getJSONObject(i); // you will get the json object
                String filename = (explrObject.getString("imId")) + ".pdf";
                downloadFile(filename);
            } catch (Exception e){
                System.out.println(e);
            }
            //do the stuff
        }
        Toast.makeText(getActivity(), "Download Complete", Toast.LENGTH_LONG).show();

    }

    public void downloadFile(String filename){

        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);

        File file = new File(getContext().getExternalFilesDir("CMSData") + "/" + filename);
        boolean deleted = file.delete();
        DownloadManager downloadmanager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://api.info-marine.com/api/download/" + filename);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(filename);
        request.setDescription("Downloading");
        request.addRequestHeader("apikey", authToken);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalFilesDir(getContext(), "/CMSData/", filename);
        downloadmanager.enqueue(request);
        binding.loadGif.setVisibility(View.INVISIBLE);
        binding.uploadDataButton.setEnabled(true);
        binding.downloadStructureButton.setEnabled(true);
        binding.downloadManualsButton.setEnabled(true);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                System.out.println( "Error checking internet connection: " + e);
            }
        } else {
            System.out.println("No network available!");
        }
        return false;
    }

}