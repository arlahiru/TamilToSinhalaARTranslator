package com.arlahiru.tsart.translation;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.arlahiru.tsart.AugmentedTextBox;
import com.arlahiru.tsart.MainActivity;
import com.arlahiru.tsart.TaSinlatorFacade;
import com.arlahiru.tsart.TaSinlatorFacadeTaskParams;
import com.arlahiru.tsart.TranslationResultActivity;
import com.arlahiru.tsart.translation.cache.TranslationCacheService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lahiru on 10/28/15.
 */
public class GoogleTranslationService {

    private static final String TAG = "TranslationService";

    private String googleApiKey="AIzaSyAvWGq9LRu5sH8e2vwUeWF4Ve8U5UhLb3s";
    private MainActivity mainActivity;
    private TranslationCacheService translationCacheService;

    public GoogleTranslationService(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        translationCacheService = new TranslationCacheService();
    }

    private String googleTranslateRestApiService(String tamilText){
        String jsonResult = "application-error";
        InputStream inputStream = null;
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            String encodedTamilText = URLEncoder.encode(tamilText, "UTF-8");

            String restServiceUrl="https://www.googleapis.com/language/translate/v2?key="+googleApiKey+"&source=ta&target=si&q="+encodedTamilText;

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(restServiceUrl));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null) {
                jsonResult = convertInputStreamToJsonString(inputStream);
                return getTranslatedTextFromJson(jsonResult);
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return jsonResult;
    }

    public List<AugmentedTextBox> getTranslatedAugmentedTextBoxes(TaSinlatorFacadeTaskParams param,List<AugmentedTextBox> augTextBoxes){

        List<AugmentedTextBox> finalAugTextBoxList = new ArrayList<AugmentedTextBox>(0);
        for(AugmentedTextBox augBox: augTextBoxes){

            //call cache service first
            String sinhalaTranslatedText = translationCacheService.getTranslationFromCache(augBox.getSourceText());
            //call google translation service if we do not have it in the cache
            if (sinhalaTranslatedText == null) {
                Log.d(TAG, "Calling Google Translation Service");
                if (isNetworkConnected()) {
                    sinhalaTranslatedText = googleTranslateRestApiService(augBox.getSourceText());
                    //check if translation result is correct
                    if (sinhalaTranslatedText.equals("application-error") || sinhalaTranslatedText.equals("json-error")) {
                        Log.e(TAG, "Sorry! Translation error!");
                        param.setErrorMessage("Sorry! Translation error!");
                        return finalAugTextBoxList;
                    } else {
                        //cache new translation
                        translationCacheService.cacheTranslation(augBox.getSourceText(), sinhalaTranslatedText);
                    }

                } else {
                    Log.e(TAG, "Please enable your network connection");
                    param.setErrorMessage("Please enable your network connection");
                    //return "ආයුබෝවන්";
                    return finalAugTextBoxList;
                }
            }
            Log.d(TAG, "Translated Text=" + sinhalaTranslatedText);
            augBox.setTargetText(sinhalaTranslatedText);
            augBox.setTargetText(sinhalaTranslatedText);
            finalAugTextBoxList.add(augBox);
        }
        return finalAugTextBoxList;
    }

    private String convertInputStreamToJsonString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }

    /**
     sample json result from Google Translation Service looks like this:
     {
         "data": {
             "translations": [
                 {
                 "translatedText": "Hallo Welt"
                 }
             ]
         }
     }
     */
    private String getTranslatedTextFromJson(String jsonResult){
        try{
            JSONObject json = new JSONObject(jsonResult);
            JSONObject data = json.getJSONObject("data");
            JSONArray translations = data.getJSONArray("translations");
            //Note: return zero'th element as we assume the result contains one element
            return translations.getJSONObject(0).getString("translatedText");

        }
        catch(Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
            return "json-error";
        }
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

