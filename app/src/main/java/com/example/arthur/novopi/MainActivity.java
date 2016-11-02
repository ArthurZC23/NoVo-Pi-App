package com.example.arthur.novopi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.InetAddress;


public class MainActivity extends AppCompatActivity {

    private EditText addressEditText;
    private TextView addressTextView;
    private Spinner webClientSpinner;
    private Button webClientButton;
    private String novopiHostname;
    private String webClientPath;
    private Intent webIntent;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mServiceInfo;
    private String novopiIP;

    // The NSD service type that the novopi exposes.
    private static final String SERVICE_TYPE = "_workstation._tcp.";


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

        novopiIP = "";
        mNsdManager = (NsdManager)(getApplicationContext().getSystemService(Context.NSD_SERVICE));


    }

    private TextWatcher addressEditTextWatcher = new TextWatcher(){

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){}

        @Override
        public void  afterTextChanged(Editable s) {

            novopiHostname = s.toString();
            addressTextView.setText(novopiHostname);

            //Discover IP address from hostname with mDNS
            initializeResolveListener();
            initializeDiscoveryListener();
            mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

        }

        @Override
        public void  beforeTextChanged(CharSequence s, int start, int count, int after){}
    };

    public View.OnClickListener webClientButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v){

            webClientPath = "http://" + novopiIP + ":6680/" + webClientSpinner.getSelectedItem().toString();
            webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webClientPath));
            try {
                startActivity(webIntent);
            }catch (Exception e) {}

        }

    };

    private void initializeDiscoveryListener() {


        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found. See if is coming from the device selected by the user
                String name = service.getServiceName();
                String type = service.getServiceType();
                Log.d("NSD", "Service Name=" + name);
                Log.d("NSD", "Service Type=" + type);
                if (type.equals(SERVICE_TYPE) && name.contains(novopiHostname)) {
                    Log.d("NSD", "Service Found @ '" + name + "'");
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            }
        };
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e("NSD", "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                mServiceInfo = serviceInfo;
                InetAddress host = mServiceInfo.getHost();
                String address = host.getHostAddress();
                Log.d("NSD", "Resolved address = " + address);
                //Get IP address of the host
                novopiIP = address;
            }
        };
    }

}
