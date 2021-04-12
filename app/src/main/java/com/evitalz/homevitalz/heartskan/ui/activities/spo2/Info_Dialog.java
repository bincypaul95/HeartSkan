package com.evitalz.homevitalz.heartskan.ui.activities.spo2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evitalz.homevitalz.heartskan.R;


public class Info_Dialog extends Dialog {
    Context context;
    String Msg="";
    TextView message,title;
    Button ok;
    boolean success;
    WindowManager windowManager;
    View separator;
    public Info_Dialog(@NonNull Context context, String Msg, boolean success) {
        super(context);
        this.context=context;
        this.Msg=Msg;
        this.success=success;
        this.windowManager=((Activity)context).getWindowManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.success_window);
        Display display=windowManager.getDefaultDisplay();
        Window window = this.getWindow();
        window.setLayout((int)(display.getWidth()*0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
        message=findViewById(R.id.message);
        title=findViewById(R.id.title);
        separator=findViewById(R.id.separator);
        ok=findViewById(R.id.ok);
        setCancelable(false);
        message.setText(Msg);
        if(success)
            ok.setBackgroundColor(Color.parseColor("#00B862"));
        else
            ok.setBackgroundColor(Color.parseColor("#f53a3a"));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Info_Dialog.this.dismiss();
            }
        });

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        ok.setOnClickListener(onClickListener);
    }
    public void set_button_text(String str)
    {
        ok.setText(str);
    }
    public void show_title(String text){
        title.setText(text);
        title.setVisibility(View.VISIBLE);
        separator.setVisibility(View.VISIBLE);
    }
}
