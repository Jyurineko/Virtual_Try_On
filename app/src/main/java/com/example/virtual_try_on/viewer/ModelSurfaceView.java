package com.example.virtual_try_on.viewer;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.virtual_try_on.controller.TouchController;

import java.io.IOException;

public class ModelSurfaceView extends GLSurfaceView {

    private ModelActivity modelActivity;
    private ModelRenderer mRenderer;
    private TouchController touchHandler;

    public ModelSurfaceView(ModelActivity modelActivity) throws IllegalAccessException, IOException {
        super(modelActivity);

        // parent component
        this.modelActivity = modelActivity;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // This is the actual renderer of the 3D space
        mRenderer = new ModelRenderer(this);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        // TODO: enable this?
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        touchHandler = new TouchController(this, mRenderer);
    }

    //-----------------------------------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }

    public ModelActivity getModelActivity() {
        return modelActivity;
    }

    public ModelRenderer getModelRenderer(){
        return mRenderer;
    }
}
