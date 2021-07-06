package com.example.ecomcolpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
  EditText Email;
  EditText Password;
  Button SignUp;
  ProgressBar pb;
  Button Signin;
  private String email,password;
  private  FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null)
        {
            openMain();
        }
    }

    private void openMain() {
        startActivity(new Intent(this,SecondActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Email=findViewById(R.id.t1);
        pb=findViewById(R.id.pb);
        Password=findViewById(R.id.t2);
        SignUp=findViewById(R.id.t3);
        Signin=findViewById(R.id.t4);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

pb.setVisibility(View.GONE);
Signin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        pb.setVisibility(View.VISIBLE);
        vaidatesigningdata();
    }

    private void vaidatesigningdata() {
        
        email=Email.getText().toString();
        password=Password.getText().toString();
        if(email.isEmpty())
        {
            Email.setError("Required");

        }
        else if(password.isEmpty())
        {
            Password.setError("Required");
        }
        else {
            signinuser();
        }
    }

    private void signinuser() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete( Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    Toast.makeText(MainActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(getApplicationContext(),SecondActivity.class));
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

});

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                validatedata();

            }
            


            private void validatedata() {
                email=Email.getText().toString();
                password=Password.getText().toString();

                if(email.isEmpty())
                {
                    Email.setError("Required");

                }
                else if(password.isEmpty())
                {
                    Password.setError("Required");
                }
                else {
                    createuser();
                }
            }

            private void createuser() {
    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull  Task<AuthResult> task) {
            if(task.isSuccessful()){
                pb.setVisibility(View.GONE);
                uploaddata();
                Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(MainActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void uploaddata() {

            myRef.setValue("Hello, World!");
            String key=myRef.push().getKey();
            HashMap<String,String> user=new HashMap<>();
            user.put("key",key);
            user.put("Email",email);
            user.put("password",password);
            myRef.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull  Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                      openMain();
                    }
                    else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "SignUp Failed ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
            }
        });

    }

}