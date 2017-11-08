package com.shashankchamoli.tango;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.mode;

public class Mainchatactivity extends AppCompatActivity {
    ListView chatlist;
    ImageButton send;
    EditText message;
    boolean myMessage = true;
    private List<ChatBubble> ChatBubbles;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String receiverid;
    String senderid;
    FirebaseDatabase database;
    ChildEventListener childEventListener,receiverlistener;
    public ArrayAdapter<ChatBubble> adapter;
    SparseBooleanArray mSelectedItemsIds;

    @Override
    protected void onStop() {
        super.onStop();
        if (childEventListener != null) {
            myRef.child(senderid).child(receiverid).removeEventListener(childEventListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainchatactivity);
        mAuth=FirebaseAuth.getInstance();
        mSelectedItemsIds = new SparseBooleanArray();
        FirebaseUser sender=mAuth.getCurrentUser();
        senderid=sender.getUid();
        Log.d("senderid", senderid);
        database= FirebaseDatabase.getInstance();
        myRef = database.getReference("chats");
        if(myRef!=null)
        {
            Log.d("senderid1", "reference received");
        }
        ChatBubbles = new ArrayList<>();
        Intent intent=getIntent();
        receiverid=intent.getStringExtra("receiverid");
        String receivername=intent.getStringExtra("receivername");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(receivername);
        toolbar.setLogo(R.drawable.circularimagewithborder);
        chatlist=(ListView)findViewById(R.id.chatid);
        send=(ImageButton)findViewById(R.id.sendButton);
        message=(EditText)findViewById(R.id.message);

        adapter = new MessageAdapter(this, R.layout.chatleftbg, ChatBubbles);
        chatlist.setAdapter(adapter);
        chatlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        chatlist.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = chatlist.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " Selected");
                toggleSelection(i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                actionMode.getMenuInflater().inflate(R.menu.deletemode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                Log.d("xxx", "onActionItemClicked: i am here");
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        Log.d("xxx", "onActionItemClicked: inside case ");
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Log.d("xxx", "onActionItemClicked: inside if");
                                ChatBubble selecteditem = adapter.getItem(selected.keyAt(i));
                                ChatBubbles.remove(selected.keyAt(i));
                                adapter.notifyDataSetChanged();
                                Log.d("xxx", "onActionItemClicked: "+selecteditem.getContent()+"::"+selecteditem.getTime());
                                if(selecteditem.myMessage()) {
                                    myRef.child(senderid).child(receiverid).child(selecteditem.getTime()).setValue(null);
                                }// Remove selected items following the ids
                                //here comes the firebase part of the code
                            }
                        }
                        // Close CAB
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                //nothing here
            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                messagereceiver message= dataSnapshot.getValue(messagereceiver.class);
                String text= message.getText();
                ChatBubble ChatBubble = new ChatBubble(text, true, message.getTime());
                ChatBubbles.add(ChatBubble);
                adapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
                Log.d("sam", "onchild added :"+text);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildChanged:" + dataSnapshot.getKey());

                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("sam", "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.child(senderid).child(receiverid).addChildEventListener(childEventListener);

        receiverlistener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                messagereceiver message= dataSnapshot.getValue(messagereceiver.class);
                String text= message.getText();
                ChatBubble ChatBubble = new ChatBubble(text, false, message.getTime());
                ChatBubbles.add(ChatBubble);
                adapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
                Log.d("sam", "onchild added :"+text);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildChanged:" + dataSnapshot.getKey());

                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("sam", "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("sam", "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.child(receiverid).child(senderid).addChildEventListener(receiverlistener);

    }
public void sendpressed(View view)
{
    if (message.getText().toString().trim().equals("")) {
        Toast.makeText(Mainchatactivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
    } else {
        //add message to list
        ///ChatBubble ChatBubble = new ChatBubble(message.getText().toString(), myMessage);
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        messagereceiver temp=new messagereceiver(message.getText().toString(),senderid,currentDateandTime);
        myRef.child(senderid).child(receiverid).child(currentDateandTime).setValue(temp);
        //myRef.child(senderid).child(receiverid).child(currentDateandTime).child("from").setValue(senderid);
        //myRef.child(senderid).child(receiverid).child(currentDateandTime).child("text").setValue(message.getText().toString());
        //myRef.child(senderid).child(receiverid).child(currentDateandTime).child("time").setValue(currentDateandTime);
        //ChatBubbles.add(ChatBubble);
        //adapter.notifyDataSetChanged();
        //scrollMyListViewToBottom();
        message.setText("");
        //if (myMessage) {
         //   myMessage = false;
        //} else {
        //    myMessage = true;
        //}
    }
}
    private void scrollMyListViewToBottom() {
        chatlist.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatlist.setSelection(adapter.getCount() - 1);
            }
        });
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        adapter.notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        adapter.notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}
