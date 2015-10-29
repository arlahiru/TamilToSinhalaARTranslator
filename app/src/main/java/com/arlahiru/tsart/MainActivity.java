package com.arlahiru.tsart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.arlahiru.tsart.ocr.TesseractOCRService;
import com.arlahiru.tsart.translation.GoogleTranslationServiceImpl;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;


public class MainActivity extends Activity implements SensorEventListener {

    private CameraPreview mPreview;
    private Button btnCapture, btnFlash;
    private boolean mAutoFocus = true;
    private Camera.PictureCallback mPicture;
    private boolean mFlashBoolean = false;
    private Context myContext;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mInitialized = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private TesseractOCRService ocrService;

    private File mLocation = new File(Environment.
            getExternalStorageDirectory(),"test.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

        btnCapture = (Button) findViewById(R.id.button_capture);
        btnCapture.setOnClickListener(captureListener);

        btnFlash = (Button) findViewById(R.id.button_flash);
        btnFlash.setOnClickListener(flashListener);

        mPicture = getPictureCallback();
        //initialize ocr service
        ocrService = new TesseractOCRService(this);

        GuideBox guideBox = new GuideBox(this);

        //overlay guide box
        addContentView(guideBox, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


    }

    public OnClickListener captureListener = new OnClickListener() {

        public void onClick(View v) {
            if (mAutoFocus) {
                mAutoFocus = false;
                //mPreview.capturePic(mPicture);
                //new GoogleTranslationServiceImpl(MainActivity.this).execute("வர");

                //test ocr
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
                String text = ocrService.recognizeTexts(bitmap);
                Log.i("RECOGNIZED TEXT",text);
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

                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext,"Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return picture;
    }

    //make picture and save to a folder
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "tsart");

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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    // just to close the app and release resources.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

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

   class GuideBox extends View {
        public GuideBox(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(72);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setColor(Color.BLUE);
            textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));


            Paint paint2 = new Paint();
            paint2.setStyle(Paint.Style.FILL_AND_STROKE);
            paint2.setColor(Color.WHITE);



            int x0 = canvas.getWidth() / 2;
            int y0 = canvas.getHeight() / 2;
            int dx = canvas.getHeight() / 3;
            int dy = canvas.getHeight() / 12;


            canvas.drawRect(x0 - dx, y0 - dy, x0 + dx, y0 + dy, paint2);
            canvas.drawText("\\u65E5\\u672C\\u8A9E", (x0 - dx)+10, (y0 - dy)+60, textPaint);

            super.onDraw(canvas);
        }
}