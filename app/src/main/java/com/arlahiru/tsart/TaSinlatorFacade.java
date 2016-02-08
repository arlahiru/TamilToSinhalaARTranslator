package com.arlahiru.tsart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlahiru.tsart.ocr.TesseractOCRService;
import com.arlahiru.tsart.overlay.ArOverlayService;
import com.arlahiru.tsart.textdetection.SwtTextDetectionService;
import com.arlahiru.tsart.translation.GoogleTranslationService;
import com.arlahiru.tsart.translation.cache.TranslationCacheService;
import com.arlahiru.tsart.util.ImageUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by lahiru on 10/30/15.
 */
public class TaSinlatorFacade extends AsyncTask<TaSinlatorFacadeTaskParams,Void,TaSinlatorFacadeTaskParams> {

    private static final String TAG = "TaSinlatorFacade";

    public final static String TRANSLATED_IMAGE_PATH = "com.arlahiru.tsart.TRANSLATED_IMAGE_PATH";

    private MainActivity mainActivity;
    private Initializer initializer;
    private SwtTextDetectionService swtTextDetectionService;
    private TesseractOCRService tesseractOCRService;
    private GoogleTranslationService googleTranslationService;
    private ArOverlayService arOverlayService;
    private ProgressDialog progDailog;

    public TaSinlatorFacade(MainActivity activity) {
        this.mainActivity = activity;
        initializer = new Initializer(mainActivity);
        initializer.initialize();
        //initialize services
        swtTextDetectionService = new SwtTextDetectionService(mainActivity);
        tesseractOCRService = new TesseractOCRService();
        googleTranslationService = new GoogleTranslationService(mainActivity);
        arOverlayService = new ArOverlayService();
        progDailog = new ProgressDialog(mainActivity);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog.setMessage("Translating... Please wait");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }

    @Override
    protected TaSinlatorFacadeTaskParams doInBackground(TaSinlatorFacadeTaskParams... params) {
        TaSinlatorFacadeTaskParams param = params[0];
        Bitmap outputImage = null;
        try {
            long start = new Date().getTime();
            Log.d(TAG, "Executing TaSinlatorFacade");

            if(!param.isTextDetectOnly()) {
                //call SWT service
                Log.d(TAG, "Calling SwtTextDetectionService");
                List<Rect> textRegionRectList = swtTextDetectionService.getTextLocations(param.getInputImagePath());
                //call ocr service
                Log.d(TAG, "Calling TesseractOCRService");
                List<AugmentedTextBox> augmentedTextBoxList = null;

                if (param.isSkipSWT()) {
                    augmentedTextBoxList = tesseractOCRService.recognizeTextsAndFgBgColorWithoutSWT(param.getInputData(), textRegionRectList, param.getCameraRes(), param.getScreenRes());
                } else {
                    augmentedTextBoxList = tesseractOCRService.recognizeTextsAndFgBgColor(param.getInputData(), textRegionRectList, param.getCameraRes(), param.getScreenRes());

                }
                //from this onwards we are populating all the attributes in the augmented text boxes and keep passing the reference until it reach final stage

                //call translation service
                Log.d(TAG, "Calling GoogleTranslationService");
                List<AugmentedTextBox> augmentedTextBoxListWithTranslationList = googleTranslationService.getTranslatedAugmentedTextBoxes(param, augmentedTextBoxList);

                //call overlay service
                Log.d(TAG, "Calling ArOverlayService");
                outputImage = arOverlayService.getOverlaidImage(param.getInputImage(), augmentedTextBoxListWithTranslationList);
            }else{
                //draw only the text bounding boxes and return the output imge
                outputImage = swtTextDetectionService.getInputImageWithBoundingBoxes(param.getInputImagePath());
            }

            long end = new Date().getTime();

            Log.d(TAG, "Execution Time - "+((end-start)/1000)+"s");

            param.setOutputImage(outputImage);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
        return param;

    }

    @Override
    protected void onPostExecute(TaSinlatorFacadeTaskParams result) {

        if(result == null){
            Toast toast = Toast.makeText(mainActivity, "Sorry! Application error!",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //show errors during the translation
        if(result.getErrorMessage() != null){
            Toast toast = Toast.makeText(mainActivity,result.getErrorMessage(),Toast.LENGTH_LONG);
            toast.show();
        }
        String translatedImgPath = Tools.saveBitmapToSD(result.getOutputImage(),"img_temp_o");
        progDailog.dismiss();

        Intent intent = new Intent(mainActivity, TranslationResultActivity.class);
        intent.putExtra(TRANSLATED_IMAGE_PATH, translatedImgPath);
        mainActivity.startActivity(intent);

        //OverlayBox overlayBox = new OverlayBox(mainActivity.getBaseContext(),result,800,200);
        //mainActivity.addContentView(overlayBox, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

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
