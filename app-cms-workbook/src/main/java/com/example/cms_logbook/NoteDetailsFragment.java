package com.example.cms_logbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.fragment.app.Fragment;

public class NoteDetailsFragment extends Fragment {

    public String note_text;
    public String note_img;
    public Integer note_position;
    private String mdeviceId;

    public Button UpdateButton;
    public ImageButton AddPhotoButton;
    public EditText GetValue;
    public ImageView PhotoImage;

    public NoteDetailsFragment() {
        // Required empty public constructor
    }

    public static NoteDetailsFragment newInstance(String note_text, String note_img, Integer note_position) {
        NoteDetailsFragment fragment = new NoteDetailsFragment();
        Bundle args = new Bundle();
        args.putString("note_text", note_text);
        args.putString("note_img", note_img);
        args.putInt("note_position", note_position);
        System.out.println(note_text);
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
            System.out.println(mdeviceId);
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
        UpdateButton = (Button) view.findViewById(R.id.addNoteButton);
        GetValue = (EditText) view.findViewById(R.id.editText1);
        AddPhotoButton = (ImageButton) view.findViewById(R.id.addPhotoButton);
        PhotoImage = (ImageView) view.findViewById(R.id.camera_image_view);

        GetValue.setText(note_text);

        Bitmap PhotoBitMap = StringToBitMap(note_img);
        PhotoImage.setImageBitmap(PhotoBitMap);

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
