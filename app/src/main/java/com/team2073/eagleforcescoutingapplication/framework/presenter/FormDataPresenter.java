package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.view.FormDataView;

import java.util.HashMap;
import java.util.Map;

public class FormDataPresenter extends BasePresenter<FormDataView> {
    private Activity mActivity;
    private CSVManager cvsManager;

    private Map<String, String> formMap = new HashMap<>();
}
