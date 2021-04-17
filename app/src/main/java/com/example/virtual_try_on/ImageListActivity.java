package com.example.virtual_try_on;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtual_try_on.viewer.ModelActivity;

import org.andresoviedo.util.android.AssetUtils;
import org.andresoviedo.util.android.ContentUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageListActivity extends AppCompatActivity {


    private static final String SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf)";
    /**
     * Load file user data
     */
    private final Map<String, Object> loadModelParameters = new HashMap<>();

    private final String[] jewelryNames = {
            "glasses 1",
            "glasses 2",
            "glasses 3",
            "test merge"
    };

    private final Integer[] imageId = {
            R.drawable.glasses1,
            R.drawable.glasses2,
            R.drawable.glasses3,
            R.drawable.glasses1
    };


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        //-------------------------------------------------
        String obj1Path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie.obj";
        String mtl1Path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie_m.mtl";
//        String obj2Path = "assets:////assets/models/Glasses.obj";
//        String mtl2Path = "assets:////assets/models/Glasses.mtl";
        InputStream obj2 = null;
        InputStream mtl2 = null;
        try {
            obj2 = getResources().getAssets().open("models/Glasses.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mtl2 = getResources().getAssets().open("models/Glasses.mtl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream finalObj = obj2;
        InputStream finalMtl = mtl2;

        String tempObjPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        String tempMtlPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        //-------------------------------------------------

        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("List of Glasses");

        ListView listView = (ListView)findViewById(R.id.ImageListView);
        listView.addHeaderView(textView);

        ImageListAdapter imageListAdapter = new ImageListAdapter(this, jewelryNames,imageId);
        listView.setAdapter(imageListAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(getApplicationContext(), "You selected " + jewelryNames[position-1] + "as new Glasses", Toast.LENGTH_SHORT).show();
//            loadModelFromAssets();
            switch (jewelryNames[position-1]){
                case("glasses 1"):
                    ContentUtils.provideAssets(this);
                    launchModelRendererActivity(Uri.parse("assets://assets" + File.separator + "models" + File.separator + "Glasses.obj"));
                    break;
                case("glasses 2"):
                    ContentUtils.provideAssets(this);
                    launchModelRendererActivity(Uri.parse("assets://assets" + File.separator + "models" + File.separator + "Glasses2.obj"));
                    break;
                case("glasses 3"):
                    ContentUtils.provideAssets(this);
                    launchModelRendererActivity(Uri.parse("assets://assets" + File.separator + "models" + File.separator + "untitled.obj"));
                    break;
                case("test merge"):
                    try {
                        MergeModel.mergeObj(tempObjPath, obj1Path, finalObj);
                        MergeModel.mergeMtl(tempMtlPath, mtl1Path, finalMtl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        });
    }


    private void launchModelRendererActivity(Uri uri) {
        Log.i("Menu", "Launching renderer for '" + uri + "'");
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        intent.putExtra("uri", uri.toString());

        // content provider case
        if (!loadModelParameters.isEmpty()) {
            intent.putExtra("type", loadModelParameters.get("type").toString());
            loadModelParameters.clear();
        }

        startActivity(intent);
    }
}