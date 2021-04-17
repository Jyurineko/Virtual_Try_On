package com.example.virtual_try_on;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class MergeModel {

    public static void mergeObj(String tempObjPath, String obj1Path, InputStream obj2Path) throws IOException {
        File mergeObjFile = new File(tempObjPath, "mergedObj.obj");

        File obj1 = new File(obj1Path);

        FileInputStream readObj1 = new FileInputStream(obj1);
//        FileInputStream readObj2 = new FileInputStream(obj2);
        FileOutputStream mergeObj = new FileOutputStream(mergeObjFile);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(readObj1));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(obj2Path));
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

    public static void mergeMtl(String tempMtlPath, String mtl1Path, InputStream mtl2Path) throws IOException {
        File mergeMtlFile = new File(tempMtlPath, "mergedMtl.mtl");

        File mtl1 = new File(mtl1Path);

        FileInputStream readMtl1 = new FileInputStream(mtl1);
//        FileInputStream readMtl2 = new FileInputStream(mtl2);
        FileOutputStream mergeMtl = new FileOutputStream(mergeMtlFile);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(readMtl1));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(mtl2Path));
        BufferedWriter writer2mergeMtl = new BufferedWriter(new OutputStreamWriter(mergeMtl));

        String temp;
        while((temp = reader1.readLine()) != null){
            writer2mergeMtl.write(Arrays.toString(temp.split("\n")));
        }
        reader1.close();
        writer2mergeMtl.close();

        while ((temp = reader2.readLine()) != null){
            writer2mergeMtl.write(Arrays.toString(temp.split("\n")));
        }
        reader2.close();
        writer2mergeMtl.close();
    }
}
