package com.team2073.eagleforcescoutingapplication.framework.view;

public interface BaseView {
    void updateProgressDialog(boolean isShowProgressDialog);
    void showErrorMessageDialog(String errorTitle, String errorMessage, Boolean isBackLogin);
}
