package com.posseggs.malapp;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileIOHelper
{
    final static String TAG = "FileIO";

    public static String readFile(String fileName) throws FileNotFoundException {
        String line = null;
        BufferedReader reader = null;
        FileInputStream is = null;
        InputStreamReader irs = null;

        try
        {
            //Read from file
            is = new FileInputStream (new File(Environment.getExternalStorageDirectory(), fileName));
            irs = new InputStreamReader(is);
            reader = new BufferedReader(irs);
            line = reader.readLine(); //Read the only line from the file
        }
        catch(FileNotFoundException ex)
        {
            Log.d(TAG, ex.getMessage());
            throw ex;
        }
        catch(IOException ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        finally
        {
            try
            {
                //Close resources
                if (reader != null)
                    reader.close();
                if (is != null)
                    is.close();
                if (irs != null)
                    irs.close();
            }
            catch (Exception ex)
            {
                throw new IllegalArgumentException("Could not close resources!");
            }
        }
        return line;
    }

    public static boolean saveToFile(String fileName, String data, Boolean append)
    {
        FileOutputStream fos = null;

        try
        {
            //Create new file
            File f = new File(Environment.getExternalStorageDirectory(), fileName);
            //Create new file if it does not already exists
            if (!f.exists())
                f.createNewFile();

            //Write to file
            fos = new FileOutputStream(f,append);
            fos.write((data + System.getProperty("line.separator")).getBytes());

            //Return true if no error occurred
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
        finally
        {
            try
            {
                //Close resources
                if (fos != null)
                    fos.close();
            }
            catch (Exception ex)
            {
                Log.d(TAG,"Could not close resources: " + ex.getMessage());
                throw new IllegalArgumentException("Could not close resources!");
            }
        }
        //Return false if error occurred
        return  false;
    }
}