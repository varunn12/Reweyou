package in.reweyou.reweyou.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Ayush.
 */
public class MyJSON {

    static String fileName = "Reweyou.json";

    public static void saveData(Context context, String mJsonResponse, int position) {
        try {
            Log.d("json", String.valueOf(position));
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + position + fileName, false);

            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static String getData(Context context, int position) {
        try {
            Log.d("jsonread", String.valueOf(position));

            File f = new File(context.getFilesDir().getPath() + "/" + position + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static void saveDataCategory(Context context, String response, int position, String category) {
        try {
            Log.d("json", String.valueOf(position));
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + position + category + fileName, false);

            file.write(response);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static String getDataCategory(Context context, int position, String category) {
        try {
            Log.d("jsonread", String.valueOf(position));

            File f = new File(context.getFilesDir().getPath() + "/" + position + category + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
}