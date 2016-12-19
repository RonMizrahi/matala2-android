package com.example.ron.matala2maps;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button googleMapsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleMapsBtn=(Button) findViewById(R.id.btn_gps);
        googleMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anythingintent=new Intent(MainActivity.this,MyLocationDemoActivity.class);
                startActivity(anythingintent);
            }
        });
    }


}
