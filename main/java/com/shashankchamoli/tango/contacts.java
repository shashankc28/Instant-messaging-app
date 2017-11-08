package com.shashankchamoli.tango;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.provider.ContactsContract;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.id.list;


public class contacts extends Fragment{
    FrameLayout fl;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =1 ;
    private Handler updateBarHandler;
    ArrayList<String> nn;
    ArrayList<contactbean> contactList= new ArrayList<contactbean>();
    TextView nts;
    Cursor cursor;
    int counter;
    FloatingActionButton fab2;
    ContactAdapter adapter;
    ArrayList<contactbean> sam;
    ListView listView;
    DatabaseHandler db ;
    ProgressBar progressBar;
    public contacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView=(ListView)getActivity().findViewById(R.id.contactlist);
        sam=new ArrayList<>();
        db = new DatabaseHandler(getActivity());
        progressBar=getActivity().findViewById(R.id.contactlistrefreshprogess);
        registerForContextMenu(listView);
        fab2=getActivity().findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar .setVisibility(View.VISIBLE);

                Log.d("::Thread Started::", "Success..");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getContacts();

                    }
                }).start();
                Snackbar snackbar1 = Snackbar.make(fl, "Reading contacts", Snackbar.LENGTH_LONG);
                snackbar1.show();

            }

        });
        //get permission to read contacts from phone book
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        //reference to frame contact
        fl=(FrameLayout)getActivity().findViewById(R.id.fl);


        //update handler for thread
        updateBarHandler =new Handler();
      nn=new ArrayList<String>();


        //call the thread to read contacts from phone book


        sam.clear();
        sam= db.getAllContacts();
        db.close();
        nts=(TextView)getActivity().findViewById(R.id.nts);
        if(sam.isEmpty())
        {
            nts.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else
        {
            nts.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            adapter = new ContactAdapter(getActivity(), R.layout.fragment_contacts, sam);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), Mainchatactivity.class);
                //i.putExtra("contactid",contactid);
                contactbean cb=sam.get(i);
                Log.d("activity_started_with", cb.getId());
                intent.putExtra("receiverid",cb.getId());
                intent.putExtra("receivername",cb.getName());
                startActivity(intent);
                Log.d("::onListItemClick::", "position: "+i+" clicked..");
            }
        });
        //update the list view
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,nn);
        //setListAdapter(adapter);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.contextmenu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                db = new DatabaseHandler(getActivity());
                contactList.clear();
                contactList=db.getAllContacts();
                db.deleteContact(contactList.get(info.position));
                sam.remove(info.position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Contact Deleted" , Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }



    public void getContacts() {

        Log.d("sam", "Fetch contacts started Successfully....");
        Log.d("sam", "clearing list");
        contactList.clear();
        String phoneNumber = null;
        FirebaseDatabase firedb=FirebaseDatabase.getInstance();
        Log.d("sam","database connected");
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        ContentResolver contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {

                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                    //for processbar

                    }
                });
                contactbean c;
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                if (hasPhoneNumber > 0) {

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor.moveToNext()) {
                        c=new contactbean();

                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        if(phoneNumber.contains(" "))
                        {
                            phoneNumber=phoneNumber.replaceAll(" ","");
                        }
                        if(phoneNumber.startsWith("+91"))
                        {
                            phoneNumber=phoneNumber.replace("+91","");
                        }

                        if(phoneNumber.startsWith("91")&&phoneNumber.length()>10)
                        {
                            phoneNumber=phoneNumber.substring(2,phoneNumber.length());
                        }
                        if(phoneNumber.contains("\\("))
                        {
                            phoneNumber=phoneNumber.replaceAll("\\(","");
                        }
                        if(phoneNumber.contains("\\)"))
                        {
                            phoneNumber=phoneNumber.replaceAll("\\)","");
                        }
                        if(phoneNumber.contains("-"))
                        {
                            phoneNumber=phoneNumber.replaceAll("-","");
                        }
                        if(phoneNumber.contains("\\."))
                        {
                            phoneNumber=phoneNumber.replaceAll("\\.","");
                        }
                        if(phoneNumber.startsWith("0"))
                        {
                            phoneNumber=phoneNumber.substring(1,phoneNumber.length());
                        }
                            if(phoneNumber.length()>=10) {






                                DatabaseReference myRef = firedb.getReference("users");
                                final String finalPhoneNumber = phoneNumber;
                                final String finalname = name;
                                Log.d("sam", "beforefirebasecheck");
                                myRef.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            if (dataSnapshot.child("phone").getValue().equals(finalPhoneNumber)) {
                                                //number exists get the id and store the number
                                                Log.d("sam", finalPhoneNumber);
                                                String id = dataSnapshot.child("id").getValue().toString();
                                                setuser(finalname, finalPhoneNumber, id);
                                                updatedatabase();
                                                Log.d("sam", "database-updated");
                                                updatelist();
                                                Log.d("sam", "list-updated");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }




                    }
                    phoneCursor.close();
                    // Read every email id associated with the contact
                    //Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
                    //while (emailCursor.moveToNext()) {
                       // email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                       // output.append("\n Email:" + email);
                    //}
                    //emailCursor.close();
                }
                // Add the contact to the ArrayList

            }

         }

        Log.d("sam", "Fetch contacts Completed Successfully....");
    }

    private void setuser(String finalname, String finalPhoneNumber, String id) {
        contactbean tempcontact=new contactbean();
        Log.d("sam", "inside setuser "+id+" :: "+finalPhoneNumber+" : "+finalname);
        tempcontact.setId(id);
        tempcontact.setName(finalname);
        tempcontact.setNumber(finalPhoneNumber);
        contactList.add(tempcontact);
    }

    public void updatelist()
    {
        contactList.clear();
        contactList= db.getAllContacts();
        db.close();
        nts=(TextView)getActivity().findViewById(R.id.nts);
        if(contactList.isEmpty())
        {
            nts.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else
        {
            nts.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d("adapterchange"," in the ui thread");
                    sam=contactList;
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        }

    public void updatedatabase()
    {
        db.cleartable();
        for (contactbean cn : contactList) {
            Log.d("sam", "before unique "+cn.getName());
        }
        Set<contactbean> s= new HashSet<>();
        s.addAll(contactList);
        contactList = new ArrayList<>();
        contactList.addAll(s);

        Collections.sort(contactList);


        for (contactbean cn : contactList) {
            db.addContact(cn);
            Log.d("sam","insert to database "+cn.getName()+" "+cn.getNumber());
        }

    }


}

