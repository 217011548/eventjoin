package com.example.eventjoin;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.eventjoin.biometric.BiometricManager;
import com.example.eventjoin.biometric.BiometricCallback;
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
        TextView textView = toolbar.findViewById(R.id.toolbarText);
        textView.setText("Register");
        Button register=(Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                EditText editText2=(EditText)findViewById(R.id.editText2);
                final String email=editText2.getText().toString().trim();
                final EditText pwd=(EditText)findViewById(R.id.editText4);
                final String password=pwd.getText().toString();
                EditText cpwd=(EditText)findViewById(R.id.editText5);
                final TextView textView7=(TextView)findViewById(R.id.textView7);
                String cpassword=cpwd.getText().toString();
                final EditText n=(EditText)findViewById(R.id.editText);
                final String name=n.getText().toString();
                final FirebaseAuth auth= FirebaseAuth.getInstance();
                if(password.equals(cpassword) && password.length()>=6 && !name.equals("") && !email.equals("")){




                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final FirebaseUser user=auth.getCurrentUser();
                                DatabaseReference newUser= FirebaseDatabase.getInstance().getReference("User");
                                //String id=newUser.push().getKey();
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                String id=currentFirebaseUser.getUid();
                                User u=new User(id,name,email);
                                newUser.child(id).setValue(u);

                                String dataToSave = email + "," + password;
                                KeystoreUtils.saveCredentialForFingerprint(getApplicationContext(), dataToSave);

                                Log.d("dataToSave", dataToSave);



                                Intent i= new Intent(getApplicationContext(),Login.class);
                                startActivity(i);
                            }
                            else{
                                Log.w("error:",task.getException());
                                Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                                //textView7.setText("error "+task.getException());
                            }
                        }



                    });




//                    BiometricManager mBiometricManager = new BiometricManager.BiometricBuilder(getApplicationContext())
//                            .setTitle("Title")
//                            .setSubtitle("Subtitle")
//                            .setDescription("description")
//                            .setNegativeButtonText("Cancel")
//                            .build();
//
//                    //start authentication
//                    //mBiometricManager.authenticate(getBiometricCallback());



                }
                else
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

//    private BiometricCallback getBiometricCallback() {
//        return new BiometricCallback() {
//            private static final String TAG = "";
//
//            @Override
//            public void onSdkVersionNotSupported() {
//                Log.d(TAG, "onSdkVersionNotSupported");
//            }
//
//            @Override
//            public void onBiometricAuthenticationNotSupported() {
//                Log.d(TAG, "onBiometricAuthenticationNotSupported");
//            }
//
//            @Override
//            public void onBiometricAuthenticationNotAvailable() {
//                Log.d(TAG, "onBiometricAuthenticationNotAvailable");
//            }
//
//            @Override
//            public void onBiometricAuthenticationPermissionNotGranted() {
//                Log.d(TAG, "onBiometricAuthenticationPermissionNotGranted");
//            }
//
//            @Override
//            public void onBiometricAuthenticationInternalError(String error) {
//                Log.d(TAG, "onBiometricAuthenticationInternalError");
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                Log.d(TAG, "onAuthenticationFailed");
//            }
//
//            @Override
//            public void onAuthenticationCancelled() {
//                Log.d(TAG, "onAuthenticationCancelled");
//            }
//
//            @Override
//            public void onAuthenticationSuccessful() {
//                try {
//                    Log.d(TAG, "onAuthenticationSuccessful");
//                    String[] values = KeystoreUtils.fetchCredentialForFingerprint(getApplicationContext());
//                    if (values != null) {
//                        String text = "";
//                        for (int i = 0; i < values.length; i++) {
//                            //text = text + values[i] + "\n";
//                            Log.d("fingerprinttext", values[i]);
//                        }
//                        //Log.d("fingerprinttext", text);
//                    } else {
//                        Log.d("fingerprinttext", "null");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//                Log.d(TAG, "onAuthenticationHelp, helpCode : " + helpCode + ", helpString : " + helpString);
//            }
//
//            @Override
//            public void onAuthenticationError(int errorCode, CharSequence errString) {
//                Log.d(TAG, "onAuthenticationError, errorCode : " + errorCode + ", errString : " + errString);
//            }
//        };
//    }




}
