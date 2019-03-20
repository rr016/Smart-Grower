package com.example.android.smartgrower;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    Button onOff,
           upload,
           run;
    TextView textView;
    EditText daysEditText,
             onEditText,
             offEditText;
    boolean isTorchOn = false;

    String address = null,
           name = null,
            days,
            onTime,
            offTime;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daysEditText = findViewById(R.id.days_text);
        onEditText = findViewById(R.id.on_text);
        offEditText = findViewById(R.id.off_text);

        try {
            setup();
        }
        catch (Exception e) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setup() throws IOException {
        textView = (TextView)findViewById(R.id.address_text);
        connectToBT();

        upload = (Button)findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = daysEditText.getText().toString();
                onTime = onEditText.getText().toString();
                offTime = offEditText.getText().toString();

                sendToBT("u");

                for(int i=0; i<days.length(); i++)
                    sendToBT(String.valueOf(days.charAt(i)));
                sendToBT(",");

                for(int i=0; i<onTime.length(); i++)
                    sendToBT(String.valueOf(onTime.charAt(i)));
                sendToBT(",");

                for(int i=0; i<offTime.length(); i++)
                    sendToBT(String.valueOf(offTime.charAt(i)));

          //      sendToBT(days);
          //      sendToBT(onTime);
          //      sendToBT(offTime);
                sendToBT("#");
            }
        });

        run = (Button)findViewById(R.id.run_button);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToBT("r");
            }
        });

        onOff = (Button)findViewById(R.id.on_off_button);
        onOff.setOnClickListener(new View.OnClickListener() { @Override
        public void onClick(View v) {
            if(!isTorchOn) {
                sendToBT("n"); // LED ON
                isTorchOn = true;
            }
            else{
                sendToBT("f"); // LED OFF
                isTorchOn = false;
            }
        }
        });
    }

    // Connects BlueTooth device with the app
    private void connectToBT() throws IOException {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for(BluetoothDevice bt : pairedDevices) {
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
        catch(Exception e){}

        // Get mobile BlueTooth device
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        // Connects to device's address and checks if it's available
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);

        // Create a RFCOMM (SPP) connection
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
        btSocket.connect();

        try {
            textView.setText("BT Name: "+name+"\nBT Address: "+address);
        }
        catch(Exception e){}
    }

    @Override
    public void onClick(View v) {
        try {}
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Sends output to BlueTooth through created socket
    private void sendToBT(String output) {
        try {
            if (btSocket != null) {
                btSocket.getOutputStream().write(output.toString().getBytes());
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}