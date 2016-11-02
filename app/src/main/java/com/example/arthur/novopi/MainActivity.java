package com.example.arthur.novopi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    private EditText addressEditText;
    private TextView addressTextView;
    private Spinner webClientSpinner;
    private Button webClientButton;
    private String novopiIPAdress;
    private String webClientPath;
    private Intent webIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get layout references
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        webClientSpinner = (Spinner) findViewById(R.id.webClientSpinner);
        webClientButton = (Button)  findViewById(R.id.webClientButton);


        //ArrayAdpter for spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.web_clients, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        webClientSpinner.setAdapter(spinnerAdapter);

        //Event Listeners
        addressEditText.addTextChangedListener(addressEditTextWatcher);
        webClientButton.setOnClickListener(webClientButtonListener);
    }

    private TextWatcher addressEditTextWatcher = new TextWatcher(){

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){}

        @Override
        public void  afterTextChanged(Editable s) {

            novopiIPAdress = s.toString();
            addressTextView.setText(novopiIPAdress);

        }

        @Override
        public void  beforeTextChanged(CharSequence s, int start, int count, int after){}
    };

    public View.OnClickListener webClientButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v){

            webClientPath = "http://" + novopiIPAdress + ":6680/" + webClientSpinner.getSelectedItem().toString();
            webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webClientPath));
            try {
                startActivity(webIntent);
            }catch (Exception e) {}

        }

    };
}
