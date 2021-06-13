package com.github.kr328.clash.ucss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kr328.clash.HomeActivity;
import com.github.kr328.clash.MainApplication;
import com.github.kr328.clash.R;
import com.github.kr328.clash.common.Global;
import com.github.kr328.clash.common.ucss.http.Api;
import com.github.kr328.clash.common.ucss.http.BaseResponse;
import com.github.kr328.clash.common.ucss.http.UserInfo;
import com.github.kr328.clash.common.ucss.http.UserService;
import com.github.kr328.clash.common.util.StringUtil;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author shangji_cd
 */
public class LoginActivity extends AppCompatActivity {
    private EditText tvName;
    private EditText tvPass;
    private TextView tvError;
    private TextView btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvName = findViewById(R.id.username);
        tvPass = findViewById(R.id.password);
        tvError = findViewById(R.id.tv_error);
        btnLogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);

        findViewById(R.id.login).setOnClickListener(v -> {
            String name = tvName.getText().toString();
            if (name.isEmpty()) {
                showError(R.string.emptyName);
                return;
            }
            String password = tvPass.getText().toString();
            if (password.isEmpty()) {
                showError(R.string.emptyPass);
                return;
            }
            login(name, password);
        });
        tvName.setText("hello@undercurrentss.com");
        tvPass.setText("123234!");

        start();
    }

    private void setView(boolean login) {
        if (login) {
            tvError.setText("");
            btnLogin.setText("");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setText(R.string.login);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void login(String name, final String password) {
        setView(true);
        UserService api = Api.createReq(UserService.class);
        api.login(StringUtil.base64(name + ":" + password))
                .subscribeOn(Schedulers.io())
                .flatMap((Function<BaseResponse<UserInfo>, ObservableSource<BaseResponse<UserInfo>>>) userInfoBaseResponse -> {
                    if (!userInfoBaseResponse.isOk()) {
                        throw new RuntimeException(userInfoBaseResponse.message);
                    }
                    return api.userInfo(userInfoBaseResponse.data.userId);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse<UserInfo> userInfoBaseResponse) {
                        setView(false);
                        start();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        tvError.setText(e.getMessage());
                        setView(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void showError(int res) {
        tvError.setText(res);
    }

    private void start() {
        ((MainApplication) Global.INSTANCE.getApplication()).start();
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }
}
