package com.example.maruta.uberfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    //Ui
    private TextView changeView;
    private Button logBtn;
    private EditText userView;
    private EditText passView;

    private FirebaseAuth fAuth;

    private boolean isLog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUi();


    }

    private void initUi() {

        fAuth = FirebaseAuth.getInstance();

        changeView = findViewById(R.id.changeView);
        changeView.setText("or, Register");

        logBtn = findViewById(R.id.logBtn);
        userView = findViewById(R.id.userView);
        passView = findViewById(R.id.passView);

        changeView.setOnClickListener((View event) ->{

            if(isLog){

                logBtn.setText("Register");
                changeView.setText("or, Login");
                isLog = false;
            }else {

                logBtn.setText("Login");
                changeView.setText("or, Register");
                isLog = true;
            }

        });


        logBtn.setOnClickListener((View event) -> {

            if(isLog){

                logFunc();
            }else {

                regFunc();
            }


        });

    }

    private void regFunc() {
        /*Register user*/

        String email = userView.getText().toString();
        String password = passView.getText().toString();

        if(email.equals("") || password.equals("")){

            Toast.makeText(LoginActivity.this, "Complete the fields first", Toast.LENGTH_LONG).show();
            return;
        }

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Register succesfull", Toast.LENGTH_SHORT).show();

                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                }else{

                    FirebaseAuthException e = (FirebaseAuthException)task.getException();


                    Log.i("zile", e.getMessage());

                    Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void logFunc() {
        /*Log user*/

        String email = userView.getText().toString();
        String password = passView.getText().toString();

        if(email.equals("") || password.equals("")){

            Toast.makeText(LoginActivity.this, "Complete the fields first", Toast.LENGTH_LONG).show();
            return;
        }

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                } else {

                    Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}
