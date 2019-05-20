package com.posseggs.calculator;

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

    final static String fileName = "calculation.txt";
    final static String TAG = "FileIO";

    public static String readFile() throws FileNotFoundException {
        String line = null;
        BufferedReader reader = null;
        FileInputStream inputStream = null;
        InputStreamReader inputReader = null;

        try
        {
            //Read from file
            inputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory(), fileName));
            inputReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputReader);
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
                if (inputStream != null)
                    inputStream.close();
                if (inputReader != null)
                    inputReader.close();
            }
            catch (Exception ex)
            {
                throw new IllegalArgumentException("Could not close resources!");
            }
        }
        return line;
    }

    public static boolean saveToFile(String data)
    {
        FileOutputStream fOut = null;

        try
        {
            //Create new file
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            file.createNewFile();

            //Write to file
            fOut = new FileOutputStream(file,false);
            fOut.write((data + System.getProperty("line.separator")).getBytes());

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
                if (fOut != null)
                    fOut.close();
            }
            catch (Exception ex)
            {
                throw new IllegalArgumentException("Could not close resources!");
            }
        }
        //Return false if error occurred
        return  false;
    }
}