package com.posseggs.calculator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

public class UpdateApp extends AsyncTask<String,Integer,Void> {

    private MainActivity main;
    private static final String TAG = "UpdateApp";
    private ProgressDialog progressDialog;


    public void setMain(MainActivity main)
    {
        this.main = main;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Create a progress dialog for the user
        progressDialog = new ProgressDialog(main);
        progressDialog.setTitle("Update in progress...");
        progressDialog.setMessage("Downloading...");
        progressDialog.setIndeterminate(false); //Defines that the progress is measurable
        progressDialog.setMax(100); //Set maximum value
        progressDialog.setCancelable(false); //Can not be canceled
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //Set style to be a horizontal progress bar
        progressDialog.show(); //Show progressDialog
    }

    @Override
    protected Void doInBackground(String... uri) {

        //Download and install the application
        downloadAndInstall(uri[0]);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]); //Update progress
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss(); //When done downloading close dialog
        main.finish(); //Close app
    }

    private void downloadAndInstall(String uri)
    {
        //Init resources
        FileOutputStream fos = null;
        InputStream is = null;
        HttpURLConnection connection = null;

        try
        {
            //Download application file from server
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Log.d(TAG,"Connecting");
            int lengthOfFile = connection.getContentLength(); //Get length of file to track progress
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
            int len; //len = 0
            long total = 0;
            while ((len = is.read(data)) != -1) //Read from DownloadStream and save to file
            {
                if (len != 0)
                {
                    total += len;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Downloading: " + (int) ((total * 100) / lengthOfFile) + "%");
                    fos.write(data, 0, len);
                }
            }

            fos.flush();
            fos.close();
            is.close();

            Log.d(TAG,"Downloaded. Installing now...");

            //Set app path
            File app = new File(path,filename);
            Uri fileUri = Uri.fromFile(app);
            if (Build.VERSION.SDK_INT >= 24) //Newer skd needs other uri
            {
                fileUri = FileProvider.getUriForFile(main, main.getPackageName() + ".provider", app);
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
            main.startActivity(intent);
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
                if (connection != null)
                    connection.disconnect();
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Resource error! " + ex.getMessage());
            }
        }
    }
}