package com.arlahiru.tsart.translation;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
public class GoogleTranslationServiceImpl extends AsyncTask<String, Void, String>{

    Activity parentActivity;
    private String googleApiKey="AIzaSyAvWGq9LRu5sH8e2vwUeWF4Ve8U5UhLb3s";

    public GoogleTranslationServiceImpl(Activity activity) {
        this.parentActivity = activity;
    }

    @Override
    protected String doInBackground(String... tamilTexts) {
        if (isNetworkConnected()) {
            return GET(tamilTexts[0]);
        } else {
            return "no-network";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        if (!result.equals("no-network")){
            Log.i("Translated Text", result);
            Toast.makeText(parentActivity.getBaseContext(), "Translated Text=" + result, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(parentActivity.getBaseContext(), "Please enable your network connection", Toast.LENGTH_LONG).show();
        }
    }

    public String GET(String tamilText){
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
        ConnectivityManager connMgr = (ConnectivityManager) parentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
