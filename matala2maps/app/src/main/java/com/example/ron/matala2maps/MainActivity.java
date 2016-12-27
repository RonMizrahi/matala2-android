package com.example.ron.matala2maps;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.provider.Settings.Secure;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    Button btn_gps,btn_qr,btn_ble,btn_trackall;
    final Activity activity=this;
    DatabaseReference mRootRef;
    DatabaseReference mUserRef;
    public User currentUser = null;
    public String userKey=null;
    String currentAndroidID=null;
    ArrayList<UserLocation> AllUsersLocations=new ArrayList<UserLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //database references
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("User");
        btn_gps=(Button) findViewById(R.id.btn_gps);
        btn_qr =(Button) findViewById(R.id.btn_qr);
        btn_ble =(Button) findViewById(R.id.btn_ble);
        btn_trackall=(Button) findViewById(R.id.btn_trackall);
        currentAndroidID=Secure.getString(getContentResolver(), Secure.ANDROID_ID);     //get unique android device id
        init();
    }

    private void init()
    {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //loop twice to make sure u get the object key
                //get user from database
                for(int i=0;i<2;i++)
                {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        User u = user.getValue(User.class);
                        AllUsersLocations.add(u.getLocation());
                        if (u.getAndroidID().equals(currentAndroidID)) {
                            currentUser = u;
                            userKey = user.getKey();
                            //break;  //user was found
                        }
                    }
                    if (currentUser == null) {  //create new user
                        currentUser = new User(currentAndroidID, false);
                        mUserRef.push().setValue(currentUser);
                        //loop to find the key after adding
                    }
                    else
                        break;  //key is already exist get out
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_gps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent anythingintent=new Intent(MainActivity.this,MyLocationDemoActivity.class);
                startActivity(anythingintent);
            }
        });

        btn_qr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        btn_ble.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent anythingintent=new Intent(MainActivity.this,DeviceList.class);
                startActivity(anythingintent);
            }
        });
        btn_trackall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent anythingintent=new Intent(MainActivity.this,MyLocationDemoActivity.class);
                Bundle b=new Bundle();
                b.putBoolean("trackall",true);
                anythingintent.putExtras(b);
                startActivity(anythingintent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //QR function!!!!!!!
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if(result.getContents()==null)
            {
                Toast.makeText(this,"You cancelled the scanning",Toast.LENGTH_LONG).show();
            }
            else
            {
                try {
                    //GET QR scan and parse it , Example: (x,y) ===== (30.30,40.40)
                    String qrMessage=result.getContents();
                    String[] split=qrMessage.split(",");
                    double latitude = Double.parseDouble(split[0]);
                    double longitude = Double.parseDouble(split[1]);
                    UserLocation ul = new UserLocation(latitude, longitude);
                    currentUser.setLocation(ul);
                    mUserRef.child(userKey).setValue(currentUser);
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    Thread.sleep(3000);
                    Intent anythingintent=new Intent(MainActivity.this,MyLocationDemoActivity.class);
                    Bundle b=new Bundle();
                    b.putBoolean("trackall",false);
                    b.putDouble("latitude",latitude);
                    b.putDouble("longitude",longitude);
                    anythingintent.putExtras(b);
                    startActivity(anythingintent);
                }
                catch(Exception e)
                {
                    Toast.makeText(this, "Bad QR scan", Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
