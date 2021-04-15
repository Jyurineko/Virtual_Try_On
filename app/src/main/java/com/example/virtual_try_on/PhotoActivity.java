package com.example.virtual_try_on;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.virtual_try_on.viewer.ModelActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final String FAILURE_CODE = "file download failure code";
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final String UPLOAD_URL = "http://jyurineko.ddns.net:2222/FileShare/Upload";

    private File imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

    }

//    check files exists module
    public void checkFileExists(View view){
        /*String fileUri = getResources().getString(R.string.requestSelfieObj);

        CheckTask task = new CheckTask();
        task.execute(fileUri);*/

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions,PERMISSION_STORAGE_CODE);
        }else{
            startDownload();
        }
    }

    private class CheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse==true)
            {
//                startDownload();
                Toast.makeText(PhotoActivity.this, "File has prepared, start launching", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(PhotoActivity.this, "File hasn't prepared yet, please wait a moment", Toast.LENGTH_SHORT).show();
            }
        }
    }
//---------------------------------------


    public void showObj(View view){

        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        startActivity(intent);
    }

    private void startDownload() {
        OkHttpClient client = new OkHttpClient();
        String objUrl = "http://jyurineko.ddns.net:2222/FileShare/ForClient/selfie.obj";
        String mtlUrl = "http://jyurineko.ddns.net:2222/FileShare/ForClient/selfie_m.mtl";
        String pngUrl = "http://jyurineko.ddns.net:2222/FileShare/ForClient/selfie_p.png";
        String savepath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        Request requestObj = new Request.Builder().url(objUrl).build();
        Request requestMtl = new Request.Builder().url(mtlUrl).build();
        Request requestPng = new Request.Builder().url(pngUrl).build();

        client.newCall(requestObj).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(FAILURE_CODE, "failure 2 Obj download");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for(int i = 0, size = responseHeaders.size(); i < size; i++){
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                File cacheDir = new File(savepath);
                String filename = "selfie.obj";
                File cacheFile = new File(cacheDir, filename);
                Log.i("Create New File","Create New File: " + cacheFile.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile, false);
                byte[] bytes = response.body().bytes();
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        });

        client.newCall(requestMtl).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(FAILURE_CODE, "failure 2 Mtl download");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for(int i = 0, size = responseHeaders.size(); i < size; i++){
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                File cacheDir = new File(savepath);
                String filename = "selfie_m.mtl";
                File cacheFile = new File(cacheDir, filename);
                Log.i("Create New File","Create New File: " + cacheFile.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile, false);
                byte[] bytes = response.body().bytes();
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        });

        client.newCall(requestPng).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(FAILURE_CODE, "failure 2 Png download");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for(int i = 0, size = responseHeaders.size(); i < size; i++){
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                File cacheDir = new File(savepath);
                //cacheDir.mkdirs();
                String filename = "selfie_p.png";
                File cacheFile = new File(cacheDir, filename);
                //cacheFile.createNewFile();
                Log.i("Create New File","Create New File: " + cacheFile.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile, false);
                byte[] bytes = response.body().bytes();
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        });
    }

//    take photo and save photo module
    public void takePhoto(View view) throws IOException{
        imageFile = createImageFile();
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.virtual_try_on.fileprovider", imageFile);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            if (imageFile != null) {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String filename = "selfie.jpeg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, filename);
        return image;
    }

/*    public void uploadImg(View view) throws IOException {
        UploadImg();
    }*/
    public void UploadImg(View view) {

        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+"selfie.jpeg";
        File image = new File(imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024,  true);
        try {
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", image.getName(), RequestBody.create(MEDIA_TYPE_JPEG, image))
                .build();

        Request request = new Request.Builder().url(UPLOAD_URL)
                .post(requestBody).build();

        OkHttpClient client = new OkHttpClient();
        client.callTimeoutMillis();
        client.readTimeoutMillis();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println(Objects.requireNonNull(response.body()).string());
                } else {
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownload();
                }else{
                    Toast.makeText(this,"Permission denied...!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}