package com.example.eventjoin.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventjoin.Chat;
import com.example.eventjoin.Messages;
import com.example.eventjoin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private  List<Messages> messages=new ArrayList<>();
    private LayoutInflater linf;
    private String email,time;
    Context context;
    public MessageAdapter(Context c,List<Messages> messages) {
        this.messages = messages;
        linf=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context  =c;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FirebaseUser currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(!messages.get(position).getSenderId().equals(currentFirebaseUser.getUid())) {
            View v = linf.inflate(R.layout.message_left, null);
            final TextView sender = (TextView) v.findViewById(R.id.sender);
            final TextView message = (TextView) v.findViewById(R.id.senderMessage);

            ImageView imageView= v.findViewById(R.id.left_img);
            LinearLayout rightLl= v.findViewById(R.id.left_ll);
            TextView textView9= v.findViewById(R.id.textView9);
            MapView mapView= v.findViewById(R.id.map);
            TextView time=(TextView)v.findViewById(R.id.time);
            time.setText(messages.get(position).getTime());


            if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("2")){
                message.setVisibility(View.GONE);
                rightLl.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions()
                        .placeholder(new ColorDrawable(Color.parseColor("#f2f3f5")))
                        .error(new ColorDrawable(Color.parseColor("#f2f3f5")));
                Glide.with(context).load(messages.get(position).getImageUrl()).apply(options).into(imageView);
            }else if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("1")){
                message.setText(messages.get(position).getMessage());
                imageView.setVisibility(View.GONE);
                rightLl.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            }else if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("3")){
                message.setText(messages.get(position).getMessage());
                imageView.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                rightLl.setVisibility(View.VISIBLE);
                GoogleMapOptions options = new GoogleMapOptions();
                options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                        .compassEnabled(false)
                        .rotateGesturesEnabled(false)
                        .tiltGesturesEnabled(false);
                textView9.setText(messages.get(position).getLocationInfo());
                mapView.onCreate(((Chat)context).savedInstanceState);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        LatLng sydney = new LatLng(messages.get(position).getLat(), messages.get(position).getLon());
                        googleMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .title(messages.get(position).getLocationInfo()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,
                                12.0f));
                    }
                });
            }




            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User").child(messages.get(position).getSenderId());
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    email = dataSnapshot.child("email").getValue().toString();
                    sender.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return v;
        }
        else{
            View v=linf.inflate(R.layout.message_right,null);
            TextView message=(TextView) v.findViewById(R.id.myMessage);
            ImageView imageView= v.findViewById(R.id.right_img);
            LinearLayout rightLl= v.findViewById(R.id.right_ll);
            TextView textView9= v.findViewById(R.id.textView9);
            MapView mapView= v.findViewById(R.id.map);

            if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("2")){
                message.setVisibility(View.GONE);
                rightLl.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions()
                        .placeholder(new ColorDrawable(Color.parseColor("#f2f3f5")))
                        .error(new ColorDrawable(Color.parseColor("#f2f3f5")));
                Glide.with(context).load(messages.get(position).getImageUrl()).apply(options).into(imageView);
            }else if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("1")){
                message.setText(messages.get(position).getMessage());
                imageView.setVisibility(View.GONE);
                rightLl.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            }else if (messages.get(position).getType()!=null&&messages.get(position).getType().equals("3")){
                message.setText(messages.get(position).getMessage());
                imageView.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                rightLl.setVisibility(View.VISIBLE);
                GoogleMapOptions options = new GoogleMapOptions();
                options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                        .compassEnabled(false)
                        .rotateGesturesEnabled(false)
                        .tiltGesturesEnabled(false);
                textView9.setText(messages.get(position).getLocationInfo());
                mapView.onCreate(((Chat)context).savedInstanceState);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        LatLng sydney = new LatLng(messages.get(position).getLat(), messages.get(position).getLon());
                        googleMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .title(messages.get(position).getLocationInfo()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,
                                12.0f));
                    }
                });
            }

            TextView time=(TextView)v.findViewById(R.id.textView8);
            time.setText(messages.get(position).getTime());
            return v;
        }
    }

    public void addAll(List<Messages> arrayList){
        this.messages.clear();
        this.messages.addAll(arrayList);
        notifyDataSetChanged();
    }
}
