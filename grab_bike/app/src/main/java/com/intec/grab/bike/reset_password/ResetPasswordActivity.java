package com.intec.grab.bike.reset_password;

import android.os.Bundle;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.MyEventCallback;

public class ResetPasswordActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Initialization(this);

        // trigger event
        this.ButtonClickEvent(R.id.btnReset, ResetPassword);
        this.EditTextOnKeyPress(R.id.oldPassword, ResetPassword);
        this.EditTextOnKeyPress(R.id.newPassword, ResetPassword);
        this.EditTextOnKeyPress(R.id.smsCode, ResetPassword);
    }

    MyEventCallback ResetPassword = (v -> {
        String email = settings.email();
        String oldPassword = this.EditText(R.id.oldPassword);
        String newPassword = this.EditText(R.id.newPassword);
        String smsCode = this.EditText(R.id.smsCode);

        if (this.IsNullOrEmpty(oldPassword, "Old Password")) return;
        if (this.IsNullOrEmpty(newPassword, "New Password")) return;
        if (this.IsNullOrEmpty(smsCode, "SMS Code")) return;

        //HttpsTrustManager.allowAllSSL();
        SharedService.ResetPasswordApi(Constants.API_NET, sslSettings)
            .ResetPassword(email, oldPassword, newPassword, smsCode)
            .enqueue(Callback.callInUI(ResetPasswordActivity.this, (json) -> {
                if (this.IsNullOrEmpty(json.jwt, "JWT Token")) return;

                settings.jwtToken(json.jwt);
                this.Redirect(MainActivity.class);
            }, (error) -> {
                String message = error.getCause() == null ? null : error.getCause().toString();
                this.Toast(message, error.body());
            }));
    });
}
