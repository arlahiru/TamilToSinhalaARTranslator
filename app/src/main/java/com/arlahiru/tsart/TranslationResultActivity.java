package com.arlahiru.tsart;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class TranslationResultActivity extends Activity {

    private ImageView imageView;
    private Button btnGoBack;

    public TranslationResultActivity(){

    }

    View.OnClickListener btnGoBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_result);
        imageView = (ImageView) findViewById(R.id.imageView1);
        btnGoBack = (Button) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(btnGoBackListener);
        Intent intent = getIntent();
        String translatedImagePath = intent.getStringExtra(MainActivity.TRANSLATED_IMAGE_PATH);
        File file = new File(translatedImagePath);
        Uri uri = Uri.fromFile(file);
        imageView.setImageURI(uri);
    }

}
