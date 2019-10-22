package com.example.a17_friends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    
    private Toolbar ChatToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,NotificationRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private ProgressDialog loadingBar;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "",myUrl="";
    private String messageSenderIDGame,messageReceiverIDGame;
    private StorageTask uploadTask;
    private Uri fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();


        IntializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        DisplayLastSeen();

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[] = new CharSequence[]
                        {
                          "上傳圖片",
                          "小遊戲",
                          "視訊",
                          "通話"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        if(i == 0)
                        {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"), 438);
                        }
                        if(i == 1 )
                        {
                            CharSequence GameOptions[] = new CharSequence[]
                                    {
                                            "猜拳",
                                            "真心話大冒險",
                                            "你畫我猜",
                                            "夫妻臉"
                                    };


                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setTitle("遊戲大廳");
                            builder.setItems(GameOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i)
                                {
                                    if(i == 0)
                                    {
                                        CharSequence MoraOptions[] = new CharSequence[]
                                                {
                                                        "剪刀",
                                                        "石頭",
                                                        "布",
                                                        "取消猜拳要求",
                                                };


                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                        builder.setTitle("猜拳");
                                        builder.setItems(MoraOptions, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i)
                                            {
                                                if(i == 0)
                                                {
                                                    MessageInputText.setText("小遊戲(猜拳):"+"\n"+"對方已選擇");
                                                    SendMessage();
                                                    SendGameMessage("scissors");
                                                }
                                                if(i == 1 )
                                                {
                                                    MessageInputText.setText("小遊戲(猜拳):"+"\n"+"對方已選擇");
                                                    SendMessage();
                                                    SendGameMessage("rock");
                                                }
                                                if(i == 2)
                                                {
                                                    MessageInputText.setText("小遊戲(猜拳):"+"\n"+"對方已選擇");
                                                    SendMessage();
                                                    SendGameMessage("paper");
                                                }
                                                if(i == 3)
                                                {
                                                    MessageInputText.setText("小遊戲(猜拳):"+"\n"+"對方已取消");
                                                    SendMessage();
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                    if(i == 1 )
                                    {
                                        CharSequence TruthOptions[] = new CharSequence[]
                                                {
                                                        "真心話",
                                                        "大冒險",
                                                };


                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                        builder.setTitle("真心話大冒險");
                                        builder.setItems(TruthOptions, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i)
                                            {
                                                if(i == 0)
                                                {
                                                    Random x=new Random();
                                                    int y=x.nextInt(23);
                                                    String truth[]=getResources().getStringArray(R.array.truth);
                                                    MessageInputText.setText("小遊戲(真心話):"+"\n"+truth[y]);
                                                    SendMessage();
                                                }
                                                if(i == 1 )
                                                {
                                                    Random x=new Random();
                                                    int y=x.nextInt(4);
                                                    String adventure[]=getResources().getStringArray(R.array.adventure);
                                                    MessageInputText.setText("小遊戲(大冒險):"+"\n"+adventure[y]);
                                                    SendMessage();
                                                }
                                            }
                                        });
                                        builder.show();
                                    }

                                    if(i == 2)
                                    {
                                        Intent loginIntent = new Intent (ChatActivity.this, DrawActivity.class);
                                        startActivity(loginIntent);
                                    }
                                    if(i == 3)
                                    {

                                    }

                                }
                            });
                            builder.show();
                        }

                        if(i == 2)
                        {

                        }
                        if(i == 3)
                        {

                        }

                    }
                });
                builder.show();
            }

        });

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        RootRef.child("Game").child(messageSenderID).child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String getwhere = dataSnapshot.child("from").getValue(String.class);
                if (getwhere !=null && getwhere.equals(messageSenderID)){
                    messageSenderIDGame = dataSnapshot.child("Game").getValue(String.class);
                    if(messageReceiverIDGame== null){}
                    else{
                        GameMission();

                    }
                } else if(getwhere !=null && getwhere.equals(messageReceiverID) ) {
                    messageReceiverIDGame = dataSnapshot.child("Game").getValue(String.class);
                    if(messageSenderIDGame == null){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("對方出拳了 換你出拳")
                                .setMessage("對方可能是剪刀");
                        dialog.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                    }
                    else{
                        GameMission();
                        }

                }
                else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void GameMission(){
        if(messageSenderIDGame.equals("scissors") && messageReceiverIDGame.equals("scissors")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("平手")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();

        }
        if(messageSenderIDGame.equals("scissors") && messageReceiverIDGame.equals("rock")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你輸了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("scissors") && messageReceiverIDGame.equals("paper")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你贏了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("rock") && messageReceiverIDGame.equals("scissors")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你贏了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("rock") && messageReceiverIDGame.equals("rock")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("平手")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("rock") && messageReceiverIDGame.equals("paper")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你輸了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("paper") && messageReceiverIDGame.equals("scissors")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你輸了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("paper") && messageReceiverIDGame.equals("rock")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("你贏了")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
        if(messageSenderIDGame.equals("paper") && messageReceiverIDGame.equals("paper")){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("平手")
                    .setMessage(messageSenderIDGame+"和"+messageReceiverIDGame);
            dialog1.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ChatActivity.this, "結束", Toast.LENGTH_SHORT).show();
                }
            });
            dialog1.show();
            DeleteGameMessage();
        }
    }

    private  void SendGameMessage(String v)
    {
        {
            String messageSenderRef = "Game/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Game/" + messageReceiverID + "/" + messageSenderID;

            Map messageTextBody = new HashMap();
            messageTextBody.put("Game", v);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/"  , messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/"  , messageTextBody);
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "傳送成功...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "錯誤", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private  void DeleteGameMessage()
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Game")
                .child(messageSenderID).child(messageReceiverID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Game")
                            .child(messageReceiverID).child(messageSenderID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            messageReceiverIDGame =null;
                            messageSenderIDGame  =null;
                        }
                    });

                }


            }
});
    }
    private void SendMessage()
    {
        final String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "請先輸入訊息...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "傳送成功...", Toast.LENGTH_SHORT).show();

                        NotificationRef = FirebaseDatabase.getInstance().getReference().child("NotificationsChat");

                        HashMap<String, String> chatNotificationMap = new HashMap<>();
                        chatNotificationMap.put("from", messageSenderID);
                        chatNotificationMap.put("message", messageText);

                        NotificationRef.child(messageReceiverID).push()
                                .setValue(chatNotificationMap);
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "錯誤", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }

    private void IntializeControllers() {


        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {
            loadingBar.setTitle("設定檔案");
            loadingBar.setMessage("正在上傳檔案請稍後...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            fileUrl = data.getData();

            if(!checker.equals("image"))
            {

            }
            else  if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUrl);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }


                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUrl.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "傳送成功...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "錯誤", Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this,"錯誤:沒有任何圖像",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private  void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("最後上線時間:" + date + " " );
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
