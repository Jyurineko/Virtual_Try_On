package com.example.virtual_try_on.scene;

import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.virtual_try_on.viewer.ModelActivity;
import com.example.virtual_try_on.viewer.ModelRenderer;

import org.andresoviedo.android_3d_model_engine.animation.Animator;
import org.andresoviedo.android_3d_model_engine.collision.CollisionDetection;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoaderTask;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SceneLoader implements LoaderTask.Callback {
    /**
     * Default model color: yellow
     */
    private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
    /**
     * Parent component
     */
    protected final ModelActivity modelActivity;
    /**
     * List of data objects containing info for building the opengl objects
     */
    private List<Object3DData> objects = new ArrayList<>();
    /**
     * Show axis or not
     */
    private boolean drawAxis = false;
    /**
     * Point of view camera
     */
    private Camera camera;
    /**
     * Enable or disable blending (transparency)
     */
    private boolean isBlendingEnabled = true;
    /**
     * Whether to draw objects as wireframes
     */
    private boolean drawWireframe = false;
    /**
     * Whether to draw using points
     */
    private boolean drawingPoints = false;
    /**
     * Whether to draw face normals. Normally used to debug models
     */
    private boolean drawNormals = false;
    /**
     * Whether to draw using textures
     */
    private boolean drawTextures = true;
    /**
     * Whether to draw using colors or use default white color
     */
    private boolean drawColors = true;
    /**
     * Light toggle feature: we have 3 states: no light, light, light + rotation
     */
    private boolean rotatingLight = true;
    /**
     * Light toggle feature: whether to draw using lights
     */
    private boolean drawLighting = true;
    /**
     * Animate model (dae only) or not
     */
    private boolean doAnimation = true;
    /**
     * show bind pose only
     */
    private boolean showBindPose = false;
    /**
     * Toggle 3d
     */
    private boolean isStereoscopic = false;
    /**
     * Toggle 3d anaglyph (red, blue glasses)
     */
    private boolean isAnaglyph = false;
    /**
     * Toggle 3d VR glasses
     */
    private boolean isVRGlasses = false;
    /**
     * Object selected by the user
     */
    private Object3DData selectedObject = null;
    /**
     * Initial light position
     */
    private final float[] lightPosition = new float[]{0, 0, 10, 1};
    /**
     * Light bulb 3d data
     */
    private final Object3DData lightPoint = Object3DBuilder.buildPoint(lightPosition).setId("light");
    /**
     * Animator
     */
    private Animator animator = new Animator();
    /**
     * Did the user touched the model for the first time?
     */
    private boolean userHasInteracted;
    /**
     * time when model loading has started (for stats)
     */
    private long startTime;


    public SceneLoader(ModelActivity main) {
        this.modelActivity = main;
    }


    public void init() {

        // Camera to show a point of view
        camera = new Camera();
        camera.setChanged(true); // force first draw
/*
        if (modelActivity.getParamUri() == null){
            return;
        }
 */

        startTime = SystemClock.uptimeMillis();

        /*
        Uri uri = parent.getParamUri();
        Log.i("Object3DBuilder", "Loading model " + uri + ". async and parallel..");
        if (uri.toString().toLowerCase().endsWith(".obj") || parent.getParamType() == 0) {
            new WavefrontLoaderTask(parent, uri, this).execute();
        } else if (uri.toString().toLowerCase().endsWith(".stl") || parent.getParamType() == 1) {
            Log.i("Object3DBuilder", "Loading STL object from: "+uri);
            new STLLoaderTask(parent, uri, this).execute();
        } else if (uri.toString().toLowerCase().endsWith(".dae") || parent.getParamType() == 2) {
            Log.i("Object3DBuilder", "Loading Collada object from: "+uri);
            new ColladaLoaderTask(parent, uri, this).execute();
        } else if (uri.toString().toLowerCase().endsWith(".gltf") || parent.getParamType() == 3) {
            Log.i("Object3DBuilder", "Loading GLtf object from: "+uri);
            new GltfLoaderTask(parent, uri, this).execute();
        }
        */
        Uri uri = modelActivity.getParamUri();
        new WavefrontLoaderTask(modelActivity,uri,this).execute();
    }

    public boolean isDrawAxis(){
        return drawAxis;
    }

    public Camera getCamera() {
        return camera;
    }

    private void makeToastText(final String text, final int toastDuration) {
        modelActivity.runOnUiThread(() -> Toast.makeText(modelActivity.getApplicationContext(), text, toastDuration).show());
    }

    public Object3DData getLightBulb() {
        return lightPoint;
    }

    public float[] getLightPosition() {
        return lightPosition;
    }

    /**
     * Hook for animating the objects before the rendering
     */
    public void onDrawFrame() {

        getLightBulb();

        // smooth camera transition
        camera.animate();

        if (objects.isEmpty()) return;

        if (doAnimation) {
            for (int i=0; i<objects.size(); i++) {
                Object3DData obj = objects.get(i);
                animator.update(obj, isShowBindPose());
            }
        }
    }

    synchronized void addObject(Object3DData obj) {
        List<Object3DData> newList = new ArrayList<Object3DData>(objects);
        newList.add(obj);
        this.objects = newList;
        requestRender();
    }

    private void requestRender() {
        // request render only if GL view is already initialized
        if (modelActivity.getGLView() != null) {
            modelActivity.getGLView().requestRender();
        }
    }

    public synchronized List<Object3DData> getObjects() {
        return objects;
    }


    public boolean isDrawWireframe() {
        return this.drawWireframe;
    }

    public boolean isDrawPoints() {
        return this.drawingPoints;
    }

    public boolean isDrawNormals() {
        return drawNormals;
    }

    public boolean isDoAnimation() {
        return doAnimation;
    }

    public boolean isShowBindPose() {
        return showBindPose;
    }

    public boolean isDrawTextures() {
        return drawTextures;
    }

    public boolean isDrawColors() {
        return drawColors;
    }

    public boolean isDrawLighting() {
        return drawLighting;
    }

    public boolean isBlendingEnabled() {
        return isBlendingEnabled;
    }

    @Override
    public void onStart(){
        ContentUtils.setThreadActivity(modelActivity);
    }

    @Override
    public void onLoadComplete(List<Object3DData> datas) {
        // TODO: move texture load to LoaderTask
        for (Object3DData data : datas) {
            if (data.getTextureData() == null && data.getTextureFile() != null) {
                Log.i("LoaderTask","Loading texture... "+data.getTextureFile());
                try (InputStream stream = ContentUtils.getInputStream(data.getTextureFile())){
                    if (stream != null) {
                        data.setTextureData(IOUtils.read(stream));
                    }
                } catch (IOException ex) {
                    data.addError("Problem loading texture " + data.getTextureFile());
                }
            }
        }

        // TODO: move error alert to LoaderTask
        List<String> allErrors = new ArrayList<>();
        for (Object3DData data : datas) {
            addObject(data);
            allErrors.addAll(data.getErrors());
        }
        if (!allErrors.isEmpty()){
            makeToastText(allErrors.toString(), Toast.LENGTH_LONG);
        }
        final String elapsed = (SystemClock.uptimeMillis() - startTime) / 1000 + " secs";
        makeToastText("Build complete (" + elapsed + ")", Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    @Override
    public void onLoadError(Exception ex) {
        Log.e("SceneLoader", ex.getMessage(), ex);
        makeToastText("There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    public Object3DData getSelectedObject() {
        return selectedObject;
    }

    private void setSelectedObject(Object3DData selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void loadTexture(Object3DData obj, Uri uri) throws IOException {
        if (obj == null && objects.size() != 1) {
            makeToastText("Unavailable", Toast.LENGTH_SHORT);
            return;
        }
        obj = obj != null ? obj : objects.get(0);
        obj.setTextureData(IOUtils.read(ContentUtils.getInputStream(uri)));
        this.drawTextures = true;
    }

    public void processTouch(float x, float y) {
        ModelRenderer mr = modelActivity.getGLView().getModelRenderer();
        Object3DData objectToSelect = CollisionDetection.getBoxIntersection(getObjects(), mr.getWidth(), mr.getHeight
                (), mr.getModelViewMatrix(), mr.getModelProjectionMatrix(), x, y);
        if (objectToSelect != null) {
            if (getSelectedObject() == objectToSelect) {
                Log.i("SceneLoader", "Unselected object " + objectToSelect.getId());
                setSelectedObject(null);
            } else {
                Log.i("SceneLoader", "Selected object " + objectToSelect.getId());
                setSelectedObject(objectToSelect);
            }
        }
    }

    public void processMove(float dx1, float dy1) {
        userHasInteracted = true;
    }
}
