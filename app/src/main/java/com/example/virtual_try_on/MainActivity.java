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
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtual_try_on.viewer.ModelActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String guidance = "Vielen Dank, dass Sie diese Software verwenden. Wenn Sie bereit sind, " +
                "klicken Sie bitte auf die Schaltfläche unten, um zum Selfie-Bildschirm und dann zur " +
                "virtuellen Umkleidekabine weitergeleitet zu werden.\n\n" +
                "Thank you for using this software. When you are ready, click on the button below to " +
                "go to the selfie screen and then to the virtual changing room.\n\n" +
                "感谢您使用此软件。准备就绪后，请点击下面的按钮即可转到自拍界面，随后进入虚拟更衣室。\n";

        TextView textView = findViewById(R.id.textView2);
        textView.setText(guidance);
    }

    public void turn2PhotoPage(View view) {
        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
        startActivity(intent);
    }

}