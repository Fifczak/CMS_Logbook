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

import db.DeviceListModel;

public class DevicesAdapter extends ArrayAdapter<DeviceListModel> {

    String devId;
    String devName;

    public DevicesAdapter(Context context, ArrayList<DeviceListModel> users) {
        super(context, 0, users);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceListModel device = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        LinearLayout linearDevice = (LinearLayout) convertView.findViewById(R.id.linearDevice);
        deviceName.setText(device.name);

        linearDevice.setTag(position);
        linearDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                DeviceListModel device = getItem(position);
                devId = device.id.toString();
                devName =  device.name.toString();
                Navigation.findNavController(view).navigate(DeviceListFragmentDirections.actionDeviceListFragmentToDeviceMenuFragment(devId, devName));
            }
        });

        return convertView;
    }
}