package com.intec.grab.bike_driver.register;

import android.os.Bundle;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.MyEventCallback;

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
        this.ImageButtonClickEvent(R.id.btnSmsCode, GetSmsCode);
        this.ButtonClickEvent(R.id.btnRegister, Register);
        this.ButtonClickEvent(R.id.btnBackLogin, BackLogin);
    }

    MyEventCallback GetSmsCode = (v) ->
    {
        String phone = this.EditText(R.id.phone);
        if (this.IsNullOrEmpty(phone, "Phone")) return;

        this.Loading(R.id.loading, true);
        SharedService.RegisterApi(Constants.API_NET, sslSettings)
            .GetSmsCode(phone)
            .enqueue(Callback.callInUI(RegisterActivity.this, (json) -> {
                this.EnableEditText(R.id.smsCode);
                this.Loading(R.id.loading, false);

                ImageButton(R.id.btnSmsCode, false);
                ImageButton(R.id.btnSmsCodeX, true);
            }, (error) -> {
                String message = error.getCause().toString();
                this.Toast(message, error.body());
                this.Loading(R.id.loading, false);
            }));
    };

    MyEventCallback Register = (v ->
    {
        String fullName = this.EditText(R.id.fullName);
        String email = this.EditText(R.id.email);
        String phone = this.EditText(R.id.phone);
        String password = this.EditText(R.id.password);
        String code = this.EditText(R.id.smsCode);

        String avatar = "temp-" + phone + ".png";
        String birthday = this.EditText(R.id.birthday);
        Boolean male = this.SwitchItem(R.id.male);
        String personalId = this.EditText(R.id.personalId);
        String address = this.EditText(R.id.address);

        String plateNo = this.EditText(R.id.plateNo);
        String bikeOwner = this.EditText(R.id.bikeOwner);
        String engineNo = this.EditText(R.id.engineNo);
        String chassisNo = this.EditText(R.id.chassisNo);
        String bikeType = this.EditText(R.id.bikeType);
        String brand = this.EditText(R.id.brand);

        if (this.IsNullOrEmpty(fullName, "Full Name")) return;
        if (this.IsNullOrEmpty(email, "Email")) return;
        if (this.IsNullOrEmpty(phone, "Phone")) return;
        if (this.IsNullOrEmpty(password, "Password")) return;
        if (this.IsNullOrEmpty(code, "SMS Code")) return;

        if (!this.IsDate(birthday, "Birthday")) return;
        if (this.IsNullOrEmpty(personalId, "Personal ID")) return;
        if (this.IsNullOrEmpty(address, "Address")) return;

        if (this.IsNullOrEmpty(plateNo, "Plate No")) return;
        if (this.IsNullOrEmpty(bikeOwner, "Bike Owner")) return;
        if (this.IsNullOrEmpty(engineNo, "Engine No")) return;
        if (this.IsNullOrEmpty(chassisNo, "Chassis No")) return;
        if (this.IsNullOrEmpty(bikeType, "Bike Type")) return;
        if (this.IsNullOrEmpty(brand, "Brand")) return;

        this.Loading(R.id.loading, true);
        SharedService.RegisterApi(Constants.API_NET, sslSettings)
            .Register(fullName, email, phone, password, code,
                    avatar, birthday, male, personalId, address,
                    plateNo, bikeOwner, engineNo, chassisNo, bikeType, brand)
            .enqueue(Callback.callInUI(RegisterActivity.this, (json) -> {
                this.SetTextView(R.id.lblMessage, R.string.register_msg_active_account);
                this.Loading(R.id.loading, false);

                Button(R.id.btnRegister, false);
                Button(R.id.btnBackLogin, true);
            }, (error) -> {
                String message = error.getCause().toString();
                this.Toast(message, error.body());
                this.Loading(R.id.loading, false);
            }));
    });

    MyEventCallback BackLogin = (v -> {
        Redirect(LoginActivity.class);
    });
}
