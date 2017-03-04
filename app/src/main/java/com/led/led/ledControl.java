package com.led.led;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;


public class ledControl extends ActionBarActivity implements View.OnClickListener {

    ImageButton btnOn1, btnOn2, btnOn3, btnOn4, btnOn5, btnOn6;
    Button btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    //static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final UUID myUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn1 = (ImageButton)findViewById(R.id.imageButton1);
        btnOn2 = (ImageButton)findViewById(R.id.imageButton2);
        btnOn3 = (ImageButton)findViewById(R.id.imageButton3);
        btnOn4 = (ImageButton)findViewById(R.id.imageButton4);
        btnOn5 = (ImageButton)findViewById(R.id.imageButton5);
        btnOn6 = (ImageButton)findViewById(R.id.imageButton6);

        btnDis = (Button)findViewById(R.id.button7);


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnOn1.setOnClickListener(this);
        btnOn2.setOnClickListener(this);
        btnOn3.setOnClickListener(this);
        btnOn4.setOnClickListener(this);
        btnOn5.setOnClickListener(this);
        btnOn6.setOnClickListener(this);
        btnDis.setOnClickListener(this);
    }

    private void disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageButton1:
                turnOn("1");
                break;
            case R.id.imageButton2:
                turnOn("2");
                break;
            case R.id.imageButton3:
                turnOn("3");
                break;
            case R.id.imageButton4:
                turnOn("4");
                break;
            case R.id.imageButton5:
                turnOn("5");
                break;
            case R.id.imageButton6:
                turnOn("6");
                break;
            case R.id.button7:
                disconnect();
                break;
        }
    }

    private void turnOn(String number) {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(number.getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
