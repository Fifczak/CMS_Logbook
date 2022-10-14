package com.example.cms_logbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cms_logbook.databinding.FragmentSettingsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

import db.DeviceModel;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Button checkTokenButton;
    private EditText tokenInput;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkTokenButton = (Button) view.findViewById(R.id.checkTokenButton);
        tokenInput = (EditText) view.findViewById(R.id.tokenInput);

        checkTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenText = tokenInput.getText().toString();
                System.out.println(tokenText);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}