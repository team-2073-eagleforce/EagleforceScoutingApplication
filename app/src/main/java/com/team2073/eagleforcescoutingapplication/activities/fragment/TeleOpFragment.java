package com.team2073.eagleforcescoutingapplication.activities.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.R;

public class TeleOpFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private PageViewModel pageViewModel;
    ScoutingFormPresenter presenter;

    //Declare variables sandstorm cargo in rocket
    View teleopRocketCargoLayoutButtonView;
    TextView teleopRocketCargoText;
    ImageButton addRocketCargoAmountButton;
    ImageButton subtractRocketCargoAmountButton;
    public int teleopRocketCargoAmount = 0;

    //Declare variables sandstorm cargo fails - rocket
    View teleopRocketCargoFailsLayoutButtonView;
    TextView teleopRocketCargoFailsText;
    ImageButton addRocketFailCargoAmountButton;
    ImageButton subtractRocketFailCargoAmountButton;
    public int teleopRocketCargoFailAmount = 0;

    //Declare variables sandstorm hatches in rocket
    View teleopRocketHatchesLayoutButtonView;
    TextView teleopRocketHatchesText;
    ImageButton addRocketHatchesAmountButton;
    ImageButton subtractRocketHatchAmountButton;
    public int teleopRocketHatchAmount = 0;

    //Declare variables sandstorm hatches fails - rocket
    View teleopRocketHatchesFailsLayoutButtonView;
    TextView teleopRocketHatchesFailsText;
    ImageButton addRocketHatchesFailAmountButton;
    ImageButton subtractRocketHatchFailAmountButton;
    public int teleopRocketHatchFailsAmount = 0;

    //Declare variables sandstorm cargo in cargoship
    View teleopCargoShipCargoLayoutButtonView;
    TextView teleopCargoShipCargoText;
    ImageButton addCargoshipCargoAmountButton;
    ImageButton subtractCargoshipCargoAmountButton;
    public int teleopCargoShipCargoAmount = 0;

    //Declare variables sandstorm cargo fails - cargoship
    View teleopCargoShipCargoFailsLayoutButtonView;
    TextView teleopCargoShipCargoFailsText;
    ImageButton addCargoshipCargoFailAmountButton;
    ImageButton subtractCargoshipCargoFailAmountButton;
    public int teleopCargoShipCargoFailAmount = 0;

    //Declare variables sandstorm hatches in Cargo Ship
    View teleopCargoShipHatchesLayoutButtonView;
    TextView teleopCargoShipHatchesText;
    ImageButton addCargoshipHatchesAmountButton;
    ImageButton subtractCargoshipHatchAmountButton;
    public int teleopCargoShipHatchAmount = 0;

    //Declare variables sandstorm hatches fails - cargoship
    View teleopCargoShipHatchesFailsLayoutButtonView;
    TextView teleopCargoShipHatchesFailsText;
    ImageButton addCargoshipHatchesFailAmountButton;
    ImageButton subtractCargoshipHatchFailAmountButton;
    public int teleopCargoShipHatchFailAmount = 0;

    public static TeleOpFragment newInstance(int index){
        TeleOpFragment fragment = new TeleOpFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        presenter = new ScoutingFormPresenter(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scouting_form_teleop, container, false);

        //Sandstorm cargo on Rocket
        teleopRocketCargoLayoutButtonView = root.findViewById(R.id.teleop_rocket_cargo_layout);
        teleopRocketCargoText = teleopRocketCargoLayoutButtonView.findViewById(R.id.amount_TextView);
        addRocketCargoAmountButton = teleopRocketCargoLayoutButtonView.findViewById(R.id.add);
        subtractRocketCargoAmountButton = teleopRocketCargoLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on Rocket fails
        teleopRocketCargoFailsLayoutButtonView = root.findViewById(R.id.teleop_rocket_cargo_fails_layout);
        teleopRocketCargoFailsText = teleopRocketCargoFailsLayoutButtonView.findViewById(R.id.amount_TextView);
        addRocketFailCargoAmountButton = teleopRocketCargoFailsLayoutButtonView.findViewById(R.id.add);
        subtractRocketFailCargoAmountButton = teleopRocketCargoFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Rocket
        teleopRocketHatchesLayoutButtonView = root.findViewById(R.id.teleop_rocket_hatches_layout);
        teleopRocketHatchesText = teleopRocketHatchesLayoutButtonView.findViewById(R.id.amount_TextView);
        addRocketHatchesAmountButton = teleopRocketHatchesLayoutButtonView.findViewById(R.id.add);
        subtractRocketHatchAmountButton = teleopRocketHatchesLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Rocket fails
        teleopRocketHatchesFailsLayoutButtonView = root.findViewById(R.id.teleop_rocket_hatches_fail_layout);
        teleopRocketHatchesFailsText = teleopRocketHatchesFailsLayoutButtonView.findViewById(R.id.amount_TextView);
        addRocketHatchesFailAmountButton = teleopRocketHatchesFailsLayoutButtonView.findViewById(R.id.add);
        subtractRocketHatchFailAmountButton = teleopRocketHatchesFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on cargoship
        teleopCargoShipCargoLayoutButtonView = root.findViewById(R.id.teleop_cargoship_cargo_layout);
        teleopCargoShipCargoText = teleopCargoShipCargoLayoutButtonView.findViewById(R.id.amount_TextView);
        addCargoshipCargoAmountButton = teleopCargoShipCargoLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipCargoAmountButton = teleopCargoShipCargoLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on cargoship fails
        teleopCargoShipCargoFailsLayoutButtonView = root.findViewById(R.id.teleop_cargoship_cargo_fails_layout);
        teleopCargoShipCargoFailsText = teleopCargoShipCargoFailsLayoutButtonView.findViewById(R.id.amount_TextView);
        addCargoshipCargoFailAmountButton = teleopCargoShipCargoFailsLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipCargoFailAmountButton = teleopCargoShipCargoFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Cargo Ship
        teleopCargoShipHatchesLayoutButtonView = root.findViewById(R.id.teleop_cargoship_hatches_layout);
        teleopCargoShipHatchesText = teleopCargoShipHatchesLayoutButtonView.findViewById(R.id.amount_TextView);
        addCargoshipHatchesAmountButton = teleopCargoShipHatchesLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipHatchAmountButton = teleopCargoShipHatchesLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Cargo Ship fails
        teleopCargoShipHatchesFailsLayoutButtonView = root.findViewById(R.id.teleop_cargoship_hatches_fail_layout);
        teleopCargoShipHatchesFailsText = teleopCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.amount_TextView);
        addCargoshipHatchesFailAmountButton = teleopCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipHatchFailAmountButton = teleopCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.subtract);


        //Button Click Listeners
        addRocketCargoAmountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                teleopRocketCargoAmount = presenter.incrementAmount(teleopRocketCargoAmount);
                presenter.setText(teleopRocketCargoText, teleopRocketCargoAmount);
            }
        });

        subtractRocketCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketCargoAmount = presenter.decrementAmount(teleopRocketCargoAmount);
                presenter.setText(teleopRocketCargoText, teleopRocketCargoAmount);
            }
        });

        addRocketFailCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketCargoFailAmount = presenter.incrementAmount(teleopRocketCargoFailAmount);
                presenter.setText(teleopRocketCargoFailsText, teleopRocketCargoFailAmount);
            }
        });

        subtractRocketFailCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketCargoFailAmount = presenter.decrementAmount(teleopRocketCargoFailAmount);
                presenter.setText(teleopRocketCargoFailsText, teleopRocketCargoFailAmount);
            }
        });

        addRocketHatchesAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketHatchAmount = presenter.incrementAmount(teleopRocketHatchAmount);
                presenter.setText(teleopRocketHatchesText, teleopRocketHatchAmount);
            }
        });

        subtractRocketHatchAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketHatchAmount = presenter.decrementAmount(teleopRocketHatchAmount);
                presenter.setText(teleopRocketHatchesText, teleopRocketHatchAmount);
            }
        });

        addRocketHatchesFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketHatchFailsAmount = presenter.incrementAmount(teleopRocketHatchFailsAmount);
                presenter.setText(teleopRocketHatchesFailsText, teleopRocketHatchFailsAmount);
            }
        });

        subtractRocketHatchFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopRocketHatchFailsAmount = presenter.decrementAmount(teleopRocketHatchFailsAmount);
                presenter.setText(teleopRocketHatchesFailsText, teleopRocketHatchFailsAmount);
            }
        });

        addCargoshipCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipCargoAmount = presenter.incrementAmount(teleopCargoShipCargoAmount);
                presenter.setText(teleopCargoShipCargoText, teleopCargoShipCargoAmount);
            }
        });

        subtractCargoshipCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipCargoAmount = presenter.decrementAmount(teleopCargoShipCargoAmount);
                presenter.setText(teleopCargoShipCargoText, teleopCargoShipCargoAmount);
            }
        });

        addCargoshipCargoFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipCargoFailAmount = presenter.incrementAmount(teleopCargoShipCargoFailAmount);
                presenter.setText(teleopCargoShipCargoFailsText, teleopCargoShipCargoFailAmount);
            }
        });

        subtractCargoshipCargoFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipCargoFailAmount = presenter.decrementAmount(teleopCargoShipCargoFailAmount);
                presenter.setText(teleopCargoShipCargoFailsText, teleopCargoShipCargoFailAmount);
            }
        });

        addCargoshipHatchesAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipHatchAmount = presenter.incrementAmount(teleopCargoShipHatchAmount);
                presenter.setText(teleopCargoShipHatchesText, teleopCargoShipHatchAmount);
            }
        });

        subtractCargoshipHatchAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipHatchAmount =  presenter.decrementAmount(teleopCargoShipHatchAmount);
                presenter.setText(teleopCargoShipHatchesText, teleopCargoShipHatchAmount);
            }
        });

        addCargoshipHatchesFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipHatchFailAmount =  presenter.incrementAmount(teleopCargoShipHatchFailAmount);
                presenter.setText(teleopCargoShipHatchesFailsText, teleopCargoShipHatchFailAmount);
            }
        });

        subtractCargoshipHatchFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teleopCargoShipHatchFailAmount = presenter.decrementAmount(teleopCargoShipHatchFailAmount);
                presenter.setText(teleopCargoShipHatchesFailsText, teleopCargoShipHatchFailAmount);
            }
        });

        return root;

    }
}
