package com.posseggs.calculator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
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
        FileOutputStream fos = null;
        InputStream is = null;
        try
        {
            //Download app
            URL url = new URL(uri[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            connection.connect();

            //Write app to Downloads folder
            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "update.apk");

            if (outputFile.exists())
                outputFile.delete();

            fos = new FileOutputStream(outputFile);
            is = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1)
            {
                if (len != 0)
                    fos.write(buffer, 0, len);
            }

            fos.flush();
            fos.close();
            is.close();

            //Install app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "update.apk")), "application/vnd.android.package-archive");
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
        return null;
    }
}