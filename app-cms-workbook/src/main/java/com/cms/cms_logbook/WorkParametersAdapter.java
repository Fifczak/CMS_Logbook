package com.cms.cms_logbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import java.util.ArrayList;

import db.NoteModel;

public class WorkParametersAdapter extends ArrayAdapter<String> {

    public String device_id;

    public WorkParametersAdapter(Context context, ArrayList<String> users, String deviceId) {
        super(context, 0, users);
        device_id = deviceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String parameter = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_work_parameter, parent, false);
        }
        TextView parameterTextView = (TextView) convertView.findViewById(R.id.workParameter);
        parameterTextView.setText(parameter);
        LinearLayout linearParameters = (LinearLayout) convertView.findViewById(R.id.linearParameter);


        linearParameters.setTag(position);
        linearParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                String parameter = getItem(position);
                Navigation.findNavController(view).navigate(WorkParametersListFragmentDirections.actionAddWorkParametersFragmentToWorkParametersDetailsFragment(parameter, position, device_id));
            }
        });


        return convertView;
    }
}