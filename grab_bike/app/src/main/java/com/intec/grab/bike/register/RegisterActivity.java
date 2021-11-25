package com.intec.grab.bike.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.helper.BaseActivity;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.fullName) EditText txtFullName;
    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.phone) EditText txtPhone;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.smsCode) EditText txtSMSCode;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnSmsCode) ImageButton btnSmsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialization(this);

        // set init
        this.EnableButton(R.id.btnRegister);
        this.DisableEditText(R.id.smsCode);

        // trigger event
        btnSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSmsCode();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void GetSmsCode()
    {
        String phone = txtPhone.getText().toString();
        if (StringHelper.isNullOrEmpty(phone)) {
            this.Toast("Phone is empty");
            return;
        }

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
    }

    private void Register()
    {
        String fullName = this.EditText(R.id.fullName);
        String email = this.EditText(R.id.email);
        String phone = this.EditText(R.id.phone);
        String password = this.EditText(R.id.password);
        String code = this.EditText(R.id.smsCode);

        if (StringHelper.isNullOrEmpty(fullName)) {
            this.Toast("Full Name is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(email)) {
            this.Toast("Email is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(phone)) {
            this.Toast("Phone is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            this.Toast("Password is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(code)) {
            this.Toast("SMS Code is empty");
            return;
        }

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
    }
}
