package com.example.virtual_try_on;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.virtual_try_on.viewer.ModelActivity;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------------------------------------------------------------------------
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        //----------------------------------------------------------------------------
        setContentView(R.layout.activity_main);
    }

    public void showObj(View view) {
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        startActivity(intent);
    }

    public void downloadObj(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSION_STORAGE_CODE);
            }else{
                //permission already granted, perform download
                startdownload();
            }
        }else{
            startdownload();
        }
    }

    private void startdownload() {
        /*
        RDJ file URL
        Glasses file URL
         */
        String urlObj = getResources().getString(R.string.linkObj);
        String urlMtl = getResources().getString(R.string.linkMtl);
        String urlPng = getResources().getString(R.string.linkPng);

        String urlglassesObj = getResources().getString(R.string.linkglassesObj);
        String urlglassesMtl = getResources().getString(R.string.linkglassesMtl);
        String urlglassesPng = getResources().getString(R.string.linkglassesPng);
        // make download request of file
        DownloadManager.Request requestObj = new DownloadManager.Request(Uri.parse(urlglassesObj));
        DownloadManager.Request requestMtl = new DownloadManager.Request(Uri.parse(urlglassesMtl));
        DownloadManager.Request requestPng = new DownloadManager.Request(Uri.parse(urlglassesPng));
        // set Network type
        requestObj.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        requestMtl.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        requestPng.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        /*
        requestObj.setTitle("Download");
        requestObj.setTitle("Downloading file...");
*/
        // Visibility of file download progress
        requestObj.allowScanningByMediaScanner();
        requestObj.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        // set file save path
//        requestObj.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Rdj/RDJ.obj");
        requestObj.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS,"Glasses/glasses.obj");
        requestMtl.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS,"Glasses/glasses_m.mtl");
        requestPng.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS,"Glasses/glasses_p.png");
        // download file service
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        // start download queue
        manager.enqueue(requestObj);
        manager.enqueue(requestMtl);
        manager.enqueue(requestPng);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startdownload();
                }else{
                    Toast.makeText(this,"Permission denied...!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}