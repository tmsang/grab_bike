package com.intec.grab.bike.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.reset_password.ResetPasswordActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity {

    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(false, "");

    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.btnForgot) Button btnForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forgot();
            }
        });

        txtEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Forgot();
                    return true;
                }
                return false;
            }
        });
    }

    private void Forgot()
    {
        String email = txtEmail.getText().toString();

        if (StringHelper.isNullOrEmpty(email)) {
            CommonHelper.showToast(ForgotPasswordActivity.this, "Email is empty");
            return;
        }

        SharedService.ForgotPasswordApi(Constants.API_NET, sslSettings)
                .ForgotPassword(email)
                .enqueue(Callback.callInUI(ForgotPasswordActivity.this, (json) ->
                {
                    // store email in ShareReferences
                    settings.email(email);
                    // redirect to Reset Password
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                }, (error) -> {
                    CommonHelper.showToast(ForgotPasswordActivity.this, error.getMessage(), error.body());
                }));
    }
}
