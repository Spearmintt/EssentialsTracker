package com.example.essentialstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Item extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Button button3 = (Button) findViewById(R.id.additem);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Item.this , AddItem.class);
                startActivity(i);
            }
        });
        
        Button button4 = (Button) findViewById(R.id.deleteitem);
        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Item.this , RemoveItem.class);
                startActivity(i);
            }
        });
    }
}