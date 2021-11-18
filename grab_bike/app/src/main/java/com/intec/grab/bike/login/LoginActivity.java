package com.intec.grab.bike.login;

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
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(true, null);

    @BindView(R.id.username) EditText txtUsername;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.btnlogin) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_login);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreProcess_Login();
            }
        });

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    PreProcess_Login();
                    return true;
                }
                return false;
            }
        });
    }

    private void PreProcess_Login() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        if (StringHelper.isNullOrEmpty(username)) {
            CommonHelper.showToast(LoginActivity.this, "Username is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            CommonHelper.showToast(LoginActivity.this, "Password is empty");
            return;
        }

        SharedService.LoginApi(Constants.API_NET, sslSettings)
            .Login(new LoginDto(username, password))
            .enqueue(Callback.callInUI(LoginActivity.this, (json) -> {
                if (json.MessageText.equals("0")) {
                    CommonHelper.showToast(LoginActivity.this, "This account does not exists");
                }
                else if (json.MessageError != null) {
                    CommonHelper.showToast(LoginActivity.this, "Username or Password is invalid", json.MessageError);
                    return;
                }

                String jwtToken = json.MessageText;
                settings.jwtToken(jwtToken);

                // redirect MESSAGE
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }, (error) -> {
                CommonHelper.showToast(LoginActivity.this, "API cannot reach", error.body());
            }));
    }
}
