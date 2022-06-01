package com.example.eventjoin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventjoin.adapter.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Chat extends AppCompatActivity {
    private DatabaseReference events;
    private FirebaseUser currentFirebaseUser;
    private String event_id;
    private List<String> messages = new ArrayList<>();
    private List<String> senders = new ArrayList<>();
    private List<Messages> messageObjects = new ArrayList<>();
    private ListView messageBox;
    private boolean counter = false;
    private String senderEmail;
    private String CHANNEL_ID = "Event Messages";
    private Messages m;
    private String name, date, time, desc, address;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private LinearLayout addImageAndMap;
    private ImageButton imageButton2;
    private MessageAdapter ma;
    private ImageButton imageCamera;
    private ImageButton imageSelect;
    private ImageButton imageMap;
    private StorageReference storageRef;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    public boolean check(Messages message) {
        int i;
        for (i = 0; i < messageObjects.size(); i++) {
            if (messageObjects.get(i).getId().equals(message.getId()))
                return false;
        }
        return true;
    }

    public Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.savedInstanceState = savedInstanceState;
        Intent i = getIntent();
        event_id = i.getStringExtra("event_id");
        String eventName = i.getStringExtra("event_name");
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbarText);
        textView.setText(eventName);
        dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        addImageAndMap = findViewById(R.id.addImageAndMap);
        imageButton2 = findViewById(R.id.imageButton2);
        imageCamera = findViewById(R.id.imageCamera);
        imageSelect = findViewById(R.id.imageSelect);
        imageMap = findViewById(R.id.imageMap);
        messageBox = findViewById(R.id.messageBox);
        ma = new MessageAdapter(Chat.this, new ArrayList<>());
        messageBox.setAdapter(ma);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageAndMap.setVisibility(addImageAndMap.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCamera();
            }
        });
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImg();
            }
        });
        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivityForResult(new Intent(Chat.this,MapSelect.class),1000);
            }
        });
        abdt = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    case R.id.my_events:
                        startActivity(new Intent(getApplicationContext(), MyEvents.class));
                        break;
                    case R.id.create_event:
                        startActivity(new Intent(getApplicationContext(), CreateEvent.class));
                        break;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(), Map.class));
                        break;
                }
                return false;
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(getApplicationContext(), PublicEventDescription.class);
                DatabaseReference event = FirebaseDatabase.getInstance().getReference("Events").child(event_id);
                event.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("event_name").getValue().toString();
                        date = dataSnapshot.child("date").getValue().toString();
                        time = dataSnapshot.child("time").getValue().toString();
                        desc = dataSnapshot.child("description").getValue().toString();
                        address = dataSnapshot.child("address").getValue().toString();
                        i.putExtra("id", event_id);
                        i.putExtra("name", name);
                        i.putExtra("date", date);
                        i.putExtra("time", time);
                        i.putExtra("desc", desc);
                        i.putExtra("address", address);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        events = FirebaseDatabase.getInstance().getReference("Events");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance("gs://cem-3c479.appspot.com");
        storageRef = storage.getReference();
        ImageButton send = (ImageButton) findViewById(R.id.imageButton);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText message = (EditText) findViewById(R.id.editText9);
                if (!message.getText().toString().equals("")) {
                    String messageId = events.push().getKey();
                    String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                    Messages m = new Messages(message.getText().toString(), currentFirebaseUser.getUid(), messageId, timeStamp
                            , 1 + "", "", 0.0d, 0.0d, "");
                    events.child(event_id).child("messages").child(messageId).setValue(m);
                    message.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Cant send Empty messages", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DatabaseReference mes = FirebaseDatabase.getInstance().getReference("Events").child(event_id).child("messages");
        mes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keynode : dataSnapshot.getChildren()) {
                    m = keynode.getValue(Messages.class);
                    String message = m.getMessage();
                    if (check(m)) {
                        messages.add(message);
                        messageObjects.add(m);
                        //todo  reload
                        ma.addAll(messageObjects);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (abdt.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    private static final String FILE_PROVIDER_AUTHORITY = "com.example.eventjoin.fileprovider";
    private Uri imageUri;

    public void doCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile = createImageFile();
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
                } else {
                    imageUri = Uri.fromFile(imageFile);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 2);
        }

    }

    private File createImageFile() {
        String format = new SimpleDateFormat("yyyy-MM-DD").format(new Date());
        String fileName = "JDEG_" + format + "_";
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(fileName, ".jpg", file);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    List<Uri> uriList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            uriList.clear();
            if (data.getClipData() != null) {
                data.getClipData().getItemAt(0).getUri();
                uriList.add(data.getClipData().getItemAt(0).getUri());
            } else if (data.getData() != null) {
                uriList.add(data.getData());
            }
            doUpload();
        } else if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            uriList.clear();

            uriList.add(imageUri);
            doUpload();


        } else if (requestCode == 1000 && resultCode == RESULT_OK && null != data) {
            String string = "";
            String messageId = events.push().getKey();
            String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Messages m = new Messages("", currentFirebaseUser.getUid(), messageId, timeStamp
                    , 3 + "", string, data.getDoubleExtra("lat",0.0d)
                    , data.getDoubleExtra("lon",0.0d), data.getStringExtra("name"));
            events.child(event_id).child("messages").child(messageId).setValue(m);
        }
    }


    private void doUpload() {
        Uri file = uriList.get(0);
        storageReference = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = storageReference.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(Chat.this, "upload err.",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.child("images/" + file.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri tempUri) {
                        String string = tempUri.toString();
                        String messageId = events.push().getKey();
                        String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                        Messages m = new Messages("", currentFirebaseUser.getUid(), messageId, timeStamp
                                , 2 + "", string, 0.0d, 0.0d, "");
                        events.child(event_id).child("messages").child(messageId).setValue(m);
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }


    public void addImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
}
