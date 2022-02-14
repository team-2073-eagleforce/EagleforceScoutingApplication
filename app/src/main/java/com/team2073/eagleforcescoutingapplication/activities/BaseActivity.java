package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.view.BaseView;

/**
 * Includes base elements/methods for other activity classes to inherit.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResourceId() != 0) {
            setContentView(getLayoutResourceId());
        }
        initView();
        bindView();
        initEvent();
    }

    @Override
    public void setContentView(int resId) {
        LinearLayout screenRootView = new LinearLayout(this);
        screenRootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        screenRootView.setOrientation(LinearLayout.VERTICAL);
        screenRootView.setBackgroundResource(R.color.colorPrimary);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screenView = inflater.inflate(resId, null);
        screenRootView.addView(screenView);

        super.setContentView(screenRootView);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveOutStateBundle(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreSavedState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void saveOutStateBundle(Bundle outState) {

    }

    protected void restoreSavedState(Bundle savedInstanceState) {

    }

    /**
     * Layout resource id for activity.
     *
     * @return Resource id
     */
    @LayoutRes
    protected abstract int getLayoutResourceId();

    /**
     * Initial event for views.
     */
    protected abstract void initEvent();

    /**
     * Initial all child views to display.
     */
    protected abstract void initView();

    /**
     * Inject view with presenter.
     */
    protected abstract void bindView();

}
