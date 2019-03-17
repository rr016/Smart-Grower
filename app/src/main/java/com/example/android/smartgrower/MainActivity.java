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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    Button button;
    TextView textView;

    String address = null,
           name = null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            setup();
        }
        catch (Exception e) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setup() throws IOException {
        textView = (TextView)findViewById(R.id.textView1);
        connectToBT();

        button = (Button)findViewById(R.id.button1);
        button.setOnTouchListener(new View.OnTouchListener() {@Override
        public boolean onTouch(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                sendToBT("f"); // LED ON
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                sendToBT("b"); // LED OFF
            }
            return true;
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
                    address = bt.getAddress().toString();name = bt.getName().toString();
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