package com.example.eventjoin;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventjoin.security.KeystoreUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        Toolbar toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView textView = toolbar.findViewById(R.id.toolbarText);
//        textView.setText("Register");
        Button register=(Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                EditText et_email=(EditText)findViewById(R.id.et_email);
                final String email=et_email.getText().toString().trim();
                final EditText et_pwd=(EditText)findViewById(R.id.et_pwd);
                final String password=et_pwd.getText().toString();
                EditText et_cppwd=(EditText)findViewById(R.id.et_confirmpwd);
                String cpassword=et_cppwd.getText().toString();
                final EditText et_name=(EditText)findViewById(R.id.et_name);
                final String name=et_name.getText().toString();
                final FirebaseAuth auth= FirebaseAuth.getInstance();



                //checking for user insert enough data and password achieved requirement
                if(password.equals(cpassword) && password.length()>=6 && !name.equals("") && !email.equals("")){
                    //pass the info to firebase for auth
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final FirebaseUser user=auth.getCurrentUser();
                                DatabaseReference newUser= FirebaseDatabase.getInstance().getReference("User");

                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                String id=currentFirebaseUser.getUid();
                                User userinfo=new User(id,name,email);
                                newUser.child(id).setValue(userinfo);

                                // save the login info for fingerprint
                                String dataToSave = email + "," + password;
                                KeystoreUtils.saveCredentialForFingerprint(getApplicationContext(), dataToSave);

                                Intent i= new Intent(getApplicationContext(),Login.class);
                                startActivity(i);
                            }
                            else{
                                Log.w("error:",task.getException());
                                Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }



                    });

                }
                else
                // alert user if not achieved the requirement
                {
                    if(!password.equals(cpassword)){
                        Toast.makeText(getApplicationContext(),"Passwords dont match",Toast.LENGTH_SHORT).show();
                    }
                    else if(password.length()<6){
                        Toast.makeText(getApplicationContext(),"Password should be more than 6 characters long",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Invalid values entered",Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });
    }





}
