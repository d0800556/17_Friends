package com.example.a17_friends;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserID, senderUserID, Current_State;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus,Self_introduction,interest,local,age,gender;
    private Button SendMessageRequestButton, DeclineMessageRequestButton;

    private DatabaseReference UserRef,ChatRequestRef,ContactsRef,NotificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        
        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();


        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        Self_introduction = (TextView) findViewById(R.id.Self_introduction);
        interest = (TextView) findViewById(R.id.interest);
        local = (TextView) findViewById(R.id.local);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);
        Current_State = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {

        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists())  &&  (dataSnapshot.hasChild("image")))
                {
                    String userGender = dataSnapshot.child("gender").getValue().toString();
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String userSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();
                    String userInterest1 = dataSnapshot.child("interest1").getValue().toString();
                    String userInterest2 = dataSnapshot.child("interest2").getValue().toString();
                    String userInterest3 = dataSnapshot.child("interest3").getValue().toString();
                    String userInterest4 = dataSnapshot.child("interest4").getValue().toString();
                    String userLocal = dataSnapshot.child("local").getValue().toString();
                    String useRage = dataSnapshot.child("age").getValue().toString();

                    Resources res = getResources();
                    String[] GenderPlanets = res.getStringArray(R.array.gander);
                    String UserRealGender = GenderPlanets[Integer.parseInt(userGender)];
                    String[] LocalPlanets = res.getStringArray(R.array.local);
                    String UserRealLocal = LocalPlanets[Integer.parseInt(userLocal)];
                    String[] InterestPlanets = res.getStringArray(R.array.interest);
                    String UserRealInterest1 = InterestPlanets[Integer.parseInt(userInterest1)];
                    String UserRealInterest2 = InterestPlanets[Integer.parseInt(userInterest2)];
                    String UserRealInterest3 = InterestPlanets[Integer.parseInt(userInterest3)];
                    String UserRealInterest4 = InterestPlanets[Integer.parseInt(userInterest4)];

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    gender.setText(UserRealGender);
                    userProfileStatus.setText(userStatus);
                    Self_introduction.setText(userSelf_introduction);
                    interest.setText(UserRealInterest1+","+UserRealInterest2+","+UserRealInterest3+","+UserRealInterest4);
                    local.setText(UserRealLocal);
                    age.setText(useRage);

                    ManageChatRequests();
                }
                else
                {
                    String userGender = dataSnapshot.child("gender").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String userSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();
                    String userInterest1 = dataSnapshot.child("interest1").getValue().toString();
                    String userInterest2 = dataSnapshot.child("interest2").getValue().toString();
                    String userInterest3 = dataSnapshot.child("interest3").getValue().toString();
                    String userInterest4 = dataSnapshot.child("interest4").getValue().toString();
                    String userLocal = dataSnapshot.child("local").getValue().toString();
                    String useRage = dataSnapshot.child("age").getValue().toString();

                    Resources res = getResources();
                    String[] GenderPlanets = res.getStringArray(R.array.gander);
                    String UserRealGender = GenderPlanets[Integer.parseInt(userGender)];
                    String[] LocalPlanets = res.getStringArray(R.array.local);
                    String UserRealLocal = LocalPlanets[Integer.parseInt(userLocal)];
                    String[] InterestPlanets = res.getStringArray(R.array.interest);
                    String UserRealInterest1 = InterestPlanets[Integer.parseInt(userInterest1)];
                    String UserRealInterest2 = InterestPlanets[Integer.parseInt(userInterest2)];
                    String UserRealInterest3 = InterestPlanets[Integer.parseInt(userInterest3)];
                    String UserRealInterest4 = InterestPlanets[Integer.parseInt(userInterest4)];

                    userProfileName.setText(userName);
                    gender.setText(UserRealGender);
                    userProfileStatus.setText(userStatus);
                    Self_introduction.setText(userSelf_introduction);
                    interest.setText(UserRealInterest1+","+UserRealInterest2+","+UserRealInterest3+","+UserRealInterest4);
                    local.setText(UserRealLocal);
                    age.setText(useRage);

                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequests() {

        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiverUserID))
                        {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
                                SendMessageRequestButton.setText("取消好友邀請");
                            }
                            else if (request_type.equals("received"))
                            {
                                Current_State = "request_received";
                                SendMessageRequestButton.setText("同意好友邀請");

                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);

                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            ContactsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(receiverUserID))
                                            {
                                                Current_State = "friends";
                                                SendMessageRequestButton.setText("刪除好友");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if (!senderUserID.equals(receiverUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    SendMessageRequestButton.setEnabled(false);

                    if (Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }

                    if (Current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }
                }
            });
        }
        else
        {
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                SendMessageRequestButton.setText("傳送邀請");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    SendMessageRequestButton.setText("刪除好友");

                                                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                SendMessageRequestButton.setText("傳送邀請");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this,R.style.AlertDialog);
        builder.setTitle("打聲招呼吧:");

        final EditText SendChatField = new EditText(ProfileActivity.this);
        SendChatField.setHint("可以跟你做個朋友嗎?");
        builder.setView(SendChatField);

        builder.setPositiveButton("送出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String SendChat = SendChatField.getText().toString();

                if (TextUtils.isEmpty(SendChat))
                {
                    SendChat = "可以跟你做個朋友嗎?";
                }

                final String finalSendChat = SendChat;
                ChatRequestRef.child(senderUserID).child(receiverUserID)
                        .child("request_type").setValue("sent")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                ChatRequestRef.child(receiverUserID).child(senderUserID)
                                        .child("text").setValue(finalSendChat);
                                ChatRequestRef.child(receiverUserID).child(senderUserID)
                                        .child("request_type").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                    chatNotificationMap.put("from", senderUserID);
                                                    chatNotificationMap.put("type", "request");
                                                    NotificationRef.child(receiverUserID).push()
                                                            .setValue(chatNotificationMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        SendMessageRequestButton.setEnabled(true);
                                                                        Current_State = "request_sent";
                                                                        SendMessageRequestButton.setText("取消好友邀請");
                                                                    }
                                                                }
                                                            });
                                                    Toast.makeText(ProfileActivity.this, " 邀請寄送成功!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                }
                            });
                }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();

    }
}
