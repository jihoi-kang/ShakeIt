package com.example.kjh.shakeit.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kjh.shakeit.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TabMoreFragment extends Fragment {

    private static TabMoreFragment instance = null;

    private Unbinder unbinder;

    public static TabMoreFragment getInstance() {
        if(instance == null)
            instance = new TabMoreFragment();

        return instance;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

