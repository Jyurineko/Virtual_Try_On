package com.example.virtual_try_on;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MergeModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private final String obj1Path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie.obj";
    private final String mtl1Path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "selfie_m.mtl";
    private final String obj2Path = "assets://assets/models/Glasses.obj";
    private final String mtl2Path = "assets://assets/models/Glasses.mtl";

    private final String tempObjPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
    private final String tempMtlPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

    public void mergeObj() throws IOException {
        File mergeObjFile = new File(tempObjPath, "mergedObj.obj");

        File obj1 = new File(obj1Path);
        File obj2 = new File(obj2Path);

        FileInputStream readObj1 = new FileInputStream(obj1);
        FileInputStream readObj2 = new FileInputStream(obj2);
        FileOutputStream mergeObj = new FileOutputStream(mergeObjFile);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(readObj1));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(readObj2));
        BufferedWriter writer2mergeObj = new BufferedWriter(new OutputStreamWriter(mergeObj));

        String temp;
        while ((temp = reader1.readLine()) != null){
            writer2mergeObj.write(temp);
        }
        reader1.close();
        writer2mergeObj.close();

        while((temp = reader2.readLine()) != null){
            writer2mergeObj.write(temp);
        }
        reader2.close();
        writer2mergeObj.close();
    }

    public void mergeMtl() throws IOException {
        File mergeMtlFile = new File(tempMtlPath, "mergedMtl.mtl");

        File mtl1 = new File(mtl1Path);
        File mtl2 = new File(mtl2Path);

        FileInputStream readMtl1 = new FileInputStream(mtl1);
        FileInputStream readMtl2 = new FileInputStream(mtl2);
        FileOutputStream mergeMtl = new FileOutputStream(mergeMtlFile);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(readMtl1));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(readMtl2));
        BufferedWriter writer2mergeMtl = new BufferedWriter(new OutputStreamWriter(mergeMtl));

        String temp;
        while((temp = reader1.readLine()) != null){
            writer2mergeMtl.write(temp);
        }
        reader1.close();
        writer2mergeMtl.close();

        while ((temp = reader2.readLine()) != null){
            writer2mergeMtl.write(temp);
        }
        reader2.close();
        writer2mergeMtl.close();
    }
}