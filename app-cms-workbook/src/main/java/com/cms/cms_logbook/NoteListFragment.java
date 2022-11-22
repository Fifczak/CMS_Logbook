package com.cms.cms_logbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import db.DeviceModel;
import db.NoteModel;

import com.cms.cms_logbook.AddPhotoActivity;
import com.cms.cms_logbook.AddPhotoRWActivity;
import com.cms.cms_logbook.NotesAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteListFragment extends Fragment {

    ListView listview;
    Button AddButton;
    ImageButton addPhotoButton;
    EditText GetValue;
    ImageView PhotoImage;
    ArrayList<NoteModel> ListElements = new ArrayList<NoteModel>();

    public static String PhotoString = null;

    private static final int REQUEST_CODE = 105;
    private static final String deviceId = "deviceId";
    private static final String ARG_PARAM2 = "param2";


    private String mdeviceId;

    public NoteListFragment() {
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
    public static NoteListFragment newInstance(String param1, String param2) {
        NoteListFragment fragment = new NoteListFragment();
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
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listview = (ListView) view.findViewById(R.id.listView1);
        AddButton = (Button) view.findViewById(R.id.addNoteButton);
        GetValue = (EditText) view.findViewById(R.id.editText1);
        addPhotoButton = (ImageButton) view.findViewById(R.id.addPhotoButton);
        PhotoImage = (ImageView) view.findViewById(R.id.camera_image_view);


        if (getArguments() != null) {
            mdeviceId = getArguments().getString(deviceId);
            DeviceModel deviceScanned = getDeviceFromQR(mdeviceId, this);
            ArrayList<NoteModel> mdeviceNotes = deviceScanned.getNotes();
            ListElements = new ArrayList<NoteModel>();
            for (NoteModel note : mdeviceNotes) {
                ListElements.add(note);
            }
        }
        ArrayAdapter arrayAdapter = new NotesAdapter(view.getContext(), ListElements, mdeviceId);
        listview.setAdapter(arrayAdapter);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( TextUtils.isEmpty(GetValue.getText())){
                    Toast.makeText(getActivity(), "Note is required!", Toast.LENGTH_LONG).show();
                } else{
                    String tmpTxt = GetValue.getText().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    Date date = new Date(System.currentTimeMillis());

                    mdeviceId = getArguments().getString(deviceId);
                    String fTxt = "[" + formatter.format(date) + "]" + tmpTxt;
                    DeviceModel deviceScanned = putNoteToDeviceFromQR(mdeviceId, fTxt);
                    ListElements.add(new NoteModel(fTxt, PhotoString));
                    listview.setAdapter(arrayAdapter);
                    GetValue.setText("");
                    PhotoImage.setImageResource(R.drawable.camera);
                }

            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    startActivityForResult(new Intent(view.getContext(), AddPhotoRWActivity.class), REQUEST_CODE);
                } else {
                    startActivityForResult(new Intent(view.getContext(), AddPhotoActivity.class), REQUEST_CODE);
                }
            }
        });

        }

    private DeviceModel getDeviceFromQR(String qrId, NoteListFragment context) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    PhotoString = data.getStringExtra(AddPhotoRWActivity.PhotoResult);
                    Bitmap PhotoBitMap = StringToBitMap(PhotoString);
                    PhotoImage.setImageBitmap(PhotoBitMap);
                } else {
                    PhotoString = data.getStringExtra(AddPhotoActivity.PhotoResult);
                    Bitmap PhotoBitMap = StringToBitMap(PhotoString);
                    PhotoImage.setImageBitmap(PhotoBitMap);
                }

            }
        }
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
                    deviceScanned.addNote( new NoteModel(deviceNote, PhotoString));
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

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}