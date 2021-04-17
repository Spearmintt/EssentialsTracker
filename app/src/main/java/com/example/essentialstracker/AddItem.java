package com.example.essentialstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essentialstracker.sample.example1_scanning.ScanActivity;

public class AddItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Button button11 = (Button) findViewById(R.id.confirm);
        button11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(AddItem.this , ScanActivity.class);
                startActivity(i);
            }
        });
    }
}