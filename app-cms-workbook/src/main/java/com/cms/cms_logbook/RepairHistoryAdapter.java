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

import db.DeviceOverhaulModel;

public class RepairHistoryAdapter extends ArrayAdapter<DeviceOverhaulModel> {

    public String device_id;

    public RepairHistoryAdapter(Context context, ArrayList<DeviceOverhaulModel> users, String deviceId) {
        super(context, 0, users);
        device_id = deviceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceOverhaulModel deviceOverhaul = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_repair_history, parent, false);
        }
        TextView repairHistoryTextView = (TextView) convertView.findViewById(R.id.RepairHistory);
        LinearLayout linearRepairHistory = (LinearLayout) convertView.findViewById(R.id.linearRepairHistory);
        repairHistoryTextView.setText(deviceOverhaul.toString());

        linearRepairHistory.setTag(position);
        linearRepairHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                DeviceOverhaulModel deviceOverhaul = getItem(position);
                Navigation.findNavController(view).navigate(RepairHistoryListFragmentDirections.actionRepairHistoryFragmentToRepairHistoryDetailsFragment(
                        deviceOverhaul.device_maintanance_rhs, deviceOverhaul.device_maintanance_comment, deviceOverhaul.if_overhaul,
                        device_id, position));
            }
        });


        return convertView;
    }
}