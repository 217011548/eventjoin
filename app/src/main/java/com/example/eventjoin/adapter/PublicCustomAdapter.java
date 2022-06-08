package com.example.eventjoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eventjoin.Event;
import com.example.eventjoin.R;

import java.util.ArrayList;
import java.util.List;

public class PublicCustomAdapter extends BaseAdapter {
    List<Event> events=new ArrayList<>();
    LayoutInflater linf;
    public PublicCustomAdapter(Context c, List<Event> events){
        this.events = events;
        linf=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=linf.inflate(R.layout.custom_list_public,null);
        TextView EventName=(TextView) v.findViewById(R.id.EventName);
        TextView Time=(TextView) v.findViewById(R.id.Time);
        TextView Date=(TextView) v.findViewById(R.id.Time);
        String type=events.get(position).getType();
        if(type.equals("CQB")) {
            v.setBackgroundResource(R.drawable.cqb);
        }
        else{
            v.setBackgroundResource(R.drawable.outdoor);
        }
        v.getBackground().setAlpha(150);
        EventName.setText(events.get(position).getEvent_name());
        String[] t=events.get(position).getDate().split(",");
        Date.setText(t[0]);
        return v;
    }
}
