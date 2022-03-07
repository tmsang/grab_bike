package com.intec.grab.bike_driver.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.widget.Toolbar;

import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.utils.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    
    RadioButton englishLanguage, vietNamLanguage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Initialization(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_menu:
                this.Redirect(MainActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
