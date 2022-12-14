package com.cms.cms_logbook;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import db.DeviceModel;
import db.DeviceOverhaulModel;
import db.NoteModel;


public class RepairHistoryListFragment extends Fragment {
    ListView listview;
    Button Addbutton;
    EditText GetRhsValue;
    EditText GetComment;
    Switch GetOvh;

    private static final String deviceId = "deviceId";

    private String mdeviceId;

    ArrayList<DeviceOverhaulModel> ListElements = new ArrayList<DeviceOverhaulModel>();


    public RepairHistoryListFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repair_history_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListElements = new ArrayList<DeviceOverhaulModel>();
        listview = (ListView) view.findViewById(R.id.repairHistoryList);
        Addbutton = (Button) view.findViewById(R.id.addButton);

        GetRhsValue = (EditText) view.findViewById(R.id.rhsInput);
        GetComment = (EditText) view.findViewById(R.id.commentInput);
        GetOvh = (Switch) view.findViewById(R.id.simpleSwitch);

        mdeviceId = getArguments().getString(deviceId);
        DeviceModel deviceScanned = getDeviceFromQR(mdeviceId);
        ArrayList<DeviceOverhaulModel> mdeviceOverhauls = deviceScanned.getDeviceOverhauls();
        for (DeviceOverhaulModel ovhModel : mdeviceOverhauls) {
            ListElements.add(ovhModel);
        }

        ArrayAdapter arrayAdapter = new RepairHistoryAdapter(view.getContext(), ListElements, mdeviceId);
        listview.setAdapter(arrayAdapter);

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(GetRhsValue.getText()) & TextUtils.isEmpty(GetComment.getText())) {
                    Toast.makeText(getActivity(), "RHS and Comment are required!", Toast.LENGTH_LONG).show();
                } else {
                    String device_maintanance_rhs = GetRhsValue.getText().toString();
                    int deviceMaintananceRhs = Integer.parseInt(device_maintanance_rhs);
                    int deviceFkey = Integer.parseInt(mdeviceId);

                    String device_maintanance_comment = GetComment.getText().toString();

                    String pattern = "yyyy-MM-dd HH:mm:ss"; //
                    DateFormat df = new SimpleDateFormat(pattern);
                    Date today = Calendar.getInstance().getTime();
                    String todayAsString = df.format(today);

                    Boolean if_overhaul = GetOvh.isChecked();
                    DeviceOverhaulModel ovhModel = new DeviceOverhaulModel(deviceMaintananceRhs, device_maintanance_comment, if_overhaul, deviceFkey, todayAsString);
                    mdeviceId = getArguments().getString(deviceId);
                    DeviceModel deviceScanned = putOvhToDeviceFromQR(mdeviceId, ovhModel);

                    ListElements.add(ovhModel);
                    listview.setAdapter(arrayAdapter);
                }
            }
        });

    }
    private DeviceModel getDeviceFromQR(String qrId) {
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

    private DeviceModel putOvhToDeviceFromQR(String qrId, DeviceOverhaulModel ovhModel) {
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
                    deviceScanned.addOvh(ovhModel);
                    Writer writer = new FileWriter(path);
                    g.toJson(deviceArray, writer);
                    writer.flush();
                    writer.close();
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return deviceScanned;
    }

    }