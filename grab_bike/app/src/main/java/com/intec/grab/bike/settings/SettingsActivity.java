package com.intec.grab.bike.settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.intec.grab.bike.R;
import com.intec.grab.bike.utils.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    
    RadioButton englishLanguage, vietNamLanguage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Initialization(this);
        
        englishLanguage = findViewById(R.id.english_radio);
        vietNamLanguage = findViewById(R.id.vietnam_radio);

        englishLanguage.setOnCheckedChangeListener(listenerRadio);
        vietNamLanguage.setOnCheckedChangeListener(listenerRadio);
    }

    CompoundButton.OnCheckedChangeListener listenerRadio = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                String txt = buttonView.getText().toString();   // "English" | "Viet Nam"
                // TODO: change language here...
            }
        }
    };
}
