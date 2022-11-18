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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepairHistoryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepairHistoryListFragment extends Fragment {
    ListView listview;
    Button Addbutton;
    EditText GetRhsValue;
    EditText GetComment;
    Switch GetOvh;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String deviceId = "deviceId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mdeviceId;

    ArrayList<String> ListElements = new ArrayList<String>();


    public RepairHistoryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepairHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepairHistoryListFragment newInstance(String param1, String param2) {
        RepairHistoryListFragment fragment = new RepairHistoryListFragment();
        Bundle args = new Bundle();
        args.putString(deviceId, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(deviceId);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        listview = (ListView) view.findViewById(R.id.repairHistoryList);
        Addbutton = (Button) view.findViewById(R.id.addButton);

        GetRhsValue = (EditText) view.findViewById(R.id.rhsInput);
        GetComment = (EditText) view.findViewById(R.id.commentInput);
        GetOvh = (Switch) view.findViewById(R.id.simpleSwitch);

        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId);
            ArrayList<DeviceOverhaulModel> mdeviceOverhauls = deviceScanned.getDeviceOverhauls();
            for (DeviceOverhaulModel ovhModel : mdeviceOverhauls) {
                ListElements.add(ovhModel.toString());


            }
        }
        final List< String > ListElementsArrayList = new ArrayList< String >
                (ListElements);


        final ArrayAdapter< String > adapter = new ArrayAdapter < String >
                (this.getContext(), android.R.layout.simple_list_item_1,
                        ListElementsArrayList);

        listview.setAdapter(adapter);

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( TextUtils.isEmpty(GetRhsValue.getText()) & TextUtils.isEmpty(GetComment.getText())){
                    Toast.makeText(getActivity(), "RHS and Comment are required!", Toast.LENGTH_LONG).show();
                } else {
                    String device_maintanance_rhs = GetRhsValue.getText().toString();
                    int deviceMaintananceRhs=Integer.parseInt(device_maintanance_rhs);
                    int deviceFkey=Integer.parseInt(mdeviceId);

                    String device_maintanance_comment = GetComment.getText().toString();

                    String pattern = "yyyy-MM-dd HH:mm:ss"; //
                    DateFormat df = new SimpleDateFormat(pattern);
                    Date today = Calendar.getInstance().getTime();
                    String todayAsString = df.format(today);


                    Boolean if_overhaul = GetOvh.isChecked();
                    DeviceOverhaulModel ovhModel = new DeviceOverhaulModel(deviceMaintananceRhs, device_maintanance_comment, if_overhaul, deviceFkey, todayAsString);

                    mdeviceId = getArguments().getString(deviceId);

                    DeviceModel deviceScanned = putOvhToDeviceFromQR(mdeviceId, ovhModel);
                    String fTxt = ovhModel.toString();
                    ListElementsArrayList.add(fTxt);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
    private DeviceModel getDeviceFromQR(String qrId) {
        DeviceModel deviceScanned;
        deviceScanned = new DeviceModel(null,null,null, null, null, null, null, null, null, null);
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

        deviceScanned = new DeviceModel(null,null,null, null, null, null, null, null, null, null);
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