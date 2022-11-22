package com.cms.cms_logbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

import com.cms.cms_logbook.databinding.FragmentDeviceMenuBinding;
import com.cms.cms_logbook.databinding.FragmentStartBinding;
import com.google.gson.Gson;

import db.DeviceModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceMenuFragment extends Fragment {

    private static final int REQUEST_CODE = 105;

    // TODO: Rename parameter arguments, choose names that match
    private static final String deviceId = "deviceId";
    private static final String deviceName = "deviceName";

    // TODO: Rename and change types of parameters
    private String mdeviceId;
    private String mdeviceName;
    private TextView textView;

    private Animation slideRight;
    private Animation slideLeft;


    public DeviceMenuFragment() {
        // Required empty public constructor
    }

    public LayoutInflater public_inflater;
    public ViewGroup public_container;
    private FragmentDeviceMenuBinding binding;

    public static DeviceMenuFragment newInstance(String param1, String param2) {
        DeviceMenuFragment fragment = new DeviceMenuFragment();
        Bundle args = new Bundle();
//        args.putString(deviceId, param1);
//        args.putString(deviceName, param2);
        args.putInt("deviceId", 5);
        fragment.setArguments(args);

        return fragment;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_device_menu, container, false);
//    }
@Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        public_inflater = inflater;
        public_container = container;
        binding = FragmentDeviceMenuBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        slideLeft = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_anim);
        slideRight = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_from_right);
        binding.repairHistoryButton.clearAnimation();
        binding.addWorkParameterButton.clearAnimation();
        binding.noteButton.clearAnimation();
        binding.remarksButton.clearAnimation();
        binding.DeviceManualButton.clearAnimation();
        binding.syncButton.clearAnimation();
        binding.repairHistoryButton.startAnimation(slideRight);
        binding.addWorkParameterButton.startAnimation(slideRight);
        binding.noteButton.startAnimation(slideRight);
        binding.remarksButton.startAnimation(slideLeft);
        binding.DeviceManualButton.startAnimation(slideLeft);
        binding.syncButton.startAnimation(slideLeft);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView) view.findViewById(R.id.deviceName);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId, this);
            mdeviceName = deviceScanned.getDeviceName();
            textView.setText(mdeviceName);
        }

        binding.remarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", mdeviceId);
                NavHostFragment.findNavController(DeviceMenuFragment.this)
                        .navigate(R.id.action_deviceMenuFragment_to_remarksListFragment, bundle);

            }
        });

        binding.addWorkParameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", mdeviceId);
                NavHostFragment.findNavController(DeviceMenuFragment.this)
                        .navigate(R.id.action_deviceMenuFragment_to_addWorkParametersFragment, bundle);

            }
        });

        binding.noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", mdeviceId);
                NavHostFragment.findNavController(DeviceMenuFragment.this)
                        .navigate(R.id.action_deviceMenuFragment_to_note, bundle);

            }
        });

        binding.repairHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", mdeviceId);
                NavHostFragment.findNavController(DeviceMenuFragment.this)
                        .navigate(R.id.action_deviceMenuFragment_to_repairHistoryFragment, bundle);

            }
        });

        binding.DeviceManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    Intent documentIntent = new Intent(view.getContext(), DocumentActivity.class);
                    documentIntent.putExtra("device_id", mdeviceId);
                    startActivityForResult(documentIntent, REQUEST_CODE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceId", mdeviceId);
                    NavHostFragment.findNavController(DeviceMenuFragment.this)
                            .navigate(R.id.action_deviceMenuFragment_to_deviceManualFragment, bundle);
                }
            }
        });
        binding.syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", mdeviceId);
                NavHostFragment.findNavController(DeviceMenuFragment.this)
                        .navigate(R.id.action_deviceMenuFragment_to_sync, bundle);

            }
        });
    }


    private DeviceModel getDeviceFromQR(String qrId, DeviceMenuFragment context) {

        DeviceModel deviceScanned;
        deviceScanned = new DeviceModel(null,null, null, null, null, null, null, null, null, null, null);

        try {
            String path = getContext().getExternalFilesDir("CMSData") + "/qrdata.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson g = new Gson();
            DeviceModel[] deviceArray = g.fromJson(bufferedReader, DeviceModel[].class);

            for (DeviceModel device : deviceArray) {
                String s = String.valueOf(device.getImId());
                if (qrId.equals(s)) {
                    deviceScanned = device;
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return deviceScanned;
    }





}