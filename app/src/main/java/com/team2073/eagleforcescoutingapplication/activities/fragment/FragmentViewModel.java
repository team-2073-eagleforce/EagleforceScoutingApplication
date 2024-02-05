package com.team2073.eagleforcescoutingapplication.activities.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FragmentViewModel extends ViewModel {
    private final MutableLiveData<Integer> trapNumber = new MutableLiveData<>();

    public void setTrapNumber(int trapNumber) {
        this.trapNumber.setValue(trapNumber);
    }

    public MutableLiveData<Integer> getTrapNumber() {
        return trapNumber;
    }
}
