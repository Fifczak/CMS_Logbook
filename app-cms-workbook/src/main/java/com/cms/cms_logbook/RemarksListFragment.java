package com.cms.cms_logbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cms.cms_logbook.databinding.FragmentRemarksListBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import db.DeviceModel;
import db.RemarkModel;

public class RemarksListFragment extends Fragment {

    private FragmentRemarksListBinding binding;
    private TextView deviceName;
    private ListView listview;
    private ArrayList<RemarkModel> ListElements = new ArrayList<RemarkModel>();

    private static final String deviceId = "deviceId";


    private String mdeviceId;


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
        return inflater.inflate(R.layout.fragment_remarks_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceName = (TextView) view.findViewById(R.id.deviceName);
        listview = (ListView) view.findViewById(R.id.listview);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId, this);
            ArrayList<RemarkModel> mdeviceRemarks = deviceScanned.getRemarks();
            deviceName.setText(deviceScanned.getDeviceName());
            ListElements = new ArrayList<RemarkModel>();
            for (RemarkModel remark : mdeviceRemarks) {
                ListElements.add(remark);
            }
        }
        ArrayAdapter arrayAdapter = new RemarksAdapter(view.getContext(), ListElements, mdeviceId);
        listview.setAdapter(arrayAdapter);
    }

    private DeviceModel getDeviceFromQR(String qrId, RemarksListFragment context) {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}