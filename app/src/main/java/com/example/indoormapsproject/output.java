package com.example.indoormapsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class output extends AppCompatActivity {
    private Button button;
    static int[][] distance = new int[100][100];;
    static int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        button = findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                backMainActivity();
            }
        });
        //TextView outText = findViewById(R.id.outputText);
        //for (int i = 0; i < count ; i++) {
        //    for (int j = 0 ; j < count ; j++) {
        //    outText.setText(outText.getText()+"\t\t" + distance[i][j]);
        //    }
        //    outText.setText(outText.getText()+"\n");
        //}
    }

    public void backMainActivity() {
        Intent intent = new Intent(this, MapsActivityIndoor.class);
        startActivity(intent);
    }
}
