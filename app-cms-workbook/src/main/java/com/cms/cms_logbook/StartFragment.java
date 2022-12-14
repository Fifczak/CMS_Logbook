package com.cms.cms_logbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.cms.cms_logbook.databinding.FragmentStartBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import android.provider.Settings.Secure;
import android.widget.TextView;

public class StartFragment extends Fragment {

    private static final int REQUEST_CODE = 105;
    private FragmentStartBinding binding;
    public LayoutInflater public_inflater;
    public ViewGroup public_container;
    public String devId;
    public String devName;
    private Animation slideRight;
    private Animation slideLeft;
    public Integer access = 1;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        public_inflater = inflater;
        public_container = container;
        binding = FragmentStartBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        slideLeft = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_anim);
        slideRight = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_from_right);
        binding.buttonSelectMachine.clearAnimation();
        binding.buttonQrCode.clearAnimation();
        binding.buttonSelectMachine.startAnimation(slideLeft);
        binding.buttonQrCode.startAnimation(slideRight);

        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String activationToken = tokenHandler.readActivationTokenFromFile(basePath);
        System.out.println("TTTT");
        System.out.println(Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        ContentResolver context = getContext().getContentResolver();
        access = tokenHandler.checkActivationToken(activationToken, context);

        binding.buttonSelectMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (access == 0) {
                    NavHostFragment.findNavController(StartFragment.this)
                            .navigate(R.id.action_startFragment_to_deviceListFragment);
                } else if (access == 1) {
                    String no_access = "No valid token. Please contact with office@cm-solution.tech";
                    Snackbar mySnackbar = Snackbar.make(view, no_access, Snackbar.LENGTH_LONG);
                    final View snackView = mySnackbar.getView();
                    final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                }  else {
                    String no_access = "The token has expired. Please contact with office@cm-solution.tech";
                    Snackbar mySnackbar = Snackbar.make(view, no_access, Snackbar.LENGTH_LONG);
                    final View snackView = mySnackbar.getView();
                    final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                }
            }
        });

        binding.buttonQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (access == 0) {
                    if (Objects.equals(Build.MODEL, "T21G")) {
                        startActivityForResult(new Intent(view.getContext(), CameraRWActivity.class), REQUEST_CODE);
                    } else {
                        startActivityForResult(new Intent(view.getContext(), CameraActivity.class), REQUEST_CODE);
                    }
                } else if (access == 1) {
                    String no_access = "No valid token. Please contact with office@cm-solution.tech";
                    Snackbar mySnackbar = Snackbar.make(view, no_access, Snackbar.LENGTH_LONG);
                    final View snackView = mySnackbar.getView();
                    final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                } else {
                    String no_access = "The token has expired. Please contact with office@cm-solution.tech";
                    Snackbar mySnackbar = Snackbar.make(view, no_access, Snackbar.LENGTH_LONG);
                    final View snackView = mySnackbar.getView();
                    final TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackbar_textsize));
                    mySnackbar.setDuration(4000);
                    mySnackbar.show();
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    devId = data.getStringExtra(CameraRWActivity.QR_SCAN_RESULT);
                    devName = "Trzeba zrobi?? z jsona"; // data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                } else {
                    devId = data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                    devName = "Trzeba zrobi?? z jsona"; // data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                }

                Navigation.findNavController(binding.getRoot()).navigate(StartFragmentDirections.actionStartFragmentToDeviceMenuFragment(devId, devName));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        TokenHandler tokenHandler = new TokenHandler();
        File basePath = getContext().getExternalFilesDir("CMSData");
        String activationToken = tokenHandler.readActivationTokenFromFile(basePath);
        ContentResolver context = getContext().getContentResolver();
        access = tokenHandler.checkActivationToken(activationToken, context);
    }

}