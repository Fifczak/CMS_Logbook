package com.cms.cms_logbook;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cms.cms_logbook.databinding.FragmentDeviceListBinding;
import com.google.gson.Gson;

import db.DeviceListModel;
import db.DeviceModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.cms.cms_logbook.DevicesAdapter;

public class DeviceListFragment extends Fragment {

    private FragmentDeviceListBinding binding;

    private ListView listView;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentDeviceListBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        listView = binding.listview;
        ArrayList<DeviceListModel> arrayList = new ArrayList<>();

        try {
            String path = getContext().getExternalFilesDir("CMSData") + "/qrdata.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson g = new Gson();
            DeviceModel[] deviceArray = g.fromJson(bufferedReader, DeviceModel[].class);
            for (DeviceModel device : deviceArray) {
                String imId = String.valueOf(device.getImId());
                String deviceName = String.valueOf(device.getDeviceName());

                arrayList.add(new DeviceListModel(imId, deviceName));

            }
        }
        catch(Exception e){
            System.out.println(e);
        }


        ArrayAdapter arrayAdapter = new DevicesAdapter(view.getContext(), arrayList);
        listView.setAdapter(arrayAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}