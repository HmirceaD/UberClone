package com.example.maruta.uberfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Ui
    private Switch uberSwitch;
    private Button sigInBtn;
    private TextView uberView;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        fAuth = FirebaseAuth.getInstance();

        fAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser user = fAuth.getCurrentUser();
                }
            }
        });


    }

    private void initUi() {

        uberSwitch = findViewById(R.id.uberSwitch);
        sigInBtn = findViewById(R.id.signIn);
        uberView = findViewById(R.id.uberView);

        uberSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(uberSwitch.isChecked()){

                    uberView.setText("Rider");
                }else {

                    uberView.setText("Driver");
                }

            }
        });

        sigInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(uberSwitch.isChecked()){

                    Intent it = new Intent(MainActivity.this, RiderActivitty.class);

                    startActivity(it);
                }else {

                    Intent it = new Intent(MainActivity.this, DriverActivity.class);

                    startActivity(it);
                }
            }
        });

    }
}
