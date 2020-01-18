package com.mikonski.happa.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.mikonski.happa.R;

public class sliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public sliderAdapter(Context context) {
        this.context = context;
    }

    /**
     * arrays for three animations
     * and text
     */
    public int [] animations = {
            R.raw.travel,
            R.raw.define,
            R.raw.share


    };
    public String [] text ={
            "TRAVEL YOUR WORLD",
            "DEFINE YOUR WORLD",
            "SHARE YOUR WORLD"
    };

    @Override
    public int getCount() {
        return animations.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout_1,container,false);

        LottieAnimationView lottieAnimationView = (LottieAnimationView) view.findViewById(R.id.animation);
        TextView textView = (TextView)view.findViewById(R.id.text);

        lottieAnimationView.setAnimation(animations[position]);
        textView.setText(text[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
