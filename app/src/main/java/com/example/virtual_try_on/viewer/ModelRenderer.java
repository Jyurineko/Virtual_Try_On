package com.example.virtual_try_on.viewer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.virtual_try_on.scene.SceneLoader;

import org.andresoviedo.android_3d_model_engine.drawer.DrawerFactory;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.util.android.GLUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class ModelRenderer implements GLSurfaceView.Renderer {
    private final static String TAG = ModelRenderer.class.getName();
    // frustrum - nearest pixel
    private static final float near = 1f;
    // frustrum - fartest pixel
    private static final float far = 30f;

    // 3D window (parent component)
    private ModelSurfaceView modelSurfaceView;
    // width of the screen
    private int width;
    // height of the screen
    private int height;

    /**
     * Drawer factory to get right renderer/shader based on object attributes
     */
    private DrawerFactory drawer;
    /**
     * 3D Axis (to show if needed)
     */
    private final Object3DData axis = Object3DBuilder.buildAxis().setId("axis");

    // The wireframe associated shape (it should be made of lines only)
    private Map<Object3DData, Object3DData> wireframes = new HashMap<>();
    // The loaded textures
    private Map<Object, Integer> textures = new HashMap<>();
    // The corresponding opengl bounding boxes and drawer
    private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<>();
    // The corresponding opengl bounding boxes
    private Map<Object3DData, Object3DData> normals = new HashMap<>();
    private Map<Object3DData, Object3DData> skeleton = new HashMap<>();

    // 3D matrices to project our 3D world
    private final float[] viewMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] lightPosInEyeSpace = new float[4];

    /**
     * Whether the info of the model has been written to console log
     */
    private Map<Object3DData, Boolean> infoLogged = new HashMap<>();
    /**
     * Did the application explode?
     */
    private boolean fatalException = false;

    /**
     * Construct a new renderer for the specified surface view
     *
     * @param modelSurfaceView
     *            the 3D window
     */

    public ModelRenderer(ModelSurfaceView modelSurfaceView) throws IllegalAccessException, IOException {
        this.modelSurfaceView = modelSurfaceView;
        // This component will draw the actual models using OpenGL
        drawer = new DrawerFactory(modelSurfaceView.getContext());
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0,0,1,1);

        // Use culling to remove back faces.
        // Don't remove back faces so we can see them
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing for hidden-surface elimination.
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Enable not drawing out of view port
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.width = width;
        this.height = height;

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        // the projection matrix is the 3D virtual space (cube) that we want to project
        float ratio = (float) width / height;
        Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if(fatalException){
            return;
        }
        try {

            GLES20.glViewport(0, 0, width, height);
            GLES20.glScissor(0, 0, width, height);

            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            SceneLoader scene = modelSurfaceView.getModelActivity().getScene();
            if (scene == null) {
                // scene not ready
                return;
            }

            if (scene.isBlendingEnabled()) {
                // Enable blending for combining colors when there is transparency
                GLES20.glEnable(GLES20.GL_BLEND);
                GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            } else {
                GLES20.glDisable(GLES20.GL_BLEND);
            }

            // animate scene
            scene.onDrawFrame();

            // recalculate mvp matrix according to where we are looking at now
            Camera camera = scene.getCamera();
            if (camera.hasChanged()) {
                // INFO: Set the camera position (View matrix)
                // The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)

                // the projection matrix is the 3D virtual space (cube) that we want to project
                float ratio = (float) width / height;

                Matrix.setLookAtM(viewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
                        camera.zView, camera.xUp, camera.yUp, camera.zUp);
                Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

                camera.setChanged(false);
            }

            onDrawFrame(viewMatrix, projectionMatrix, viewProjectionMatrix, lightPosInEyeSpace, null);

        }catch (Exception ex){
            Log.e("ModelRenderer", "Fatal exception: "+ex.getMessage(), ex);
            fatalException = true;
        }
    }

    private void onDrawFrame(float[] viewMatrix, float[] projectionMatrix, float[] viewProjectionMatrix,
                             float[] lightPosInEyeSpace, float[] colorMask) {

        SceneLoader scene = modelSurfaceView.getModelActivity().getScene();

        // draw light
        if (scene.isDrawLighting()) {

            Object3D lightBulbDrawer = drawer.getPointDrawer();

            Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, scene.getLightBulb().getModelMatrix(), 0);

            // Calculate position of the light in eye space to support lighting
            Matrix.multiplyMV(lightPosInEyeSpace, 0, modelViewMatrix, 0, scene.getLightPosition(), 0);

            // Draw a point that represents the light bulb
            //lightBulbDrawer.draw(scene.getLightBulb(), projectionMatrix, viewMatrix, -1, lightPosInEyeSpace, colorMask);

        }

        // draw axis
        if (scene.isDrawAxis()){
            Object3D basicDrawer = drawer.getPointDrawer();
            basicDrawer.draw(axis, projectionMatrix, viewMatrix, axis.getDrawMode(), axis
                    .getDrawSize(),-1, lightPosInEyeSpace, colorMask);
        }


        // is there any object?
        if (scene.getObjects().isEmpty()){
            return;
        }

        // draw all available objects
        List<Object3DData> objects = scene.getObjects();
        for (int i=0; i<objects.size(); i++) {
            Object3DData objData = null;
            try {
                objData = objects.get(i);

                Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting(),
                        scene.isDoAnimation(), scene.isDrawColors());

                if (drawerObject == null){
                    continue;
                }

                if (!infoLogged.containsKey(objData)) {
                    Log.i("ModelRenderer","Model '"+objData.getId()+"'. Drawer "+drawerObject.getClass().getName());
                    infoLogged.put(objData, true);
                }

                boolean changed = objData.isChanged();

//				if (objData instanceof AnimatedModel
//						&& ((AnimatedModel) objData).getGltfAnimation() != null){
//					//TODO: read each animation model data and change vertex inside objData
//					// based on channel data and do transformation
//					AnimationModel animation = ((AnimatedModel) objData).getGltfAnimation();
//					for (AnimationModel.Channel channelModel: animation.getChannels()){
//						String path = Channel
//					}
//				}

                //TODO: refactor code for loading more textures, consider delete textures map and add texture class
                Integer textureId = textures.get(objData.getTextureData());
                Integer emissiveTextureId = textures.get(objData.getEmissiveTextureData());
                if (textureId == null && objData.getTextureData() != null) {
                    //Log.i("ModelRenderer","Loading texture '"+objData.getTextureFile()+"'...");
                    ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
                    ByteArrayInputStream emissiveTextureIs = null;
                    if (emissiveTextureId == null && objData.getEmissiveTextureData() != null){
                        emissiveTextureIs = new ByteArrayInputStream(objData.getEmissiveTextureData());
                    }
                    int[] textureIds = GLUtil.loadTexture(textureIs, emissiveTextureIs);
                    textureId = textureIds[0];
                    emissiveTextureId = textureIds[1];
                    textureIs.close();
                    if (emissiveTextureIs != null){
                        emissiveTextureIs.close();
                    }
                    textures.put(objData.getTextureData(), textureId);
                    textures.put(objData.getEmissiveTextureData(), emissiveTextureId);
                    objData.setEmissiveTextureHandle(emissiveTextureId);
                    //Log.i("GLUtil", "Loaded texture ok");
                }
                if (textureId == null){
                    textureId = -1;
                }

                // draw points
                if (objData.getDrawMode() == GLES20.GL_POINTS){
                    Object3D basicDrawer = drawer.getPointDrawer();
                    basicDrawer.draw(objData, projectionMatrix, viewMatrix, GLES20.GL_POINTS,lightPosInEyeSpace);
                }

                // draw wireframe
                else if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS
                        && objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
                        && objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
                    // Log.d("ModelRenderer","Drawing wireframe model...");
                    try{
                        // Only draw wireframes for objects having faces (triangles)
                        Object3DData wireframe = wireframes.get(objData);
                        if (wireframe == null || changed) {
                            Log.i("ModelRenderer","Generating wireframe model...");
                            wireframe = Object3DBuilder.buildWireframe(objData);
                            wireframes.put(objData, wireframe);
                        }
                        drawerObject.draw(wireframe, projectionMatrix, viewMatrix, wireframe.getDrawMode(),
                                wireframe.getDrawSize(), textureId, lightPosInEyeSpace,
                                colorMask);
                    }catch(Error e){
                        Log.e("ModelRenderer",e.getMessage(),e);
                    }
                }

                // draw points
                else if (scene.isDrawPoints() || objData.getFaces() == null || !objData.getFaces().loaded()){
                    drawerObject.draw(objData, projectionMatrix, viewMatrix
                            , GLES20.GL_POINTS, objData.getDrawSize(),
                            textureId, lightPosInEyeSpace, colorMask);
                }

                // draw solids
                else {
                    drawerObject.draw(objData, projectionMatrix, viewMatrix,
                            textureId, lightPosInEyeSpace, colorMask);
                }


                // Draw normals
                if (scene.isDrawNormals()) {
                    Object3DData normalData = normals.get(objData);
                    if (normalData == null || changed) {
                        normalData = Object3DBuilder.buildFaceNormals(objData);
                        if (normalData != null) {
                            // it can be null if object isnt made of triangles
                            normals.put(objData, normalData);
                        }
                    }
                    if (normalData != null) {
                        Object3D normalsDrawer = drawer.getFaceNormalsDrawer();
                        normalsDrawer.draw(normalData, projectionMatrix, viewMatrix, -1, null);
                    }
                }

                // TODO: enable this only when user wants it
                // obj3D.drawVectorNormals(result, viewMatrix);
            } catch (Exception ex) {
                Log.e("ModelRenderer","There was a problem rendering the object '"+objData.getId()+"':"+ex.getMessage(),ex);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[] getModelProjectionMatrix() {
        return projectionMatrix;
    }

    public float[] getModelViewMatrix() {
        return viewMatrix;
    }
}
