package com.example.autobartender.ui.layout_helpers_lol;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.autobartender.R;
import com.example.autobartender.utils.networking.NetworkStatusManager.Status;

/**
 * No No its not an activity or a fragment i swear.
 * its just a class with a dedicated layout that displays UI and handles user interaction
 * frasgment would be overkill but i didnt want to write the same code twice
 */
public class NoNetworkInfo {

    public ConstraintLayout rootView;
    public TextView tvMain;
    public TextView tvInfo;
    public Button btnTryAgain;

    public NoNetworkInfo(ConstraintLayout rootView) {
        this.rootView = rootView;
        this.tvMain = rootView.findViewById(R.id.tv_no_connection_main);
        this.tvInfo = rootView.findViewById(R.id.tv_no_connection_info);
        this.btnTryAgain = rootView.findViewById(R.id.btn_try_again);
    }

    public void init(String reason, View.OnClickListener tryAgainHandler) {
        this.tvInfo.setText(reason);
        this.btnTryAgain.setOnClickListener(tryAgainHandler);
    }

    public void hide() {
        this.rootView.setVisibility(View.GONE);
    }

    public void show() {
        this.rootView.setVisibility(View.VISIBLE);
    }
}
