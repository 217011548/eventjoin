package com.example.eventjoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eventjoin.Messages;
import com.example.eventjoin.R;

import java.util.ArrayList;
import java.util.List;

public class MyMessageAdapter extends BaseAdapter {

    private List<Messages> messages=new ArrayList<>();
    private LayoutInflater linf;
    private String email;

    public MyMessageAdapter(Context c, List<Messages> messages) {
        this.messages = messages;
        linf=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View v=linf.inflate(R.layout.message_right,null);
        TextView message=(TextView) v.findViewById(R.id.myMessage);

        message.setText(messages.get(position).getMessage());


        return v;
    }

}
