package com.arlahiru.tsart.translation.cache;

import com.arlahiru.tsart.AppConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lahiru on 10/29/15.
 */
public class TranslationCacheService {

    private static final String TAG = "CacheService";
    private File cacheFile;
    private Map<String,String> cacheMap = new HashMap<String,String>(0);

    public TranslationCacheService() {
        cacheFile = new File(AppConstants.DATA_PATH + "cache/cache.txt");
        //load cache map for the first time
        //loadCacheMap();
    }

    public void cacheTranslation(String srcText,String targetText){
        try {
            //put new translation to the cache map first
            cacheMap.put(srcText,targetText);
            //write new translation to the cache file
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cacheFile), "UTF8"));
            //entry - e.g src text = target text
            out.append(srcText).append("=").append(targetText).append("\n");
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadCacheMap(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), "UTF8"));
            String translation;
            while ((translation = reader.readLine()) != null) {
                //entry - e.g src text = target text
                if(translation.contains("=")) {
                    String[] translationArray = translation.split("=");
                    cacheMap.put(translationArray[0], translationArray[1]);
                }

            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getTranslationFromCache(String srcText){
        return cacheMap.get(srcText);
    }


}
