package com.example.eventjoin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdatePage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String nameEvent,id;
    private DrawerLayout dl;
    private Event e;
    private ActionBarDrawerToggle abdt;
    private EditText name,addre,desc;
    private TextView dateDialog,timeDialog;
    private int iday,iyear,imonth,fday,fmonth,fyear;
    private int imin,ihour,fmin,fhour,iampm,fampm;
    private Button delete,update;
    private boolean counter,counter1;
    private Spinner spinner;
    private String[] going;
    DatabaseReference user;
    String newarraycopy;
    private String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_page);
        Intent i=getIntent();
        e=(Event) i.getSerializableExtra("event");
        id=e.getId();
        nameEvent=e.getEvent_name();
        Toolbar toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = toolbar.findViewById(R.id.toolbarText);
        textView.setText("Update Event");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        abdt=new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navView= findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        break;
                    case R.id.home:startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        break;
                    case R.id.my_events:startActivity(new Intent(getApplicationContext(),MyEvents.class));
                        break;
                    case R.id.create_event:startActivity(new Intent(getApplicationContext(),CreateEvent.class));
                        break;
                    case R.id.map:startActivity(new Intent(getApplicationContext(),Map.class));
                        break;
                }
                return false;
            }
        });
        name= findViewById(R.id.editText8);
        addre=(EditText)findViewById(R.id.editText10);
        desc=(EditText)findViewById(R.id.editText11);
        name.setText(e.getEvent_name());
        addre.setText(e.getAddress());
        desc.setText(e.getDescription());
        dateDialog=(TextView)findViewById(R.id.textView24);
        timeDialog=(TextView)findViewById(R.id.textView25);
        dateDialog.setText(e.getDate());
        timeDialog.setText(e.getTime());

        addre.setFocusable(false);
        addre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(UpdatePage.this);

                startActivityForResult(intent, 100);

            }
        });



        dateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onclick","");
                Calendar c=Calendar.getInstance();
                iyear=c.get(Calendar.YEAR);
                imonth=c.get(Calendar.MONTH);
                iday=c.get(Calendar.DAY_OF_MONTH);
                Log.d("date",iday+" "+imonth+" "+iyear );
                DatePickerDialog datePickerDialog=new DatePickerDialog(UpdatePage.this,UpdatePage.this,iyear,imonth,iday);
                datePickerDialog.show();
            }
        });
        timeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                ihour=c.get(Calendar.HOUR_OF_DAY);
                imin=c.get(Calendar.MINUTE);
                iampm=c.get(Calendar.AM_PM);
                TimePickerDialog timePickerDialog=new TimePickerDialog(UpdatePage.this,UpdatePage.this,ihour,imin,true);
                timePickerDialog.show();
            }
        });
        spinner=(Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.eventTypes, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        if(e.getType().equals("CQB"))
            spinner.setSelection(0);
        else if(e.getType().equals("Outdoor"))
            spinner.setSelection(1);
        update=(Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                DatabaseReference event= FirebaseDatabase.getInstance().getReference("Events").child(id);
                String type=spinner.getSelectedItem().toString();
                String host_id=currentFirebaseUser.getUid();
                Log.d("Desc: ", desc.getText().toString());
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address=addre.getText().toString();
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    if(!name.getText().toString().equals("")&&!addre.getText().toString().equals("")&&!dateDialog.getText().toString().equals("Click to select Date")&&!timeDialog.getText().toString().equals("Click to Select Time")&&!desc.getText().toString().equals("")){
                        Address loc = addresses.get(0);
                        double longitude = loc.getLongitude();
                        double latitude = loc.getLatitude();
                        Event e=new Event(id,name.getText().toString(),addre.getText().toString(),dateDialog.getText().toString(),timeDialog.getText().toString(),desc.getText().toString(),host_id,type,latitude,longitude);
                        event.child("event_name").setValue(e.getEvent_name());
                        event.child("description").setValue(e.getDescription());
                        event.child("address").setValue(e.getAddress());
                        event.child("latitude").setValue(e.getLatitude());
                        event.child("longitude").setValue(e.getLongitude());
                        event.child("going").setValue(e.getGoing());
                        event.child("date").setValue(e.getDate());
                        event.child("time").setValue(e.getTime());
                        event.child("type").setValue(e.getType());
                        Toast.makeText(getApplicationContext(),"Event Updated",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),UpdateEvent.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"All fields are required to create an event",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Invalid address or error loading co-ordinates", Toast.LENGTH_SHORT).show();
                }
            }
        });
        delete=(Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference event=FirebaseDatabase.getInstance().getReference("Events").child(e.getId());


                going=e.getGoing().split(";");
                Log.d("EventID", e.getId());



                counter1=true;
                for(int i=1;i<going.length;i++){
                    counter1=true;
                    Log.d("User: ",going[i]);
                    user=FirebaseDatabase.getInstance().getReference("User").child(going[i]);
                    user.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(counter1) {
                                String attending = dataSnapshot.child("attending").getValue().toString();
                                String[] temp = attending.split(";");

                                    for(int i =1; i<temp.length; i++ ){
                                        if(temp[i].equals(e.getId())){

                                            String [] copyArray = new String[temp.length-1];

                                            System.arraycopy(temp,0,copyArray,0, i);
                                            System.arraycopy(temp, i+1, copyArray, i, temp.length - i - 1);
                                            newarraycopy = String.join(";", copyArray);


                                        }

                                    }
                                Log.d("attending before ",attending);
                                attending.replace(";"+id , "");
                                Log.d("attending after ",newarraycopy);
                                user.child("attending").setValue(attending);
                                user.child("attending").setValue(newarraycopy);

                                counter1=false;
                                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                event.removeValue();
                Intent intent=new Intent(getApplicationContext(),UpdateEvent.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.d("place" , place.getAddress());
            addre.setText(String.format(place.getAddress()));
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        fday=dayOfMonth;
        fmonth=month;
        fyear=year;
        dateDialog.setText(fday+" "+months[fmonth]+", "+fyear);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        fhour=hourOfDay;
        fmin=minute;
        timeDialog.setText(fhour+":"+fmin+" hours");
    }
}
