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
import com.intec.grab.bike.utils.auth.HttpsTrustManager;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    SETTING settings;
    String cert = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDDTCCAfWgAwIBAgIJANsEVdEI3dSuMA0GCSqGSIb3DQEBCwUAMBQxEjAQBgNV\n" +
            "BAMTCWxvY2FsaG9zdDAeFw0yMTExMDQwMjU5MDdaFw0yMjExMDQwMjU5MDdaMBQx\n" +
            "EjAQBgNVBAMTCWxvY2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
            "ggEBANDgf9hDIVidGDcYoQZbQ5L9UtAh4fLGbgmTEIzaUOYEgb0W78I/4EGUN1xs\n" +
            "A8Xaq72ifFDUrjNLkV21DqKpcGIuZ/Wmp7Tl1A2S41+fJncx4zcFZJjgAkkuiO89\n" +
            "6L1StXBWauhbjtEgMc/HDquOuKOLXCZf8DSqlNkItbPhnpecD/EwT6czmqytCK/T\n" +
            "xjZaQPTGh8trCPK7rn5h//cqFHlRVoNarWix2XStFNvKjsDBefiXmQdSuEzK7MHK\n" +
            "Pg9YoNymeWkdvfYHNoecOoYfFS/coMoD8RDMsfEEb4NkYTJXtqjzRBwForbAMhZN\n" +
            "76rGYJEFF33KA6Q/k2dx54oGisUCAwEAAaNiMGAwDAYDVR0TAQH/BAIwADAOBgNV\n" +
            "HQ8BAf8EBAMCBaAwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwEwFwYDVR0RAQH/BA0w\n" +
            "C4IJbG9jYWxob3N0MA8GCisGAQQBgjdUAQEEAQIwDQYJKoZIhvcNAQELBQADggEB\n" +
            "AMWd7OwamjA1A5AR4OqdR3edA9WAW0e0Rc1j7eIO3QHtxpMf4uhHZ4z6qDPPbNPm\n" +
            "3HmXD4XFnCgnktdI7jxt4va1FIaDEPfQRkZu3QY3xV5Dl7cTLRMv35Q3y579rXRD\n" +
            "G59nqXNtfp4JFUkrMZ3FxaO+dj6LQPAMNNycMKsRCeBn+hlGLsKxXy3QT6l98DAy\n" +
            "CvPN3/e2avV/O0aAU0fBSNivM+bDNQeiumPcznr2gXZUycK4kNyKu6GyuaD2z/fv\n" +
            "ASpZnrMDsdlRUcUoBCuPepZNHzbmrsjajuNCQyP1RgLYy8GqAvKASM09cAdr9wBl\n" +
            "BtTluXCDQXT81IKvHV04JKU=\n" +
            "-----END CERTIFICATE-----\n";
    SSLSettings sslSettings = new SSLSettings(false, "");

    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Login();
                    return true;
                }
                return false;
            }
        });
    }

    private void Login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (StringHelper.isNullOrEmpty(email)) {
            CommonHelper.showToast(LoginActivity.this, "Email is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            CommonHelper.showToast(LoginActivity.this, "Password is empty");
            return;
        }

        //HttpsTrustManager.allowAllSSL();
        SharedService.LoginApi(Constants.API_NET, sslSettings)
            .Login(email, password)
            .enqueue(Callback.callInUI(LoginActivity.this, (json) -> {
                if (StringHelper.isNullOrEmpty(json.jwt)) {
                    CommonHelper.showToast(LoginActivity.this, "JWT Token is null or empty");
                    return;
                }

                settings.jwtToken(json.jwt);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }, (error) -> {
                CommonHelper.showToast(LoginActivity.this, "API cannot reach", error.body());
            }));
    }
}
