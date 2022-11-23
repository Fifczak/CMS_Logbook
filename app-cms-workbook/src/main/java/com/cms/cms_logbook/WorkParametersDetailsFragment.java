package com.cms.cms_logbook;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import db.DeviceModel;


public class WorkParametersDetailsFragment extends Fragment {

    private Spinner spinner;
    private Button updateButton;
    private Button deleteButton;
    private EditText GetValue;

    private static final String[] paths = {"RHs", "RPM", "PRESSURE", "TEMP"};

    private static final String deviceId = "deviceId";
    private static final String parameters = "parameter";
    private static final String position = "position";

    private String mdeviceId;
    private String mParameter;
    private Integer mPosition;


    public WorkParametersDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            mParameter = getArguments().getString(parameters);
            mPosition = getArguments().getInt(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_parameters_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner)view.findViewById(R.id.spinner2);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        GetValue = (EditText) view.findViewById(R.id.editText2);

        ArrayAdapter<String> WorkParametersAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,paths);

        WorkParametersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(WorkParametersAdapter);

        mParameter = mParameter.substring(mParameter.indexOf(']')+1, mParameter.length());
        String value = mParameter.substring(mParameter.indexOf(']')+1, mParameter.length());
        String task = mParameter.substring(1, mParameter.indexOf(']'));
        GetValue.setText(value);
        spinner.setSelection(WorkParametersAdapter.getPosition(task));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWorkParametersToDevice();
                if( TextUtils.isEmpty(GetValue.getText())){
                    Toast.makeText(getActivity(), "Value is required!", Toast.LENGTH_LONG).show();
                } else {
                    String tmpTxt = GetValue.getText().toString();
                    String tmpTxt2 = spinner.getSelectedItem().toString();

                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());

                    mdeviceId = getArguments().getString(deviceId);

                    String fTxt = "[" + formatter.format(date)  + "]" + "[" + tmpTxt2 + "]" + tmpTxt;

                    DeviceModel deviceScanned = putMeasurementToDeviceFromQR(mdeviceId, fTxt);
                }
                NavHostFragment.findNavController(WorkParametersDetailsFragment.this).popBackStack();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWorkParametersToDevice();
                NavHostFragment.findNavController(WorkParametersDetailsFragment.this).popBackStack();
            }
        });
    }

    private DeviceModel deleteWorkParametersToDevice() {
        DeviceModel deviceScanned;
        deviceScanned = new DeviceModel(null,null, null, null, null, null, null, null, null, null, null);

        try {
            String path = getContext().getExternalFilesDir("CMSData") + "/qrdata.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson g = new Gson();
            DeviceModel[] deviceArray = g.fromJson(bufferedReader, DeviceModel[].class);

            for (DeviceModel device : deviceArray) {
                String s = String.valueOf(device.getImId());
                if (mdeviceId.equals(s)) {
                    deviceScanned = device;
                    deviceScanned.deleteWorkParameters(mPosition);
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

    private DeviceModel putMeasurementToDeviceFromQR(String qrId, String deviceMeasurement) {
        DeviceModel deviceScanned;
        DeviceModel deviceScanned2;
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
                    deviceScanned.addMeasurement(deviceMeasurement);
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