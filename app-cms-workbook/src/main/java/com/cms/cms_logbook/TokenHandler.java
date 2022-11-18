package com.cms.cms_logbook;

import android.content.ContentResolver;
import android.provider.Settings;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.keys.AesKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenHandler {

    public void saveActivationTokenFile (String tokenText, String filePath){
        File file = new File(filePath);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(tokenText.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void saveAuthTokenFile (String tokenText, String filePath){
        File file = new File(filePath);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(tokenText.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }



    public Boolean checkActivationToken(String tokenText, ContentResolver context) {
        String key = "2ColzZecaMIPVjeq";
        try {

            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setKey(new AesKey(key.getBytes()));
            jwe.setCompactSerialization(tokenText);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jwe.getPayload());

            String android_id = Settings.Secure.getString(context,
                    Settings.Secure.ANDROID_ID);

            Date todayDate = new Date();
            Date tokenDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            todayDate = dateFormatter.parse(dateFormatter.format(new Date() ));
            tokenDate = dateFormatter.parse((String) json.get("due"));

            Boolean snOk = false;
            Boolean dueOk = false;

            if (json.get("sn").equals(android_id)){
                snOk = true;
            }
            if (todayDate.compareTo(tokenDate) == -1){
                dueOk = true;
            }
            if (snOk == true && dueOk == true){
                return Boolean.TRUE;
            };

            return Boolean.FALSE;


        }catch(Exception e){
            System.out.println(e);
        }
        return Boolean.FALSE;

    }

    public String readActivationTokenFromFile(File baseDir) {
        String path = baseDir + "/activationToken.json";
        File file = new File(path);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try{
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }
        String token = new String(bytes);
        return token;
    }
    public String readAuthTokenFromFile(File baseDir) {
        String path = baseDir + "/authToken.json";
        File file = new File(path);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try{
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }
        String token = new String(bytes);
        return token;
    }
}
