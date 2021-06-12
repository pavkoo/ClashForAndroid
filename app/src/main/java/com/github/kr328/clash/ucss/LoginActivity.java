package com.github.kr328.clash.ucss;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kr328.clash.HomeActivity;
import com.github.kr328.clash.MainApplication;
import com.github.kr328.clash.R;
import com.github.kr328.clash.common.Global;

/**
 * @author shangji_cd
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        findViewById(R.id.login).setOnClickListener(v -> {
            extracted(v);
        });

        extracted(null);
    }

    private void extracted(android.view.View v) {
        ((MainApplication) Global.INSTANCE.getApplication()).start();
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }
}
