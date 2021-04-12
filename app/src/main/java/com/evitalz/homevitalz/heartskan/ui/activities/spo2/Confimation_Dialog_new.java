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


public class Confimation_Dialog_new extends Dialog {
    Context context;
    String Msg="";
    TextView message,title;
    Button yes,no,maybe;
    boolean success_yes,success_no,success_maybe;
    WindowManager windowManager;
    View separator;
    public Confimation_Dialog_new(@NonNull Context context, String Msg, boolean success_yes, boolean maybe, boolean success_no) {
        super(context);
        this.context=context;
        this.Msg=Msg;
        this.success_yes=success_yes;
        this.success_no=success_no;
        this.success_maybe=maybe;
        this.windowManager=((Activity)context).getWindowManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirmation_window_new);
        Display display=windowManager.getDefaultDisplay();
        Window window = this.getWindow();
        window.setLayout((int)(display.getWidth()*0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
        message=findViewById(R.id.message);
        title=findViewById(R.id.title);
        separator=findViewById(R.id.separator);
        yes=findViewById(R.id.yes);
        maybe=findViewById(R.id.maybe);
        no=findViewById(R.id.no);
        setCancelable(false);
        message.setText(Msg);
        if(success_yes)
            yes.setBackgroundColor(Color.parseColor("#00B862"));
        else
            yes.setBackgroundColor(Color.parseColor("#f53a3a"));
        if(success_maybe)
            maybe.setBackgroundColor(Color.parseColor("#00B862"));
        else
            maybe.setBackgroundColor(Color.parseColor("#f53a3a"));
        if(success_no)
            no.setBackgroundColor(Color.parseColor("#00B862"));
        else
            no.setBackgroundColor(Color.parseColor("#f53a3a"));
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Confimation_Dialog_new.this.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Confimation_Dialog_new.this.dismiss();
            }
        });
        maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Confimation_Dialog_new.this.dismiss();
            }
        });

    }

    public void set_yesOnClickListener(View.OnClickListener onClickListener) {
        yes.setOnClickListener(onClickListener);
    }
    public void set_maybeOnClickListener(View.OnClickListener onClickListener) {
        maybe.setOnClickListener(onClickListener);
    }
    public void set_noOnClickListener(View.OnClickListener onClickListener) {
        no.setOnClickListener(onClickListener);
    }
    public void set_text_yes_button(String text)
    {
        yes.setText(text);
    }
    public void set_text_maybe_button(String text)
    {
        maybe.setText(text);
    }
    public void set_text_no_button(String text)
    {
        no.setText(text);
    }
    public void show_title(String text){
        title.setText(text);
        title.setVisibility(View.VISIBLE);
        separator.setVisibility(View.VISIBLE);
    }
    public void set_alignment(int align){
       message.setTextAlignment(align);
    }
}
