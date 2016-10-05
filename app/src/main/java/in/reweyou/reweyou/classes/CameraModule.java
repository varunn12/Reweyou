package in.reweyou.reweyou.classes;

import android.content.Context;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by Reweyou on 2/2/2016.
 */
public class CameraModule implements TextureView.SurfaceTextureListener {
    private Camera mCamera;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = getCamera();

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    private Camera getCamera() {
        Camera cam = null;

        try {
            cam = Camera.open();
        } catch (RuntimeException e) {
            //loggerManager.error("Camera not available");
        }

        return cam;
    }
}