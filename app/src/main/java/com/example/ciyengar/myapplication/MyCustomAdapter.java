package com.example.ciyengar.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dhruv Sagar on 26-Mar-16.
 */
public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<User> userList = new ArrayList<User>();
    private Context context;



    public MyCustomAdapter(ArrayList<User> users, Context context) {
        userList = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int pos) {
        return userList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;//list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.admin_list_layout, null);
        }

        final User thisUser = userList.get(position);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.user_item);
        listItemText.setText((String) thisUser.getName());

        Switch blockSwitch = (Switch)view.findViewById(R.id.blocked_switch);
        blockSwitch.setChecked(thisUser.isBlocked());

        blockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Firebase usersRef = new Firebase("https://moviespotlight.firebaseio.com/users/");
                thisUser.setBlocked(!thisUser.isBlocked());
                usersRef.child(thisUser.getId()).child("blocked").setValue(thisUser.isBlocked());
            }
        });

//
//        //Handle buttons and add onClickListeners
//        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
//        Button addBtn = (Button)view.findViewById(R.id.add_btn);
//
//        deleteBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                list.remove(position); //or some other task
//                notifyDataSetChanged();
//            }
//        });
//        addBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                notifyDataSetChanged();
//            }
//        });

        return view;
    }
}