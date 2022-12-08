package com.cms.cms_logbook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cms.cms_logbook.databinding.FragmentSettingsAuthorizationBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class SettingsAuthorizationFragment extends Fragment {

    private FragmentSettingsAuthorizationBinding binding;
    private Button checkAuthTokenButton;
    private Button getAuthTokenButton;
    private EditText authTokenInput;

    private void sendAuthTokenRequest(EditText authTokenInput,View v) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://api.info-marine.com/req/auth";
        JSONObject jsonObject = new JSONObject();
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            jsonObject.put("android_id", androidId);
        } catch (JSONException e) {
            System.out.println(e);

        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                (Response.Listener<JSONObject>) response -> {

                    try {
                        String tokenStr = (String) response.get("auth_token");
                        authTokenInput.setText(tokenStr);

                    } catch (Exception e) {
                        System.out.println(e);
                    }

                },
                (Response.ErrorListener) error -> {
                    System.out.println(error);
                }
        ) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(putRequest);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSettingsAuthorizationBinding.inflate(inflater,
                container,
                false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkAuthTokenButton = (Button) view.findViewById(R.id.checkAuthTokenButton);
        getAuthTokenButton = (Button) view.findViewById(R.id.getAuthTokenButton);
        authTokenInput = (EditText) view.findViewById(R.id.authTokenInput);


        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);

        authTokenInput.getText().insert(authTokenInput.getSelectionStart(), authToken);

        checkAuthTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenText = authTokenInput.getText().toString();
                TokenHandler tokenHandler = new TokenHandler();
                String path = getContext().getExternalFilesDir("CMSData") + "/authToken.json";

                Snackbar mySnackbar = Snackbar.make(view,
                        "Token saved", Snackbar.LENGTH_LONG);
                final View snackView = mySnackbar.getView();
                final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                mySnackbar.setDuration(4000);
                mySnackbar.show();
                tokenHandler.saveAuthTokenFile(tokenText, path);

            }
        });


        getAuthTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAuthTokenRequest(authTokenInput, v);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}