package com.example.kjh.shakeit.cash.presenter;

import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.cash.contract.ChargeContract;

import org.json.JSONException;
import org.json.JSONObject;

public class ChargePresenter implements ChargeContract.Presenter {

    private ChargeContract.View view;
    private ChargeContract.Model model;

    public ChargePresenter(ChargeContract.View view, ChargeContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        // 카카오페이 결제 준비
        model.chargeReady(view.getUser().getUserId(), view.getAmount(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                String url = "";
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    url = jsonObject.getString("next_redirect_app_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.loadUrl(url);
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }

}
