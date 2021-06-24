package com.github.kr328.clash.ucss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
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
import com.github.kr328.clash.common.ucss.http.Subscription;
import com.github.kr328.clash.common.ucss.http.TradeService;
import com.github.kr328.clash.common.ucss.http.UserApi;
import com.github.kr328.clash.common.ucss.http.UserInfo;
import com.github.kr328.clash.common.util.StringUtil;
import com.github.kr328.clash.design.store.UiStore;
import com.github.kr328.clash.design.view.UcssProgressbar;
import com.google.gson.Gson;

import java.util.List;

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
    private UcssProgressbar progressBar;

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

        findViewById(R.id.tv_forget).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://my.undercurrentss.net/index.php?rp=/password/reset/begin"));
            startActivity(browserIntent);
        });
        findViewById(R.id.tv_sign).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://my.undercurrentss.net/cart.php"));
            startActivity(browserIntent);
        });
//        tvName.setText("hello@undercurrentss.com");
//        tvPass.setText("123234!");
    }

    private void setView(boolean login) {
        if (login) {
            tvError.setText("");
            btnLogin.setText("");
            progressBar.setStart(true);
        } else {
            btnLogin.setText(R.string.login);
            progressBar.setStart(false);
        }
    }

    private void login(String name, final String password) {
        setView(true);
        Global.INSTANCE.getUser().token = StringUtil.base64(name + ":" + password);
        Global.INSTANCE.getUser().email = name;
        UserApi api = Api.createReq(UserApi.class);
        api.login()
                .subscribeOn(Schedulers.io())
                .flatMap((Function<BaseResponse<UserInfo>, ObservableSource<BaseResponse<List<TradeService>>>>) userInfoBaseResponse -> {
                    if (!userInfoBaseResponse.isOk()) {
                        throw new RuntimeException(userInfoBaseResponse.message);
                    }
                    Global.INSTANCE.getUser().userid = userInfoBaseResponse.data.userid;
                    return api.userService(userInfoBaseResponse.data.userid);
                })
                .flatMap((Function<BaseResponse<List<TradeService>>, ObservableSource<BaseResponse<Subscription>>>) listBaseResponse -> {
                    if (listBaseResponse.isOk() && listBaseResponse.data.size() > 0) {
                        Global.INSTANCE.getUser().serviceId = listBaseResponse.data.get(0).serviceid;
                    } else {
                        throw new RuntimeException("No valid service");
                    }
                    return api.subscription(Global.INSTANCE.getUser().serviceId);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<Subscription>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse<Subscription> res) {
                        setView(false);
                        if (res.isOk()) {
                            UiStore store = new UiStore(LoginActivity.this);
                            Global.INSTANCE.getUser().subUri = res.data.url;
                            Gson gson = new Gson();
                            store.setUserInfo(gson.toJson(Global.INSTANCE.getUser()));
                            start();
                        } else {
                            throw new RuntimeException(res.message);
                        }
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
