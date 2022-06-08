package com.example.eventjoin.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eventjoin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater linf;
    private String[] eventids;
    private String type;
    String name,time,date;


    public CustomAdapter(Context c,String[] eventid) {
        Log.d("idd", String.valueOf(eventid));
        this.eventids = eventid;
        linf=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventids.length;
    }

    @Override
    public Object getItem(int position) {
        return eventids[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View v=linf.inflate(R.layout.custom_list_public,null);
        final TextView EventName=(TextView) v.findViewById(R.id.EventName);
        final TextView Time=(TextView) v.findViewById(R.id.Time);
        final TextView Date=(TextView) v.findViewById(R.id.Time);
        DatabaseReference event= FirebaseDatabase.getInstance().getReference("Events");
        event.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name=(String)dataSnapshot.child(eventids[position]).child("event_name").getValue();
                time=(String)dataSnapshot.child(eventids[position]).child("time").getValue();
                date=(String)dataSnapshot.child(eventids[position]).child("date").getValue();
                type=(String)dataSnapshot.child(eventids[position]).child("type").getValue();
                Log.d("DATA::",eventids[position]+" "+position+" "+name+" "+time+" "+date);
                Log.d("length:",""+eventids.length);
                Log.d("Type", name);
                if(type.equals("CQB")) {
                    v.setBackgroundResource(R.drawable.cqb);
                }
                else if(type.equals("Outdoor")){
                    v.setBackgroundResource(R.drawable.outdoor);
                }

                EventName.setText(name);
                Date.setText(time);
                Time.setText(date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }
}
