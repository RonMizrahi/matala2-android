package com.example.ron.matala2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1,btn2;
    private TextView txtView1,txtView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=(Button)findViewById(R.id.btn1);
        btn2=(Button)findViewById(R.id.btn2);
        txtView1=(TextView)findViewById(R.id.txtView1);
        txtView2=(TextView)findViewById(R.id.txtView2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn1:
            {
                txtView1.setText(String.valueOf(Integer.parseInt(txtView1.getText().toString())+1));
                txtView2.setText(String.valueOf(Integer.parseInt(txtView2.getText().toString())-1));
                break;
            }
            case R.id.btn2:
            {
                txtView1.setText(String.valueOf(Integer.parseInt(txtView1.getText().toString())+1));
                txtView2.setText(String.valueOf(Integer.parseInt(txtView2.getText().toString())-1));
                break;
            }
        }



    }
}
