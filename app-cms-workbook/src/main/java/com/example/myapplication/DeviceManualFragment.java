package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceManualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceManualFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String deviceId = "deviceId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mdeviceId;

    public DeviceManualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceManualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceManualFragment newInstance(String param1, String param2) {
        DeviceManualFragment fragment = new DeviceManualFragment();
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
        return inflater.inflate(R.layout.fragment_device_manual, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mdeviceId = getArguments().getString(deviceId);

        PDFView pdfView = view.findViewById(R.id.pdfView);
        String path = Environment.getExternalStorageDirectory() + "/CMSData/" + mdeviceId + ".pdf";
        try {
            pdfView.fromFile(new File(path)).load();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}