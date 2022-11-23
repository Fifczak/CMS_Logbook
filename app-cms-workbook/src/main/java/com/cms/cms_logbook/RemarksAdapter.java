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

import db.RemarkModel;

public class RemarksAdapter extends ArrayAdapter<RemarkModel> {

    private TextView remarkTextView;

    public String remark_text;
    public String remark_date;
    public String device_id;

    public RemarksAdapter(Context context, ArrayList<RemarkModel> users, String deviceId) {
        super(context, 0, users);
        device_id = deviceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RemarkModel remark = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_remark, parent, false);
        }
        remarkTextView = (TextView) convertView.findViewById(R.id.remarkText);
        LinearLayout linearRemark = (LinearLayout) convertView.findViewById(R.id.linearRemark);

        remarkTextView.setText("[" + remark.remark_date  + "] " + remark.remark_text);

        linearRemark.setTag(position);
        linearRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                RemarkModel remark  = getItem(position);
                remark_text = remark.remark_text;
                remark_date = remark.remark_date;
                Navigation.findNavController(view).navigate(RemarksListFragmentDirections.actionRemarksListFragmentToRemarksFragment(remark_text, remark_date, device_id));
            }
        });


        return convertView;
    }

}