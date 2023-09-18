package com.jasim0021.blankscreen;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import static  android.content.Context.WINDOW_SERVICE;

public class Window {
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;



    public  Window(Context context){
        this.context=context;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,// for whole screen
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // display it over other app
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,// input focus
                    PixelFormat.TRANSPARENT // for underlayring visible

            );
        }
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.activity_popup_window,null);
//        mView.findViewById(R.id.window_close).setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                close();
//            }
//        });
        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

    }
    public void open (){
        try {
            if (mView.getWindowToken() == null){
                if (mView.getParent()==null){
                    mWindowManager.addView(mView,mParams);
                }
            }
        }catch (Exception e){
            Log.d( "errorOpen: ",e.toString());
        }
    }

   public void close (){
        try {
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView.invalidate();
            ((ViewGroup)mView.getParent()).removeAllViews();
        }catch (Exception e){
            Log.d( "errorClose: ",e.toString());
        }
   }
}