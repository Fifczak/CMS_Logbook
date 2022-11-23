package com.cms.cms_logbook;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;


public class DeviceManualFragment extends Fragment {

    private static final String deviceId = "deviceId";

    private String mdeviceId;

    public DeviceManualFragment() {
        // Required empty public constructor
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
            String path = getContext().getExternalFilesDir("CMSData") + "/" + mdeviceId + ".pdf";
            try {
                pdfView.fromFile(new File(path)).load();
            }
            catch(Exception e){
                System.out.println(e);
            }

    }
}