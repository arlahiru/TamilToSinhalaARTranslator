package com.arlahiru.tsart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.arlahiru.tsart.ocr.TesseractOCRService;
import com.arlahiru.tsart.textdetection.SwtTextDetectionService;


public class MainActivity extends Activity implements SensorEventListener {

    static {
        System.loadLibrary("ccv-wrapper");
    }

    private static final String TAG = "MainActivity";
    private CameraPreview mPreview;
    private Button btnCapture, btnFlash, btnFocus;
    private boolean mAutoFocus = true;
    private Camera.PictureCallback mPicture;
    private boolean mFlashBoolean = false;
    private Context myContext;
    private FocusBoxView focusBox;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mInitialized = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private TaSinlatorFacade taSinlatorFacade;

    //return array with two text lines looks like: [[23,45,100,60],[5,17,40,60]]
    public native String[] ccvSwtDetectwords(String imagePath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // display our (only) XML layout - Views already ordered
        setContentView(R.layout.activity_main);
        myContext = this;
        // the accelerometer is used for autofocus
        mSensorManager = (SensorManager) getSystemService(Context.
                SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);

        mPreview = (CameraPreview) findViewById(R.id.preview);

        focusBox = (FocusBoxView) findViewById(R.id.focus_box);

        btnCapture = (Button) findViewById(R.id.button_capture);
        btnCapture.setOnClickListener(captureListener);

        btnFlash = (Button) findViewById(R.id.button_flash);
        btnFlash.setOnClickListener(flashListener);

        btnFocus = (Button) findViewById(R.id.button_focus);
        btnFocus.setOnClickListener(requestFocusListener);

        mPicture = getPictureCallback();

    }

    public OnClickListener captureListener = new OnClickListener() {

        public void onClick(View v) {
            if (mAutoFocus) {
                mAutoFocus = false;
                mPreview.capturePic(mPicture);
                try {


                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }
                mAutoFocus = true;
            }else{
                Toast toast = Toast.makeText(myContext,"Please wait until camera auto focus",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    OnClickListener flashListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFlashBoolean){
                mPreview.setFlash(false);
            }
            else{
                mPreview.setFlash(true);
            }
            mFlashBoolean = !mFlashBoolean;
        }
    };

    OnClickListener requestFocusListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mPreview.requestFocus(myAutoFocusCallback);
        }
    };

    // this is the autofocus call back
    private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
            mAutoFocus = true;
        }
    };


    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    //Bitmap bmp = Tools.getFocusedBitmap(MainActivity.this, camera, data, focusBox.getBox());
                    Bitmap sceneImage = BitmapFactory.decodeByteArray(data, 0, data.length, null);
                    String imgPath = saveBitmapToSD(sceneImage, "swt_i");
                    SwtTextDetectionService swtService= new SwtTextDetectionService(MainActivity.this);
                    Bitmap sceneImageWithTextdetection = swtService.getInputImageWithBoundingBoxes(imgPath);
                    saveBitmapToSD(sceneImageWithTextdetection,"swt_o");
                    /*Log.d(TAG, "TaSinlatorFacade called");
                    new TaSinlatorFacade(MainActivity.this).execute(bmp);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return picture;
    }

    //make picture and save to a folder
    private static File getOutputMediaFile(String fileName) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(AppConstants.DATA_PATH);

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() +File.separator+ "IMG_" +fileName+"_"+ timeStamp + ".png");

        return mediaFile;
    }

    public static String saveBitmapToSD(Bitmap bitmap, String filename){
        try {
            //make a new picture file
            File pictureFile = getOutputMediaFile(filename);
            //write the cropped image to file
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(byteArray);
            fos.close();
            Log.d(TAG, "Picture saved: " + pictureFile.getName());
            return pictureFile.getAbsolutePath();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    // just to close the app and release resources.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    //TODO remove auto focus and implement request focus with a button
    // mainly used for autofocus to happen when the user takes a picture
    // I also use it to redraw the canvas using the invalidate() method
    // when I need to redraw things.
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX  = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaY > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaZ > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) */
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;

    }

    // extra overrides to better understand app lifecycle and assist debugging
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause()");
        mPreview.releaseCamera();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
        //Log.i(TAG, "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart()");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
