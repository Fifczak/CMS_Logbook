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
 * Use the {@link Note#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Note extends Fragment {
    ListView listview;
    Button Addbutton;
    EditText GetValue;
    ArrayList<String> ListElements = new ArrayList<String>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String deviceId = "deviceId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String mdeviceId;

    public Note() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Note.
     */
    // TODO: Rename and change types and number of parameters
    public static Note newInstance(String param1, String param2) {
        Note fragment = new Note();
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
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listview = (ListView) view.findViewById(R.id.listView1);
        Addbutton = (Button) view.findViewById(R.id.addNoteButton);
        GetValue = (EditText) view.findViewById(R.id.editText1);



        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId, this);
            ArrayList<String> mdeviceNotes = deviceScanned.getNotes();
            for (String note : mdeviceNotes) {
                ListElements.add(note);
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
                String tmpTxt = GetValue.getText().toString();
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                System.out.println(formatter.format(date));

                mdeviceId = getArguments().getString(deviceId);

                String fTxt = "[" + date + "]" + tmpTxt;
                DeviceModel deviceScanned = putNoteToDeviceFromQR(mdeviceId, fTxt);
                ListElementsArrayList.add(fTxt);
                adapter.notifyDataSetChanged();
            }
        });




        }

    private DeviceModel getDeviceFromQR(String qrId, Note context) {
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

    private DeviceModel putNoteToDeviceFromQR(String qrId, String deviceNote) {
        DeviceModel deviceScanned;
        DeviceModel deviceScanned2;

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
                    deviceScanned.addNote(deviceNote);
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