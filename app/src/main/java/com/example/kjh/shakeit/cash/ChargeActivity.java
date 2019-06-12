package com.example.kjh.shakeit.cash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.app.AppManager;
import com.example.kjh.shakeit.cash.contract.ChargeContract;
import com.example.kjh.shakeit.cash.presenter.ChargePresenter;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Injector;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 카카오페이로 포인트를 충전하는 클래스(직접적으로 충전)
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 6. 12. PM 5:16
 **/
public class ChargeActivity extends AppCompatActivity implements ChargeContract.View {

    private final String TAG = ChargeActivity.class.getSimpleName();

    private ChargeContract.Presenter presenter;

    private Unbinder unbinder;
    @BindView(R.id.webview) WebView webView;

    private int amount;
    private User user;
    private boolean isPayment = false;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        unbinder = ButterKnife.bind(this);

        presenter = new ChargePresenter(this, Injector.provideChargeModel());

        amount = getIntent().getIntExtra("amount", 0);
        user = (User) getIntent().getSerializableExtra("user");

        /** WebView 셋팅 */
        webView.setWebViewClient(new MyWebViewClient(this));
        webView.addJavascriptInterface(new WebAppInterface(this), "android");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        presenter.onCreate();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);

        // 결제성공 후 종료 된 경우
        if(isPayment) {
            user.setCash(user.getCash() + amount);
            Events.updateProfileEvent event = new Events.updateProfileEvent(user);
            BusProvider.getInstance().post(event);
        }

        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public User getUser() {
        return user;
    }

    /**------------------------------------------------------------------
     클래스 ==> 자바 스크립트 인터페이스
     ------------------------------------------------------------------*/
    class WebAppInterface {

        Context context;

        WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void finish() {
            isPayment = true;
            ((Activity)context).finish();
        }

        @JavascriptInterface
        public void finishCancel() {
            ((Activity)context).finish();
        }

    }

    /**------------------------------------------------------------------
     클래스 ==> WebView Load Url
     ------------------------------------------------------------------*/
    class MyWebViewClient extends WebViewClient {

        private Context context;

        public MyWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = context.getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        context.startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id="+intent.getPackage()));
                        context.startActivity(marketIntent);
                    }
                    return true;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        context.startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }
    }
}



