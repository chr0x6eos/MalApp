package com.posseggs.calculator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class UpdateApp extends AsyncTask<String,Void,Void> {
    private Context context;
    private static final String TAG = "UpdateApp";


    public void setContext(Context contextf)
    {
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... uri) {

        downloadAndInstall(uri[0]);
        return null;
    }

    private void downloadAndInstall(String uri)
    {
        FileOutputStream fos = null;
        InputStream is = null;
        try
        {
            //Download app
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Log.d(TAG,"Connecting");
            int lengthOfFile = connection.getContentLength();
            connection.connect();

            //Write app to Downloads folder
            String path = Environment.getExternalStorageDirectory() + "/Download";
            String filename = "update.apk";
            //File file = new File(PATH,"update.apk");
            //file.mkdirs();
            File outputFile = new File(path, filename);

            if (outputFile.exists())
                outputFile.delete();

            fos = new FileOutputStream(outputFile,false);
            //Open input stream to read file with 8k buffer
            is = new BufferedInputStream(url.openStream(), 8192);

            byte data[] = new byte[1024];
            int len = 0;
            long total = 0;
            while ((len = is.read(data)) != -1)
            {
                if (len != 0)
                {
                    total += len;
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));
                    fos.write(data, 0, len);
                }
            }

            fos.flush();
            fos.close();
            is.close();

            Log.d(TAG,"Downloaded. Installing now");
            //Install app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path,filename)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Update error! " + ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                //Close the resources
                if (fos != null)
                    fos.close();
                if (is != null)
                    is.close();
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Resource error! " + ex.getMessage());
            }
        }
    }
}