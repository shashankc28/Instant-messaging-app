package com.shashankchamoli.tango;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sam on 26-07-2017.
 */

public class ContactAdapter extends ArrayAdapter<contactbean> {

    private Activity activity;
    private List<contactbean> names;

    public ContactAdapter(Activity context, int resource, List<contactbean> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.names = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactAdapter.ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(getContext());

         // determined by view type
        contactbean cb = getItem(position);

        if (convertView != null) {
            holder = (ContactAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.contactlistitemlayout, parent, false);
            holder = new ContactAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.name.setText(cb.getName());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return 1;
    }

    private class ViewHolder {
        private TextView name;

        public ViewHolder(View v) {
            name = v.findViewById(R.id.contactname);
        }
    }
}