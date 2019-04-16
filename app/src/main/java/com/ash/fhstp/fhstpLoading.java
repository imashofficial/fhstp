package com.ash.fhstp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class fhstpLoading extends Dialog {
    public fhstpLoading(Context context) {
        super(context, R.style.LoadingTheme);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(
                R.layout.activity_loading, null);
        setContentView(view);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start animation
        ImageView iv = findViewById(R.id.loadingImg);
        iv.setImageResource(R.drawable.loading_anim);
        AnimationDrawable anim = (AnimationDrawable) iv.getDrawable();
        anim.start();
    }
}
