package com.mikonski.happa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikonski.happa.R;
import com.mikonski.happa.utility.sliderAdapter;

public class onBoardingActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private sliderAdapter sliderAdapter;
    private LinearLayout linearLayout;
    private TextView[] textViews;
    Button skip;

    /**
     * shows a short walk through and is displayed only ones after installation
     * this is ensured by shared preference (login activity)
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        sliderAdapter = new sliderAdapter(this);

        linearLayout = findViewById(R.id.dots);
        skip = findViewById(R.id.skip);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);

        viewPager.addOnPageChangeListener(listener);
    }

    // add text view dynamically to linear layout
    private  void addDots(int position){
        textViews = new TextView[3];
        linearLayout.removeAllViews();

        for (int i = 0; i < textViews.length;i++){
            textViews[i] = new TextView(this);
            textViews[i].setText(Html.fromHtml("&#8226;"));
            textViews[i].setTextColor(getResources().getColor(R.color.colorAccent));
            textViews[i].setTextSize(35);

            linearLayout.addView(textViews[i]);
        }
        if(textViews.length > 0){

            textViews[position].setTextSize(45);

        }
    }
    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * display dots below animation and next button when last screen reached
         */
        @Override
        public void onPageSelected(int position) {
            addDots(position);


            if (position == textViews.length-1){
                skip.setEnabled(true);
                skip.setVisibility(View.VISIBLE);
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent login = new Intent(onBoardingActivity.this,LoginAcivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                    }
                });

            }
            else {
                skip.setEnabled(false);
                skip.setVisibility(View.GONE);
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
