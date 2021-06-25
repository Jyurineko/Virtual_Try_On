package com.example.virtual_try_on;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.andresoviedo.util.android.ContentUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class HatLoaderTask extends LoaderTask {
    /**
     * Build a new progress dialog for loading the data model asynchronously
     *
     * @param parent
     * @param uri      the URL pointing to the 3d model
     * @param callback
     */
    public HatLoaderTask(Activity parent, Uri uri, Callback callback) {
        super(parent, uri, callback);
    }

    @Override
    protected List<Object3DData> build() throws Exception {
        InputStream params0 = ContentUtils.getInputStream(uri);
        WavefrontLoader wfl = new WavefrontLoader("");

        // allocate memory
        publishProgress(0);
        wfl.analyzeModel(params0);
        params0.close();

        // Allocate memory
        publishProgress(1);
        wfl.allocateBuffers();
        wfl.reportOnModel();

        // create the 3D object
        Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
                wfl.getFaceMats(), wfl.getMaterials());
        data3D.setId(uri.getPath());
        data3D.setUri(uri);
        data3D.setLoader(wfl);
        data3D.setDrawMode(GLES20.GL_TRIANGLES);
        data3D.setDimensions(data3D.getLoader().getDimensions());

        return Collections.singletonList(data3D);
    }

    @Override
    protected void build(List<Object3DData> datas) throws Exception {
        InputStream stream = ContentUtils.getInputStream(uri);
        try {
            Object3DData data = datas.get(0);

            // parse model
            publishProgress(2);
            data.getLoader().loadModel(stream);
            stream.close();

            // scale object
            publishProgress(3);
            data.centerScale();
            data.setTranslation(new float[]{0, (float)0.41, (float)0});
            data.setScale(new float[]{(float)3.7, (float)3.3, (float)4.9});

            // draw triangles instead of points
            data.setDrawMode(GLES20.GL_TRIANGLES);

            // build 3D object buffers
            publishProgress(4);
            Object3DBuilder.generateArrays(data);
            publishProgress(5);

        } catch (Exception e) {
            Log.e("Object3DBuilder", e.getMessage(), e);
            throw e;
        }
    }
}
