package com.team2073.eagleforcescoutingapplication.activities.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

public class AutoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private PageViewModel pageViewModel;
    ScoutingFormPresenter presenter;
    private CheckBox passBaseLineCheckBox;
    private String[] arrayList;

    public int teamNumber = 0;

    //Declare variables sandstorm cargo in rocket
    View sandstormRocketCargoLayoutButtonView;
    TextView sandstormRocketCargoText;
    ImageButton addRocketCargoAmountButton;
    ImageButton subtractRocketCargoAmountButton;
    public int sandstormRocketCargoAmount = 0;

    //Declare variables sandstorm cargo fails - rocket
    View sandstormRocketCargoFailsLayoutButtonView;
    TextView sandstormRocketCargoFailsText;
    ImageButton addRocketFailCargoAmountButton;
    ImageButton subtractRocketFailCargoAmountButton;
    public int sandstormRocketCargoFailAmount = 0;

    //Declare variables sandstorm hatches in rocket
    View sandstormRocketHatchesLayoutButtonView;
    TextView sandstormRocketHatchesText;
    ImageButton addRocketHatchesAmountButton;
    ImageButton subtractRocketHatchAmountButton;
    public int sandstormRocketHatchAmount = 0;

    //Declare variables sandstorm hatches fails - rocket
    View sandstormRocketHatchesFailsLayoutButtonView;
    TextView sandstormRocketHatchesFailsText;
    ImageButton addRocketHatchesFailAmountButton;
    ImageButton subtractRocketHatchFailAmountButton;
    public int sandstormRocketHatchFailsAmount = 0;

    //Declare variables sandstorm cargo in cargoship
    View sandstormCargoShipCargoLayoutButtonView;
    TextView sandstormCargoShipCargoText;
    ImageButton addCargoshipCargoAmountButton;
    ImageButton subtractCargoshipCargoAmountButton;
    public int sandstormCargoShipCargoAmount = 0;

    //Declare variables sandstorm cargo fails - cargoship
    View sandstormCargoShipCargoFailsLayoutButtonView;
    TextView sandstormCargoShipCargoFailsText;
    ImageButton addCargoshipCargoFailAmountButton;
    ImageButton subtractCargoshipCargoFailAmountButton;
    public int sandstormCargoShipCargoFailAmount = 0;

    //Declare variables sandstorm hatches in Cargo Ship
    View sandstormCargoShipHatchesLayoutButtonView;
    TextView sandstormCargoShipHatchesText;
    ImageButton addCargoshipHatchesAmountButton;
    ImageButton subtractCargoshipHatchAmountButton;
    public int sandstormCargoShipHatchAmount = 0;

    //Declare variables sandstorm hatches fails - cargoship
    View sandstormCargoShipHatchesFailsLayoutButtonView;
    TextView sandstormCargoShipHatchesFailsText;
    ImageButton addCargoshipHatchesFailAmountButton;
    ImageButton subtractCargoshipHatchFailAmountButton;
    public int sandstormCargoShipHatchFailAmount = 0;


    public static AutoFragment newInstance(int index){
        AutoFragment fragment = new AutoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        presenter = new ScoutingFormPresenter(this.getActivity());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scouting_form_auto, container, false);

        passBaseLineCheckBox = root.findViewById(R.id.sandstorm_cross_CheckBox);

        //Sandstorm cargo on Rocket
        sandstormRocketCargoLayoutButtonView = root.findViewById(R.id.teleop_rocket_cargo_layout);
        sandstormRocketCargoText = sandstormRocketCargoLayoutButtonView.findViewById(R.id.relEditText);
        addRocketCargoAmountButton = sandstormRocketCargoLayoutButtonView.findViewById(R.id.add);
        subtractRocketCargoAmountButton = sandstormRocketCargoLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on Rocket fails
        sandstormRocketCargoFailsLayoutButtonView = root.findViewById(R.id.teleop_rocket_cargo_fails_layout);
        sandstormRocketCargoFailsText = sandstormRocketCargoFailsLayoutButtonView.findViewById(R.id.relEditText);
        addRocketFailCargoAmountButton = sandstormRocketCargoFailsLayoutButtonView.findViewById(R.id.add);
        subtractRocketFailCargoAmountButton = sandstormRocketCargoFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Rocket
        sandstormRocketHatchesLayoutButtonView = root.findViewById(R.id.teleop_rocket_hatches_layout);
        sandstormRocketHatchesText = sandstormRocketHatchesLayoutButtonView.findViewById(R.id.relEditText);
        addRocketHatchesAmountButton = sandstormRocketHatchesLayoutButtonView.findViewById(R.id.add);
        subtractRocketHatchAmountButton = sandstormRocketHatchesLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Rocket fails
        sandstormRocketHatchesFailsLayoutButtonView = root.findViewById(R.id.teleop_rocket_hatches_fail_layout);
        sandstormRocketHatchesFailsText = sandstormRocketHatchesFailsLayoutButtonView.findViewById(R.id.relEditText);
        addRocketHatchesFailAmountButton = sandstormRocketHatchesFailsLayoutButtonView.findViewById(R.id.add);
        subtractRocketHatchFailAmountButton = sandstormRocketHatchesFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on cargoship
        sandstormCargoShipCargoLayoutButtonView = root.findViewById(R.id.teleop_cargoship_cargo_layout);
        sandstormCargoShipCargoText = sandstormCargoShipCargoLayoutButtonView.findViewById(R.id.relEditText);
        addCargoshipCargoAmountButton = sandstormCargoShipCargoLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipCargoAmountButton = sandstormCargoShipCargoLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm cargo on cargoship fails
        sandstormCargoShipCargoFailsLayoutButtonView = root.findViewById(R.id.teleop_cargoship_cargo_fails_layout);
        sandstormCargoShipCargoFailsText = sandstormCargoShipCargoFailsLayoutButtonView.findViewById(R.id.relEditText);
        addCargoshipCargoFailAmountButton = sandstormCargoShipCargoFailsLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipCargoFailAmountButton = sandstormCargoShipCargoFailsLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Cargo Ship
        sandstormCargoShipHatchesLayoutButtonView = root.findViewById(R.id.teleop_cargoship_hatches_layout);
        sandstormCargoShipHatchesText = sandstormCargoShipHatchesLayoutButtonView.findViewById(R.id.relEditText);
        addCargoshipHatchesAmountButton = sandstormCargoShipHatchesLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipHatchAmountButton = sandstormCargoShipHatchesLayoutButtonView.findViewById(R.id.subtract);

        //Sandstorm hatches on Cargo Ship fails
        sandstormCargoShipHatchesFailsLayoutButtonView = root.findViewById(R.id.teleop_cargoship_hatches_fail_layout);
        sandstormCargoShipHatchesFailsText = sandstormCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.relEditText);
        addCargoshipHatchesFailAmountButton = sandstormCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.add);
        subtractCargoshipHatchFailAmountButton = sandstormCargoShipHatchesFailsLayoutButtonView.findViewById(R.id.subtract);


        //Button Click Listeners
        addRocketCargoAmountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                sandstormRocketCargoAmount = presenter.incrementAmount(sandstormRocketCargoAmount);
                presenter.setText(sandstormRocketCargoText, sandstormRocketCargoAmount);
            }
        });

        subtractRocketCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketCargoAmount = presenter.decrementAmount(sandstormRocketCargoAmount);
                presenter.setText(sandstormRocketCargoText, sandstormRocketCargoAmount);
            }
        });

        addRocketFailCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketCargoFailAmount = presenter.incrementAmount(sandstormRocketCargoFailAmount);
                presenter.setText(sandstormRocketCargoFailsText, sandstormRocketCargoFailAmount);
            }
        });

        subtractRocketFailCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketCargoFailAmount = presenter.decrementAmount(sandstormRocketCargoFailAmount);
                presenter.setText(sandstormRocketCargoFailsText, sandstormRocketCargoFailAmount);
            }
        });

        addRocketHatchesAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketHatchAmount = presenter.incrementAmount(sandstormRocketHatchAmount);
                presenter.setText(sandstormRocketHatchesText, sandstormRocketHatchAmount);
            }
        });

        subtractRocketHatchAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketHatchAmount = presenter.decrementAmount(sandstormRocketHatchAmount);
                presenter.setText(sandstormRocketHatchesText, sandstormRocketHatchAmount);
            }
        });

        addRocketHatchesFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketHatchFailsAmount = presenter.incrementAmount(sandstormRocketHatchFailsAmount);
                presenter.setText(sandstormRocketHatchesFailsText, sandstormRocketHatchFailsAmount);
            }
        });

        subtractRocketHatchFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormRocketHatchFailsAmount = presenter.decrementAmount(sandstormRocketHatchFailsAmount);
                presenter.setText(sandstormRocketHatchesFailsText, sandstormRocketHatchFailsAmount);
            }
        });

        addCargoshipCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipCargoAmount = presenter.incrementAmount(sandstormCargoShipCargoAmount);
                presenter.setText(sandstormCargoShipCargoText,sandstormCargoShipCargoAmount );
            }
        });

        subtractCargoshipCargoAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipCargoAmount = presenter.decrementAmount(sandstormCargoShipCargoAmount);
                presenter.setText(sandstormCargoShipCargoText, sandstormCargoShipCargoAmount);
            }
        });

        addCargoshipCargoFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipCargoFailAmount = presenter.incrementAmount(sandstormCargoShipCargoFailAmount);
                presenter.setText(sandstormCargoShipCargoFailsText,sandstormCargoShipCargoFailAmount );
            }
        });

        subtractCargoshipCargoFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipCargoFailAmount = presenter.decrementAmount(sandstormCargoShipCargoFailAmount);
                presenter.setText(sandstormCargoShipCargoFailsText,sandstormCargoShipCargoFailAmount );
            }
        });

        addCargoshipHatchesAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipHatchAmount = presenter.incrementAmount(sandstormCargoShipHatchAmount);
                presenter.setText(sandstormCargoShipHatchesText,sandstormCargoShipHatchAmount );
            }
        });

        subtractCargoshipHatchAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipHatchAmount =  presenter.decrementAmount(sandstormCargoShipHatchAmount);
                presenter.setText(sandstormCargoShipHatchesText,sandstormCargoShipHatchAmount );
            }
        });

        addCargoshipHatchesFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipHatchFailAmount =  presenter.incrementAmount(sandstormCargoShipHatchFailAmount);
                presenter.setText(sandstormCargoShipHatchesFailsText,sandstormCargoShipHatchFailAmount );
            }
        });

        subtractCargoshipHatchFailAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sandstormCargoShipHatchFailAmount = presenter.decrementAmount(sandstormCargoShipHatchFailAmount);
                presenter.setText(sandstormCargoShipHatchesFailsText,sandstormCargoShipHatchFailAmount );
            }
        });

        return root;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    @Override
    public void onClick(View view) {

    }
}
