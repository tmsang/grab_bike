package com.intec.grab.bike.utils.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike.R;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.log.Log;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {
    public SETTING settings;
    public SSLSettings sslSettings = new SSLSettings(false, null);
    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void Initialization(Activity activity) {
        this.activity = activity;

        // ButterKnife.bind(activity);
        settings = new SETTING(activity);
    }




    // ============================================================
    // EditText
    // ============================================================
    public void EnableEditText(int rId)
    {
        EditText editText = this.activity.findViewById(rId);
        editText.setBackgroundResource(R.drawable.tp_edittext_bg);
        editText.setEnabled(true);
    }
    public void DisableEditText(int rId)
    {
        EditText editText = this.activity.findViewById(rId);
        editText.setBackgroundResource(R.drawable.tp_edittext_bg_disable);
        editText.setEnabled(false);
    }

    public String EditText(int rId) {
        EditText editText = this.activity.findViewById(rId);
        return editText.getText().toString();
    }
    public void EditText(int rId, String value) {
        EditText editText = this.activity.findViewById(rId);
        editText.setText(value);
    }
    public void EditTextOnKeyPress(int rId, MyEventCallback callback) {
        EditText txt = this.activity.findViewById(rId);
        txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    callback.execute(txt);
                    return true;
                }
                return false;
            }
        });
    }

    // ============================================================
    // Button
    // ============================================================
    public void EnableButton(int rId)
    {
        Button button = this.activity.findViewById(rId);
        button.setBackgroundResource(R.drawable.tp_button_bg);
        button.setEnabled(true);
    }
    public void DisableButton(int rId)
    {
        Button button = this.activity.findViewById(rId);
        button.setBackgroundResource(R.drawable.tp_button_bg_disable);
        button.setEnabled(false);
    }
    public void ButtonClickEvent(int rId, MyEventCallback callback) {
        Button btn = this.activity.findViewById(rId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(btn);
            }
        });
    }

    // ============================================================
    // TextView
    // ============================================================
    public void SetTextView(int rId, int rString)
    {
        TextView textView = this.activity.findViewById(rId);
        textView.setText(rString);
    }
    public String GetTextView(int rId)
    {
        TextView textView = this.activity.findViewById(rId);
        return textView.getText().toString();
    }
    public void TextViewClickEvent(int rId, MyEventCallback callback) {
        TextView lbl = this.activity.findViewById(rId);
        lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(lbl);
            }
        });
    }

    // ============================================================
    // Toast
    // ============================================================
    public void Toast(String message, String... errors) {
        if (errors.length > 0 && errors[0] != null) {
            Log.e(errors[0]);
        }
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }

    // INVISIBLE: This view is invisible, but it still takes up space for layout purposes.
    // GONE: This view is invisible, and it doesn't take any space for layout purposes.
    public void Loading(int rId, boolean isShow) {
        ProgressBar loading = this.activity.findViewById(rId);
        loading.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void Redirect(Class destinationActivity) {
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
    }

    public Boolean IsNullOrEmpty(String value, String... name) {
        if (StringHelper.isNullOrEmpty(value)) {
            String message = name.length > 0 ? name[0] : "Value Input";
            CommonHelper.showToast(this, message + " is null or empty");
            return true;
        }
        return false;
    }









}
