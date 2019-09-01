package com.example.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eagleforcescoutingapplication.activities.fragment.AutoFragment;
import com.example.eagleforcescoutingapplication.activities.fragment.SubmitFragment;
import com.example.eagleforcescoutingapplication.activities.fragment.TeleOpFragment;
import com.example.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.example.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {
    private Activity mActivity;
    private CSVManager csvManager;
    AutoFragment autoFragment;
    TeleOpFragment teleOpFragment;
    Object teamNumber;
    private String root = Environment.getExternalStorageDirectory().getAbsolutePath();

    private Map<String, String> formMap = new HashMap<>();

    public ScoutingFormPresenter(Activity activity){
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
    }

    public void saveData(String key, String data){
        formMap.put(key, data);
    }

    public String readData(String key){
        return formMap.get(key);
    }

    public void createCSV(){
        teamNumber = getAutoData();
//                Arrays.asList(getAutoData()).get(0);
//        csvManager.createCSV(root, 0);
    }

    public void writeCSV(){
        csvManager.writeData(getAutoData());
    }

    public int incrementAmount(int amount){
        amount += 1;
        return amount;
    }

    public int decrementAmount(int amount){
        amount -= 1;
        if(amount < 0){
            amount = 0;
        }
        return amount;
    }

    public void setText(TextView textView, int amount){
        textView.setText(String.valueOf(amount));
    }

    public Object[] getTeleOpArray(){
        Object[] teleOpData = {teleOpFragment.teleopRocketCargoAmount, teleOpFragment.teleopCargoShipCargoFailAmount,
                teleOpFragment.teleopRocketHatchAmount, teleOpFragment.teleopRocketHatchFailsAmount,
                teleOpFragment.teleopCargoShipCargoAmount, teleOpFragment.teleopCargoShipCargoFailAmount,
                teleOpFragment.teleopCargoShipHatchAmount, teleOpFragment.teleopRocketHatchFailsAmount};
        return teleOpData;
    }

    public Object[] getAutoData(){

        Object[] autoData = {autoFragment.sandstormRocketCargoFailAmount,
                autoFragment.sandstormRocketHatchAmount, autoFragment.sandstormCargoShipHatchFailAmount,
                autoFragment.sandstormCargoShipCargoAmount, autoFragment.sandstormCargoShipCargoFailAmount,
                autoFragment.sandstormCargoShipHatchAmount, autoFragment.sandstormCargoShipHatchFailAmount};

//        Object[] autoData =  {0, 023, 432};
        return autoData;
    }
}
