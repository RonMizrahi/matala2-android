package com.example.ron.matala2maps;
import android.app.Activity;
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

public class MainActivity extends AppCompatActivity{
    Button btn_gps,btn_qr;
    final Activity activity=this;
    DatabaseReference mRootRef;
    DatabaseReference mUserRef;
    public User currentUser = null;
    public String userKey=null;
    String currentAndroidID=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //database references
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("User");
        btn_gps=(Button) findViewById(R.id.btn_gps);
        btn_qr =(Button) findViewById(R.id.btn_qr);
        currentAndroidID=Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        init();
    }

    private void init()
    {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //loop twice to make sure u get the object key
                for(int i=0;i<2;i++)
                {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        User u = user.getValue(User.class);
                        if (u.getAndroidID().equals(currentAndroidID)) {
                            currentUser = u;
                            userKey = user.getKey();
                            break;  //user was found
                        }
                    }
                    if (currentUser == null) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if(result.getContents()==null)
            {
                Toast.makeText(this,"You cancelled the scanning",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}