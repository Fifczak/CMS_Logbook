package com.cms.cms_logbook;


import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cms.cms_logbook.databinding.FragmentSettingsBinding;

import java.io.File;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Animation slideRight;
    private Animation slideLeft;
    public Boolean open = false;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        slideLeft = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_anim);
        slideRight = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_from_right);
        binding.buttonAuthentication.clearAnimation();
        binding.buttonAuthorization.clearAnimation();
        binding.buttonAuthentication.startAnimation(slideRight);
        binding.buttonAuthorization.startAnimation(slideLeft);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open = true;
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_SettingsFragment_to_settingsAuthorizationFragment);

            }
        });

        binding.buttonAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open = true;
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_SettingsFragment_to_settingsAuthenticationFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (open) {
            getActivity().recreate();
        }
    }

}