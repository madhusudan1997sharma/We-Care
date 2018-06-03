package com.magarex.emergencyalert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magarex.emergencyalert.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class AddContacts extends ActionBarActivity {

    EditText number1, number2, number3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        number1 = (EditText) findViewById(R.id.number1);
        number2 = (EditText) findViewById(R.id.number2);
        number3 = (EditText) findViewById(R.id.number3);
        Button savebtn = (Button) findViewById(R.id.savebtn);
        Button cancelbtn = (Button) findViewById(R.id.cancelbtn);


        try {
            FileInputStream fin;
            int c = 0;
            String tempnumber = "";

            fin = openFileInput("number1");
            while ((c = fin.read()) != -1) {
                tempnumber = tempnumber + Character.toString((char) c);
            }
            number1.setText(tempnumber);
            ///////////////////////////////////////////////////////////////////////////////////
            c = 0;
            tempnumber = "";
            fin = openFileInput("number2");
            while ((c = fin.read()) != -1) {
                tempnumber = tempnumber + Character.toString((char) c);
            }
            number2.setText(tempnumber);
            ///////////////////////////////////////////////////////////////////////////////////
            c = 0;
            tempnumber = "";
            fin = openFileInput("number3");
            while ((c = fin.read()) != -1) {
                tempnumber = tempnumber + Character.toString((char) c);
            }
            number3.setText(tempnumber);
        } catch (IOException e) { }




        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num1 = number1.getText().toString();
                String num2 = number2.getText().toString();
                String num3 = number3.getText().toString();
                try {
                    FileOutputStream fout;
                    fout = openFileOutput("number1", Context.MODE_PRIVATE);
                    fout.write(num1.getBytes());
                    fout = openFileOutput("number2", Context.MODE_PRIVATE);
                    fout.write(num2.getBytes());
                    fout = openFileOutput("number3", Context.MODE_PRIVATE);
                    fout.write(num3.getBytes());

                    fout.close();

                    Toast.makeText(AddContacts.this, "Changes Saved", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(AddContacts.this, "Error Saving the changes, please try again later", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}