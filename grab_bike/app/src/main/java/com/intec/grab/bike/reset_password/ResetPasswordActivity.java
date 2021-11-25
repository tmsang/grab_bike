package com.intec.grab.bike.reset_password;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {

    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(false, "");

    @BindView(R.id.oldPassword) EditText txtOldPassword;
    @BindView(R.id.newPassword) EditText txtNewPassword;
    @BindView(R.id.smsCode) EditText txtSmsCode;
    @BindView(R.id.btnReset) Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });
        txtOldPassword.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                ResetPassword();
                return true;
            }
            return false;
        });
        txtNewPassword.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                ResetPassword();
                return true;
            }
            return false;
        });
        txtSmsCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                ResetPassword();
                return true;
            }
            return false;
        });
    }

    private void ResetPassword() {
        String email = settings.email();
        String oldPassword = txtOldPassword.getText().toString();
        String newPassword = txtNewPassword.getText().toString();
        String smsCode = txtSmsCode.getText().toString();

        if (StringHelper.isNullOrEmpty(oldPassword)) {
            CommonHelper.showToast(ResetPasswordActivity.this, "Old Password is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(newPassword)) {
            CommonHelper.showToast(ResetPasswordActivity.this, "New Password is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(smsCode)) {
            CommonHelper.showToast(ResetPasswordActivity.this, "SMS Code is empty");
            return;
        }

        //HttpsTrustManager.allowAllSSL();
        SharedService.ResetPasswordApi(Constants.API_NET, sslSettings)
                .ResetPassword(email, oldPassword, newPassword, smsCode)
                .enqueue(Callback.callInUI(ResetPasswordActivity.this, (json) -> {
                    if (StringHelper.isNullOrEmpty(json.jwt)) {
                        CommonHelper.showToast(ResetPasswordActivity.this, "JWT Token is null or empty");
                        return;
                    }

                    settings.jwtToken(json.jwt);
                    Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    startActivity(intent);

                }, (error) -> {
                    CommonHelper.showToast(ResetPasswordActivity.this, error.getMessage(), error.body());
                }));
    }
}
