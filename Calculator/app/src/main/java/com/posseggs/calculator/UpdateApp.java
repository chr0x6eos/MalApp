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
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

public class UpdateApp extends AsyncTask<String,Void,Void> {
    private Context context;
    private static final String TAG = "UpdateApp";


    public void setContext(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... uri) {

        //Download and install the application
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
                    Log.d(TAG, "Downloading: " + (int) ((total * 100) / lengthOfFile) + "%");
                    fos.write(data, 0, len);
                }
            }

            fos.flush();
            fos.close();
            is.close();

            Log.d(TAG,"Downloaded. Installing now");

            //Set app path
            File app = new File(path,filename);
            Uri fileUri = Uri.fromFile(app);
            if (Build.VERSION.SDK_INT >= 24) //Newer skd needs other uri
            {
                fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", app);
            }

            //Start installation intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");

            //Set flags for intent
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Prompt user to install app
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