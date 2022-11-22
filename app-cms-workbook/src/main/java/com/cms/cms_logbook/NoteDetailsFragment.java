package com.cms.cms_logbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import db.DeviceModel;
import db.NoteModel;


public class NoteDetailsFragment extends Fragment {

    public String note_text;
    public String note_img;
    public Integer note_position;
    private String mdeviceId;

    public static String PhotoString = null;

    public Button UpdateButton;
    public Button DeleteButton;
    public ImageButton AddPhotoButton;
    public EditText GetValue;
    public ImageView PhotoImage;

    private static final int REQUEST_CODE = 105;

    public NoteDetailsFragment() {
        // Required empty public constructor
    }

    public static NoteDetailsFragment newInstance(String note_text, String note_img, Integer note_position) {
        NoteDetailsFragment fragment = new NoteDetailsFragment();
        Bundle args = new Bundle();
        args.putString("note_text", note_text);
        args.putString("note_img", note_img);
        args.putInt("note_position", note_position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note_text = getArguments().getString("note_text");
            note_img = getArguments().getString("note_img");
            note_position = getArguments().getInt("note_position");
            mdeviceId = getArguments().getString("deviceId");
            note_text = note_text.substring(note_text.indexOf(']')+1, note_text.length());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UpdateButton = (Button) view.findViewById(R.id.updateNoteButton);
        DeleteButton = (Button) view.findViewById(R.id.deleteNoteButton);
        GetValue = (EditText) view.findViewById(R.id.editText1);
        AddPhotoButton = (ImageButton) view.findViewById(R.id.addPhotoButton);
        PhotoImage = (ImageView) view.findViewById(R.id.camera_image_view);

        GetValue.setText(note_text);
        Bitmap PhotoBitMap = StringToBitMap(note_img);
        PhotoImage.setImageBitmap(PhotoBitMap);

        AddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(Build.MODEL, "T21G")){
                    startActivityForResult(new Intent(view.getContext(), AddPhotoRWActivity.class), REQUEST_CODE);
                } else {
                    startActivityForResult(new Intent(view.getContext(), AddPhotoActivity.class), REQUEST_CODE);
                }
            }
        });

        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNoteToDeviceFromQR(mdeviceId, note_position);
                String tmpTxt = GetValue.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());

                String fTxt = "[" + formatter.format(date) + "]" + tmpTxt;
                DeviceModel deviceScanned = putNoteToDeviceFromQR(mdeviceId, fTxt);
                NavHostFragment.findNavController(NoteDetailsFragment.this).popBackStack();
            }
        });
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceModel deviceScanned = deleteNoteToDeviceFromQR(mdeviceId, note_position);
                NavHostFragment.findNavController(NoteDetailsFragment.this).popBackStack();
            }
        });

    }

    private DeviceModel deleteNoteToDeviceFromQR(String qrId, int note_position) {
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
                    deviceScanned.deleteNote(note_position);
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
