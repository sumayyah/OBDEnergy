package com.example.obdenergy.obdenergy.Utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Created by Sumayyah on 7/10/2014.
 */
public class SlideoutMenu extends LinearLayout implements Animation.AnimationListener{

    private boolean isOpen = false;
    private int windowWidth = 0;
    private int windowHeight = 0;


    public SlideoutMenu(final Context context, AttributeSet attributes) {
        super(context, attributes);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void width(int w) { windowWidth = w;}
    public void height(int w) { windowHeight = w;}
    public int width() { return windowWidth;}
    public int height() { return windowHeight;}


    public void toggle()
    {
        TranslateAnimation anim;

        isOpen = !isOpen;

        if (isOpen) {
            setVisibility(View.VISIBLE);
            anim = new TranslateAnimation(width()*-1, 0.0f, 0.0f,0.0f);
        }
        else {
            anim=new TranslateAnimation(0.0f, width()*-1, 0.0f, 0.0f);
            anim.setAnimationListener(this);
        }

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(1.0f));
        startAnimation(anim);
    }

    @Override
    public void onAnimationStart(Animation animation)
    {
        setVisibility(View.GONE);
    }

    @Override
    public void onAnimationEnd(Animation animation) { }

    @Override
    public void onAnimationRepeat(Animation animation) { }
}
