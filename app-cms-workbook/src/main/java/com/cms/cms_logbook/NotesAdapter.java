package com.cms.cms_logbook;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import java.util.ArrayList;

import db.DeviceListModel;
import db.NoteModel;

public class NotesAdapter extends ArrayAdapter<NoteModel> {

    public String note_text;
    public String note_img;
    public String device_id;

    public NotesAdapter(Context context, ArrayList<NoteModel> users, String deviceId) {
        super(context, 0, users);
        device_id = deviceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        NoteModel note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);
        }
        TextView noteText = (TextView) convertView.findViewById(R.id.note_text);
        ImageView noteImg = (ImageView) convertView.findViewById(R.id.note_img);
        LinearLayout linearNote = (LinearLayout) convertView.findViewById(R.id.linearDevice);
        noteText.setText(note.note_text);
        if (note.note_img != null){
            Bitmap PhotoBitMap = StringToBitMap(note.note_img);
            noteImg.setImageBitmap(PhotoBitMap);
        } else {
            noteImg.setVisibility(View.INVISIBLE);
        }

        linearNote.setTag(position);
        linearNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                NoteModel note = getItem(position);
                note_text = note.note_text;
                note_img = note.note_img;
                Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteToNoteDetailsFragment(note_text, note_img, position, device_id));
            }
        });


        return convertView;
    }

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