package com.intec.grab.bike.forgot_password;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.reset_password.ResetPasswordActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.MyEventCallback;

import butterknife.BindView;

public class ForgotPasswordActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Initialization(this);

        this.ButtonClickEvent(R.id.btnForgot, Forgot);
        this.EditTextOnKeyPress(R.id.txtEmail, Forgot);
    }

    MyEventCallback Forgot = (v ->
    {
        String email = EditText(R.id.txtEmail);
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
                    String message = error.getCause() == null ? null : error.getCause().toString();
                    this.Toast(message);
                }));
    });
}
