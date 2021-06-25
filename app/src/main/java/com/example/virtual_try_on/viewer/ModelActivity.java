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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.virtual_try_on.ImageListActivity;
import com.example.virtual_try_on.MainActivity;
import com.example.virtual_try_on.PhotoActivity;
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
    private Uri paramUri1;
    private Uri paramUri2;
    private Uri paramType;

    private ModelSurfaceView gLView;

    private SceneLoader scene;

    private Handler handler;


    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_model);

        //-------------------------------test-----------------------------------------
//        ContentUtils.provideAssets(this);
//        Uri uri = Uri.parse("assets://assets/models/selfie.obj");
        String fileObj = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie.obj";
        String fileMtl = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie_m.mtl";
        String filePng = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie_p.png";
        File obj = new File(fileObj);
        File mtl = new File(fileMtl);
        File png = new File(filePng);
        this.paramUri1 = Uri.fromFile(obj);
        ContentUtils.addUri("selfie_m.mtl",Uri.fromFile(mtl));
        ContentUtils.addUri("selfie_p.png",Uri.fromFile(png));
        //----------------------------------------------------------------------------

        Bundle b = getIntent().getExtras();
        if (b.getString("uri") != null && b.getString("accessoryType") != null){
            this.paramUri2 = Uri.parse(b.getString("uri"));
            this.paramType = Uri.parse(b.getString("accessoryType"));
        }

/*        ContentUtils.provideAssets(this);
        Uri uri1 = Uri.parse("assets://assets/models/Glasses.obj");
        Uri uri2 = Uri.parse("assets://assets/models/Glasses2.obj");

        this.paramUri1 = uri1;
        this.paramUri2 = uri2;*/

//        -----------------------------------------------------------------------------

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
            //create return mainpage button
            Button btn_return_mainpage = new Button(this);
            //params of btn
            FrameLayout.LayoutParams layoutParams_btn_return_mainpage = new FrameLayout.LayoutParams(400,200);
            //bottom setting
            layoutParams_btn_return_mainpage.gravity = Gravity.CENTER | Gravity.LEFT;
//            layoutParams_btn_return_mainpage.setMargins(100,750,0,0);
            //onclick function(test return to main activity
            btn_return_mainpage.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(intent);
                finish();
            });
            //text setting
            btn_return_mainpage.setGravity(Gravity.RIGHT);
            btn_return_mainpage.setGravity(Gravity.CENTER);
            btn_return_mainpage.setText("Return to take new Photo");
            //add btn widget
            addContentView(btn_return_mainpage,layoutParams_btn_return_mainpage);

            //TODO: add "return to choose new glasses"
            Button btn_return_choose = new Button(this);
            FrameLayout.LayoutParams layoutParams_btn_return_choose = new FrameLayout.LayoutParams(400, 200);
            layoutParams_btn_return_choose.gravity = Gravity.CENTER | Gravity.RIGHT;
            btn_return_choose.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ImageListActivity.class);
                startActivity(intent);
                finish();
            });
            btn_return_choose.setGravity(Gravity.CENTER);
            btn_return_choose.setText("Return to choose new Glasses");
            addContentView(btn_return_choose,layoutParams_btn_return_choose);


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

    public Uri getParamUri1() {
        return paramUri1;
    }
    public Uri getParamUri2() { return paramUri2; }
    public Uri getParamType() { return paramType; }

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