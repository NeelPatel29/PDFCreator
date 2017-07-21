package com.pdfkns.pdfcreator.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdfkns.pdfcreator.R;
import com.pdfkns.pdfcreator.Tools.PrefManager;

public class SplashScreen extends AppCompatActivity {
    ProgressBar progressBar;
    Integer count;
    PrefManager prefManager;
    RelativeLayout relativeLayout;
    RelativeLayout.LayoutParams layoutParams;
    TextView textviewTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        relativeLayout = new RelativeLayout(getApplicationContext());
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        relativeLayout.setBackgroundColor(Color.parseColor("#08557E"));
        prefManager = new PrefManager(this);



        textviewTitle = new TextView(getApplicationContext());
        textviewTitle.setId(R.id.textviewTitle);
        RelativeLayout.LayoutParams rlLayoutParamsTextview = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textviewTitle.setText("PDFCreator");
        textviewTitle.setTextColor(Color.WHITE);
        textviewTitle.setTextSize(50);
        textviewTitle.setPadding(150,0,0,0);
        textviewTitle.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        rlLayoutParamsTextview.addRule(RelativeLayout.CENTER_IN_PARENT);
        textviewTitle.setLayoutParams(rlLayoutParamsTextview);

        progressBar = new ProgressBar(getApplicationContext());
        progressBar.setId(R.id.progressBar);
        RelativeLayout.LayoutParams rlLayoutParamsProgress = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlLayoutParamsProgress.addRule(RelativeLayout.BELOW,R.id.textviewTitle);
        progressBar.setLayoutParams(rlLayoutParamsProgress);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setRepeatCount(5);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        textviewTitle.findViewById(R.id.textviewTitle).startAnimation(alphaAnimation);

        relativeLayout.addView(textviewTitle);
        relativeLayout.addView(progressBar);
        setContentView(relativeLayout,layoutParams);
        new MyTask().execute(5);
}
class MyTask extends AsyncTask<Integer, Integer, String> {
    @Override
    protected String doInBackground(Integer... params) {
        for (count = 1; count <= params[0]; count++) {
            try {
                Thread.sleep(1000);
                publishProgress(count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(prefManager.isFirstTimeLaunch()){
            startActivity(new Intent(SplashScreen.this,Guide.class));
            finish();

        }else{
            startActivity(new Intent(SplashScreen.this,MainActivity.class));
            finish();

        }
        return "Task Completed.";
    }
    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);

    }
    @Override
    protected void onPreExecute() {

    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }
}
}

