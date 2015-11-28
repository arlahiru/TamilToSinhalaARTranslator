package com.arlahiru.tsart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlahiru.tsart.ocr.TesseractOCRService;
import com.arlahiru.tsart.translation.GoogleTranslationService;
import com.arlahiru.tsart.translation.cache.TranslationCacheService;
import com.arlahiru.tsart.util.ImageUtil;

/**
 * Created by lahiru on 10/30/15.
 */
public class TaSinlatorFacade extends AsyncTask<Bitmap,Void,String>{

    private static final String TAG = "TaSinlatorFacade";

    private MainActivity mainActivity;
    private Initializer initializer;
    //TODO SWT service
    private TesseractOCRService tesseractOCRService;
    private TranslationCacheService translationCacheService;
    private GoogleTranslationService googleTranslationService;

    public TaSinlatorFacade(MainActivity activity) {
        this.mainActivity = activity;
        initializer = new Initializer(mainActivity);
        initializer.initialize();
        tesseractOCRService = new TesseractOCRService();
        translationCacheService = new TranslationCacheService();
        googleTranslationService = new GoogleTranslationService();
    }
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        try {
            //call ocr service
            String tamilRecognizedText = tesseractOCRService.recognizeTexts(bitmaps[0]);
            Log.d(TAG, "Tamil Text=" + tamilRecognizedText);
            //call cache service first
            String sinhalaTranslatedText = translationCacheService.getTranslationFromCache(tamilRecognizedText);
            //call google translation service if we do not have it in the cache
            if (sinhalaTranslatedText == null) {
                Log.d(TAG, "Calling Google Translation Service");
                if (isNetworkConnected()) {
                    sinhalaTranslatedText = googleTranslationService.GET(tamilRecognizedText);
                    //check if translation result is correct
                    if (sinhalaTranslatedText.equals("application-error") || sinhalaTranslatedText.equals("json-error")) {
                        Log.e(TAG, "Sorry! Application error!");
                        return null;
                    }else{
                        //cache new translation
                        translationCacheService.cacheTranslation(tamilRecognizedText,sinhalaTranslatedText);
                    }

                } else {
                    Log.e(TAG, "Please enable your network connection");
                    //return test value
                    //return "ආයුබෝවන්";
                    return "";
                }
            }
            Log.d(TAG, "Translated Text=" + sinhalaTranslatedText);
            return sinhalaTranslatedText;
           //TODO call overlay process


        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return null;
        }


    }
    @Override
    protected void onPostExecute(String result) {
        OverlayBox overlayBox = new OverlayBox(mainActivity.getBaseContext(),result,800,200);
        mainActivity.addContentView(overlayBox, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

    }

    public boolean isNetworkConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) mainActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}



class OverlayBox extends View {

    String text;
    int width,height;
    public OverlayBox(Context context, String text, int width, int height) {
        super(context);
        this.text = text;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*
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
        */
        Bitmap bmp = ImageUtil.getTextImage(text, width, height);
        canvas.drawBitmap(bmp,0,0,null);
        super.onDraw(canvas);
    }
}
