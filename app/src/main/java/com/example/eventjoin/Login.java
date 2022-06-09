package com.example.eventjoin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Login extends AppCompatActivity {
    private final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private final static int REQUEST_LOCATION = 100;
    private final static int REQUEST_CAMERA = 101;
    private final static int CHOOSE_REQUEST = 188;

    String[] values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PermissionUtils.onRequestMultiplePermissionsResult(this, PERMISSIONS_LOCATION, new PermissionUtils.OnPermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(String... permission) {
                showNoticeDialog(REQUEST_LOCATION);
            }

            @Override
            public void alwaysDenied(String... permission) {
                PermissionUtils.goToAppSetting(Login.this, "Permission");
            }


        });

        EditText email=(EditText)findViewById(R.id.et_login_email);
        EditText pwd=(EditText)findViewById(R.id.et_login_pwd);

        //login by fingerprint
        ImageView fingerprintLogin = (ImageView) findViewById(R.id.btn_fingerlogin);

        fingerprintLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BiometricManager mBiometricManager = new BiometricManager.BiometricBuilder(getApplicationContext())
                        .setTitle("Title")
                        .setSubtitle("Subtitle")
                        .setDescription("description")
                        .setNegativeButtonText("Cancel")
                        .build();

                //start authentication
                mBiometricManager.authenticate(getBiometricCallback());

            }
        });


        Toolbar toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = toolbar.findViewById(R.id.toolbarText);
        textView.setText("Login");


        TextView forgot=(TextView)findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId= email.getText().toString();
                if(emailId.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter email to proceed "+emailId,Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        FirebaseApp.initializeApp(this);
        final FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null){
            email.setText(user.getEmail());
        }
        TextView tv_register=(TextView)findViewById(R.id.tv_register);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });
        Button btn1=(Button)findViewById(R.id.btn_Login);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText editText3=(EditText)findViewById(R.id.et_login_email);
                EditText editText6=(EditText)findViewById(R.id.et_login_pwd);
                String email,password;
                email= editText3.getText().toString();
                password= editText6.getText().toString();
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void showNoticeDialog(final int type) {
        final String temp = "Permission notice";
        String tips = null;
        if (type == REQUEST_LOCATION) {
            tips = String.format(temp, "Location");
        } else if (type == CHOOSE_REQUEST) {
            tips = String.format(temp, "EXTERNAL STORAGE");
        } else if (type == REQUEST_CAMERA) {
            tips = String.format(temp, "CAMERA");
        }
        if (!TextUtils.isEmpty(tips)) {
            new android.app.AlertDialog.Builder(Login.this)
                    .setTitle(PermissionUtils.TITLE)
                    .setMessage(tips)
                    .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (type == REQUEST_LOCATION) {
                                PermissionUtils.requestMultiplePermissions(Login.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                            } else if (type == CHOOSE_REQUEST) {
                                PermissionUtils.requestPermission(Login.this, PermissionUtils.PERMISSION_SD, CHOOSE_REQUEST);
                            } else if (type == REQUEST_CAMERA) {
                                PermissionUtils.requestPermission(Login.this, PermissionUtils.PERMISSION_SD, REQUEST_CAMERA);
                            }

                        }
                    }).setNegativeButton("cancel", null)
                    .show();
        }
    }



    private BiometricCallback getBiometricCallback() {
        return new BiometricCallback() {
            private static final String TAG = "";

            @Override
            public void onSdkVersionNotSupported() {
                Log.d(TAG, "onSdkVersionNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotSupported() {
                Log.d(TAG, "onBiometricAuthenticationNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotAvailable() {
                Log.d(TAG, "onBiometricAuthenticationNotAvailable");
            }

            @Override
            public void onBiometricAuthenticationPermissionNotGranted() {
                Log.d(TAG, "onBiometricAuthenticationPermissionNotGranted");
            }

            @Override
            public void onBiometricAuthenticationInternalError(String error) {
                Log.d(TAG, "onBiometricAuthenticationInternalError");
            }

            @Override
            public void onAuthenticationFailed() {
                Log.d(TAG, "onAuthenticationFailed");
            }

            @Override
            public void onAuthenticationCancelled() {
                Log.d(TAG, "onAuthenticationCancelled");
            }

            @Override
            public void onAuthenticationSuccessful() {
                try {
                    Log.d(TAG, "onAuthenticationSuccessful");
                    values = KeystoreUtils.fetchCredentialForFingerprint(getApplicationContext());
                    if (values != null) {
                        String text = "";
                        for (int i = 0; i < values.length; i++) {
                            //text = text + values[i] + "\n";
                            Log.d("fingerprinttext", values[i]);
                        }
                        //Log.d("fingerprinttext", text);


                        String email = values[0];
                        String password = values[1];
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });




                    } else {
                        Log.d("fingerprinttext", "null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Log.d(TAG, "onAuthenticationHelp, helpCode : " + helpCode + ", helpString : " + helpString);
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Log.d(TAG, "onAuthenticationError, errorCode : " + errorCode + ", errString : " + errString);
            }
        };
    }




}
