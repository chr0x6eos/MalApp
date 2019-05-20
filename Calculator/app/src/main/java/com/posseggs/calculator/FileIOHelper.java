package com.posseggs.calculator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileIOHelper {

    final static String fileName = "data.txt";
    //final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/calculator/data" ;
    final static String TAG = "FILEIO";

    public static String readFile(Context context) {

        String line = null;

        try
        {
            FileInputStream fileInputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory(), fileName));//path + "/" +  fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            line = bufferedReader.readLine();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        return line;
    }

    public static boolean saveToFile(String data) {
        try
        {
            /*
            File directory = new File(path);
            File file;
            if (!directory.exists())
            {
                if (!directory.mkdir())
                { //Create file without directory
                    file = new File(fileName);
                    file.createNewFile();
                }
                else
                { //Create file with directory
                    file = new File(path + "/" + fileName);
                    file.createNewFile();
                }
            }
            else
            { //Create file with directory
                file = new File(path + "/" + fileName);
                file.createNewFile();
            }
            */

            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file,false);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            return true;
        }
        catch(FileNotFoundException ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        return  false;
    }
}
