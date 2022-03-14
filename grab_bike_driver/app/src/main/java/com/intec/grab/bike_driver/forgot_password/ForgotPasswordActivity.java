package com.intec.grab.bike_driver.forgot_password;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.reset_password.ResetPasswordActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.MyEventCallback;

import butterknife.BindView;

public class ForgotPasswordActivity extends BaseActivity
{
    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.btnForgot) Button btnForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Initialization(this);

        this.ButtonClickEvent(R.id.btnForgot, Forgot);
        this.EditTextOnKeyPress(R.id.email, Forgot);
    }

    MyEventCallback Forgot = (v ->
    {
        String email = txtEmail.getText().toString();
        if (this.IsNullOrEmpty(email, "Email")) return;

        SharedService.ForgotPasswordApi(Constants.API_NET, sslSettings)
                .ForgotPassword(email)
                .enqueue(Callback.callInUI(ForgotPasswordActivity.this, (json) ->
                {
                    // store email in ShareReferences
                    settings.email(email);
                    // redirect to Reset Password
                    this.Redirect(ResetPasswordActivity.class);
                }, (error) -> {
                    this.Toast(error.getMessage(), error.body());
                }));
    });
}
