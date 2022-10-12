package com.example.cms_logbook;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddWorkParametersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWorkParametersFragment extends Fragment {
    ListView listview;
    Button Addbutton;
    EditText GetValue;
    Spinner GetTask;

    ArrayList<String> ListElements = new ArrayList<String>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String deviceId = "deviceId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner spinner;
    private static final String[] paths = {"RHs", "RPM", "PRESSURE", "TEMP"};

    private String mdeviceId;


    public AddWorkParametersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWorkParametersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddWorkParametersFragment newInstance(String param1, String param2) {
        AddWorkParametersFragment fragment = new AddWorkParametersFragment();
        Bundle args = new Bundle();
        args.putString(deviceId, param1);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_work_parameters, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner)view.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//


        listview = (ListView) view.findViewById(R.id.listView2);
        Addbutton = (Button) view.findViewById(R.id.button2);
        GetValue = (EditText) view.findViewById(R.id.editText2);
        GetTask = (Spinner) view.findViewById(R.id.spinner2);



        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId);
            ArrayList<String> mdeviceMeasurements = deviceScanned.getMeasurements();
            for (String measurement : mdeviceMeasurements) {
                System.out.println(measurement);
                ListElements.add(measurement);
            }
        }

        final List< String > ListElementsArrayList = new ArrayList< String >
                (ListElements);


        final ArrayAdapter< String > adapter2 = new ArrayAdapter < String >
                (this.getContext(), android.R.layout.simple_list_item_1,
                        ListElementsArrayList);

        listview.setAdapter(adapter2);

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpTxt = GetValue.getText().toString();
                String tmpTxt2 = GetTask.getSelectedItem().toString();

                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());

                mdeviceId = getArguments().getString(deviceId);

                String fTxt = "[" + date + "]" + "[" + tmpTxt2 + "]" + tmpTxt;
                System.out.println(formatter.format(date));

                DeviceModel deviceScanned = putMeasurementToDeviceFromQR(mdeviceId, fTxt);
                ListElementsArrayList.add(fTxt);
                adapter2.notifyDataSetChanged();
            }
        });

    }
    private DeviceModel getDeviceFromQR(String qrId) {
        DeviceModel deviceScanned;
        deviceScanned = new DeviceModel(null,null, null, null, null, null, null, null, null, null);
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

    private DeviceModel putMeasurementToDeviceFromQR(String qrId, String deviceMeasurement) {
        DeviceModel deviceScanned;
        DeviceModel deviceScanned2;

        deviceScanned = new DeviceModel(null,null, null, null, null, null,null, null, null, null);
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