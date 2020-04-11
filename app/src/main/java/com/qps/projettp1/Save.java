package com.qps.projettp1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.core.app.ActivityCompat;

import static androidx.core.app.ActivityCompat.requestPermissions;
public class Save extends Activity {
    private Context context;
    private String nameOfFoledr = "/Mehdi_Image";
    private String NameOfFile =  "MehdiImageSave";

    public void SaveImage(Context context , Bitmap bitmap){


        this.context = context;
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(file_path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory()+File.separator,NameOfFile+currentDateAndTime+".jpg");
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(file);
            System.out.println(outputStream);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

            outputStream.flush();
            MakeSureFileWasCreatedThenMakeAvailable(file);
            AbleToSave();
            
        } catch (FileNotFoundException e) {
            UnableToSave();
        } catch (IOException e) {
            UnableToSave();
        }
    }


    private void UnableToSave() {
        Toast.makeText(context,"Vous devez activer le stockage dans Settings ",Toast.LENGTH_LONG).show();
    }

    private String getCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatteDate = simpleDateFormat.format(calendar.getTime());
        return formatteDate;
    }

    private void AbleToSave() {
        Toast.makeText(context,"Picture saved",Toast.LENGTH_LONG).show();
    }

    private void MakeSureFileWasCreatedThenMakeAvailable(File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage","Scanned"+ path + ":");
                        Log.e("ExternalStorage","-> uri="+ uri);
                    }
                });
    }


}
