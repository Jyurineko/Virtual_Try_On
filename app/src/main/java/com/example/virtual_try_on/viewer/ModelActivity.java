package com.example.virtual_try_on.viewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.virtual_try_on.MainActivity;
import com.example.virtual_try_on.R;
import com.example.virtual_try_on.scene.SceneLoader;

import org.andresoviedo.util.android.ContentUtils;

import java.io.File;
import java.io.IOException;

public class ModelActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOAD_TEXTURE = 1000;

    /**
     * The file to load. Passed as input parameter
     */
    private Uri paramUri;

    private ModelSurfaceView gLView;

    private SceneLoader scene;

    private Handler handler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_model);

        //-------------------------------test-----------------------------------------
        ContentUtils.provideAssets(this);
//        Uri uri = Uri.parse("assets://assets/models/glasses.obj");
        File fileObj = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/Glasses");
        String filepath = "file://" + fileObj.getPath() + "/glasses.obj";
        Uri uri = Uri.parse(filepath);
        this.paramUri = uri;
        //----------------------------------------------------------------------------

        handler = new Handler(getMainLooper());

        // Create our 3D sceneario
        scene = new SceneLoader(this);
        scene.init();

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        try {
            gLView = new ModelSurfaceView(this);
            //TODO: 3D Showroom layout Param setting
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,800);
            addContentView(gLView,layoutParams);

            //TODO: add "return to MainPage"
            FrameLayout.LayoutParams layoutParams_btn_return_mainpage = new FrameLayout.LayoutParams(400,200);
            //bottom setting
            layoutParams_btn_return_mainpage.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
            Button btn_return_mainpage = new Button(this);
            //onclick function(test return to main activity
            btn_return_mainpage.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            });
            //text setting
            btn_return_mainpage.setGravity(Gravity.RIGHT);
            btn_return_mainpage.setGravity(Gravity.CENTER);
            btn_return_mainpage.setText("Return to MainPage");
            //add widget
            addContentView(btn_return_mainpage,layoutParams_btn_return_mainpage);

            //TODO: add "take picture"

            //Don't enable this, it will replace the ContentView by Model Layout file!!!!!!!!!!!!
            //setContentView(R.layout.activity_model);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading OpenGL view:\n" +e.getMessage(), Toast.LENGTH_LONG).show();
        }

        showSystemUI();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public Uri getParamUri() {
        return paramUri;
    }

    public SceneLoader getScene() {
        return scene;
    }

    public ModelSurfaceView getGLView() {
        return gLView;
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    private void showSystemUI() {
        handler.removeCallbacksAndMessages(null);
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_LOAD_TEXTURE:
                // The URI of the selected file
                final Uri uri = data.getData();
                if (uri != null) {
                    Log.i("ModelActivity", "Loading texture '" + uri + "'");
                    try {
                        ContentUtils.setThreadActivity(this);
                        scene.loadTexture(null, uri);
                    } catch (IOException ex) {
                        Log.e("ModelActivity", "Error loading texture: " + ex.getMessage(), ex);
                        Toast.makeText(this, "Error loading texture '" + uri + "'. " + ex
                                .getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        ContentUtils.setThreadActivity(null);
                    }
                }
        }
    }
}