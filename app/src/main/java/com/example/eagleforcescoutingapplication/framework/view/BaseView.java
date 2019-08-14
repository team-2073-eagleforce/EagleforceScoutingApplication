package com.example.eagleforcescoutingapplication.framework.view;

public interface BaseView {
    void updateProgressDialog(boolean isShowProgressDialog);
    void showErrorMessageDialog(String errorTitle, String errorMessage, Boolean isBackLogin);
}
