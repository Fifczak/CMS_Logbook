package com.cms.cms_logbook;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cms.cms_logbook.databinding.FragmentRemarksBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;

import db.DeviceModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RemarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemarksFragment extends Fragment {

    private static final String deviceId = "deviceId";
    private static final String deviceName = "deviceName";

    private String mdeviceId;

    private TextView nameTextView;
    private TextView classTextView;
    private TextView remarkTextView;
    private TextView dateView;


    public RemarksFragment() {
        // Required empty public constructor
    }

    public LayoutInflater public_inflater;
    public ViewGroup public_container;
    private FragmentRemarksBinding binding;


    public static RemarksFragment newInstance(String param1, String param2) {
        RemarksFragment fragment = new RemarksFragment();
        Bundle args = new Bundle();
        args.putString(deviceId, param1);
        args.putString(deviceName, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_remarks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView = (TextView) view.findViewById(R.id.deviceName);
        classTextView = (TextView) view.findViewById(R.id.className);
        remarkTextView = (TextView) view.findViewById(R.id.remarkText);
        dateView = (TextView) view.findViewById(R.id.dateRemark);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId, this);
            String mdeviceName = deviceScanned.getDeviceName();
            String mdeviceClass = deviceScanned.getIsoClass();
            String mdeviceRemark = deviceScanned.getRemark();
            String mdeviceDate = deviceScanned.getLastDate();

            nameTextView.setText(mdeviceName);
            classTextView.setText(mdeviceClass);
            remarkTextView.setText(mdeviceRemark);
            dateView.setText(mdeviceDate);

        }


    }
    private DeviceModel getDeviceFromQR(String qrId, RemarksFragment context) {

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