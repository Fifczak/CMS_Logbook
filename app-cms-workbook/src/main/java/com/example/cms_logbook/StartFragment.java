package com.example.cms_logbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.example.cms_logbook.databinding.FragmentStartBinding;

import java.util.Objects;

public class StartFragment extends Fragment {

    private static final int REQUEST_CODE = 105;
    private FragmentStartBinding binding;
    public LayoutInflater public_inflater;
    public ViewGroup public_container;
    public String devId;
    public String devName;
    private Animation slideRight;
    private Animation slideLeft;


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
        binding.buttonSelectMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartFragment.this)
                        .navigate(R.id.action_startFragment_to_deviceListFragment);
            }
        });
        binding.buttonQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    startActivityForResult(new Intent(view.getContext(), CameraRWActivity.class), REQUEST_CODE);
                } else {
                    startActivityForResult(new Intent(view.getContext(), CameraActivity.class), REQUEST_CODE);
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
                    devName = "Trzeba zrobić z jsona"; // data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                } else {
                    devId = data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                    devName = "Trzeba zrobić z jsona"; // data.getStringExtra(CameraActivity.QR_SCAN_RESULT);
                }


//                Navigation.findNavController(binding.getRoot()).navigate(StartFragmentDirections.actionStartFragmentToDeviceMenuFragment(devId, devName));
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}