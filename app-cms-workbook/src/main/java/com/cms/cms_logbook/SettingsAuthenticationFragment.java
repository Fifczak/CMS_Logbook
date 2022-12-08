package com.cms.cms_logbook;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import com.cms.cms_logbook.databinding.FragmentSettingsAuthenticationBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class SettingsAuthenticationFragment extends Fragment {

    private FragmentSettingsAuthenticationBinding binding;
    private Button checkTokenButton;
    private Button getTokenButton;
    private EditText tokenInput;

    private void sendTokenRequest(EditText tokenInput,View v) {
        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://api.info-marine.com/api/access_token";
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
                        String tokenStr = (String) response.get("activation_token");


                        tokenInput.setText(tokenStr);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                },
                (Response.ErrorListener) error -> {
                    if (error.toString().contains("AuthFailureError")){
                        String no_access = "Authorization failed. Please check auth token.";
                        Snackbar mySnackbar = Snackbar.make(v, no_access, Snackbar.LENGTH_LONG);
                        final View view = mySnackbar.getView();
                        final TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                        mySnackbar.setDuration(4000);
                        mySnackbar.show();
                    }
                }
        ) {
            /**
             * Passing some request headers
             */
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


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSettingsAuthenticationBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkTokenButton = (Button) view.findViewById(R.id.checkTokenButton);
        getTokenButton = (Button) view.findViewById(R.id.getTokenButton);
        tokenInput = (EditText) view.findViewById(R.id.tokenInput);


        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String activationToken = tokenHandler.readActivationTokenFromFile(basePath);
        String authToken = tokenHandler.readAuthTokenFromFile(basePath);


        tokenInput.getText().insert(tokenInput.getSelectionStart(), activationToken);

        checkTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenText = tokenInput.getText().toString();
                TokenHandler tokenHandler = new TokenHandler();
                String path = getContext().getExternalFilesDir("CMSData") + "/activationToken.json";
                ContentResolver context = getContext().getContentResolver();
                int tokenValid = tokenHandler.checkActivationToken(tokenText, context);
                if (tokenValid == 0) {
                    Snackbar mySnackbar = Snackbar.make(view,
                            "Token active", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    tokenHandler.saveActivationTokenFile(tokenText, path);
                }else{
                    Snackbar mySnackbar = Snackbar.make(view,
                            "Bad token. Try again or contact: office@cm-solution.tech", Snackbar.LENGTH_LONG);
                    final View view = mySnackbar.getView();
                    final TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                }

            }

        });

        getTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendTokenRequest(tokenInput, v);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}