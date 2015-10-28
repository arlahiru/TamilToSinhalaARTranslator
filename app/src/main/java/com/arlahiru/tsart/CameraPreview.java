package com.arlahiru.tsart;

/**
 * Created by lahiru on 9/17/15.
 */
import java.io.ByteArrayOutputStream;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private String TAG="CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private byte[] mBuffer;

    // this constructor used when requested as an XML resource
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public void init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where to draw.
        try {
            mCamera = Camera.open(); // WARNING: without permission in Manifest.xml, crashes
            // Setting the right parameters in the camera
            Camera.Parameters params = mCamera.getParameters();
            params.setPictureSize(1024,768);
            params.setPictureFormat(PixelFormat.JPEG);
            params.setJpegQuality(85);
            mCamera.setParameters(params);
        }
        catch (RuntimeException exception) {
            //Log.i(TAG, "Exception on Camera.open(): " + exception.toString());
            Toast.makeText(getContext(), "Camera broken, quitting :(",Toast.LENGTH_LONG).show();
            // TODO: exit program
        }

        try {
            mCamera.setPreviewDisplay(holder);

        } catch (Exception exception) {
            //Log.e(TAG, "Exception trying to set preview");
            releaseCamera();
            // TODO: add more exception handling logic here
        }
    }

    public void capturePic(Camera.PictureCallback mPicture){
        mCamera.takePicture(null, null, mPicture);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        //Log.i(TAG,"SurfaceDestroyed being called");
        releaseCamera();
    }

    // FYI: not called for each frame of the camera preview
    // gets called on my phone when keyboard is slid out
    // requesting landscape orientation prevents this from being called as camera tilts
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //Log.i(TAG, "Preview: surfaceChanged() - size now " + w + "x" + h);
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        try {
            mParameters = mCamera.getParameters();
            mParameters.set("orientation","landscape");
            mCamera.setParameters(mParameters); // apply the changes
        } catch (Exception e) {
            e.printStackTrace();
            // older phone - doesn't support these calls
        }

        mCamera.startPreview();
    }

    public Parameters getCameraParameters(){
        return mCamera.getParameters();
    }

    public void setCameraFocus(AutoFocusCallback autoFocus){
        Log.i(TAG, "setCameraFocus called"); // DEBUG
        if (mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_AUTO) ||
                mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_MACRO)){
            Log.i(TAG, "Camera autoFocus called");
            mCamera.autoFocus(autoFocus);
        }
    }

    public void setFlash(boolean flash){
        if (flash){
            mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
        }
        else{
            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
        }
    }

    public void releaseCamera(){
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}