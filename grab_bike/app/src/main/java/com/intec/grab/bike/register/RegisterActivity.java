package com.intec.grab.bike.register;

import android.os.Bundle;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.helper.BaseActivity;
import com.intec.grab.bike.utils.helper.MyEventCallback;

public class RegisterActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialization(this);

        // set init
        this.EnableButton(R.id.btnRegister);
        this.DisableEditText(R.id.smsCode);

        // trigger event
        this.ButtonClickEvent(R.id.smsCode, GetSmsCode);
        this.ButtonClickEvent(R.id.btnRegister, Register);
    }

    MyEventCallback GetSmsCode = (v ->
    {
        String phone = this.EditText(R.id.phone);
        if (this.IsNullOrEmpty(phone, "Phone")) return;

        this.Loading(R.id.loading, true);
        SharedService.RegisterApi(Constants.API_NET, sslSettings)
            .GetSmsCode(phone)
            .enqueue(Callback.callInUI(RegisterActivity.this, (json) -> {
                this.EnableEditText(R.id.smsCode);
                this.Loading(R.id.loading, false);
            }, (error) -> {
                this.Toast(error.body(), error.body());
                this.Loading(R.id.loading, false);
            }));
    });

    MyEventCallback Register = (v ->
    {
        String fullName = this.EditText(R.id.fullName);
        String email = this.EditText(R.id.email);
        String phone = this.EditText(R.id.phone);
        String password = this.EditText(R.id.password);
        String code = this.EditText(R.id.smsCode);

        if (this.IsNullOrEmpty(fullName, "Full Name")) return;
        if (this.IsNullOrEmpty(email, "Email")) return;
        if (this.IsNullOrEmpty(phone, "Phone")) return;
        if (this.IsNullOrEmpty(password, "Password")) return;
        if (this.IsNullOrEmpty(code, "SMS Code")) return;

        this.Loading(R.id.loading, true);
        SharedService.RegisterApi(Constants.API_NET, sslSettings)
            .Register(fullName, email, phone, password, code)
            .enqueue(Callback.callInUI(RegisterActivity.this, (json) -> {
                this.SetTextView(R.id.lblMessage, R.string.register_msg_active_account);
                this.DisableButton(R.id.btnRegister);
                this.Loading(R.id.loading, false);
            }, (error) -> {
                this.Toast(error.body(), error.body());
                this.Loading(R.id.loading, false);
            }));
    });
}
