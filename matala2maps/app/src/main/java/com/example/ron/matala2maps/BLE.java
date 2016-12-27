package com.example.ron.matala2maps;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;


public class BLE extends MainActivity {

    Button btn_get, btnDis;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mBLE = mRootRef.child("BLE");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the BLE
        setContentView(R.layout.activity_ble);

        //call the widgtes
        btn_get = (Button)findViewById(R.id.button2);
        btnDis = (Button)findViewById(R.id.button4);

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btn_get.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getLocation();      //method to turn on
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
    }

    private void Disconnect()
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

    private void getLocation() {
        if (btSocket != null) {
            try {   //BLE Device that gives cordinates in inputstream
                btSocket.connect();//start connection
                DataInputStream mmInStream = new DataInputStream(btSocket.getInputStream());        //get input
                String bleMessage=mmInStream.readUTF();
                String[] split=bleMessage.split(",");
                double latitude = Double.parseDouble(split[0]);
                double longitude = Double.parseDouble(split[1]);
                UserLocation ul = new UserLocation(latitude, longitude);
                currentUser.setLocation(ul);
                mUserRef.child(userKey).setValue(currentUser);
                Toast.makeText(this, bleMessage, Toast.LENGTH_LONG).show();
                lumn.setText(bleMessage);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent anythingintent=new Intent(BLE.this,MyLocationDemoActivity.class);
                Bundle b=new Bundle();
                b.putDouble("latitude",latitude);
                b.putDouble("longitude",longitude);
                anythingintent.putExtras(b);
                startActivity(anythingintent);
            }
            catch (Exception e) {
                    connectBleDevice();         //if can not get BLE input stream location, get from database the location
            }
        }
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BLE.this, "Connecting...", "Please wait!!!");  //show a progress dialog
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

    //version 2
    private void connectBleDevice()
    {
        mBLE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean foundDevice=false;
                //check if blutooth device exists in database
                    for (DataSnapshot ble : dataSnapshot.getChildren()) {
                        BleDevice b = ble.getValue(BleDevice.class);
                        if (b.getDeviceMac().equals(address)) {
                            foundDevice=true;
                            double latitude = b.getLoc().getLatitude();
                            double longitude = b.getLoc().getLongitude();
                            Intent anythingintent=new Intent(BLE.this,MyLocationDemoActivity.class);
                            Bundle bun=new Bundle();
                            bun.putDouble("latitude",latitude);
                            bun.putDouble("longitude",longitude);
                            anythingintent.putExtras(bun);
                            startActivity(anythingintent);
                        }
                    }
                    if (foundDevice==false) {
                        //no exists, create default object
                        BleDevice ble=new BleDevice(address,new UserLocation(32.103909,35.207836));
                        mBLE.push().setValue(ble);
                        Intent anythingintent=new Intent(BLE.this,MyLocationDemoActivity.class);
                        Bundle bun=new Bundle();
                        bun.putDouble("latitude",32.103909);
                        bun.putDouble("longitude",35.207836);
                        anythingintent.putExtras(bun);
                        startActivity(anythingintent);
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
