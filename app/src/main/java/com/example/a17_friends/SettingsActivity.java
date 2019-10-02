package com.example.a17_friends;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName,userStatus,Self_introduction,age;
    private CircleImageView userProfileImage;
    private Spinner interest1,interest2,interest3,interest4,gender;
    private LinearLayout genderlayout;

    private  String currentUserID,StrInterest;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static  final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;
    private Toolbar SettingsToolBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        InitializeFields();


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }


    private void InitializeFields() {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        age = (EditText) findViewById(R.id.set_age_status);
        Self_introduction = (EditText) findViewById(R.id.Self_introduction);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        interest1 = (Spinner) findViewById(R.id.interest1);
        interest2 = (Spinner) findViewById(R.id.interest2);
        interest3 = (Spinner) findViewById(R.id.interest3);
        interest4 = (Spinner) findViewById(R.id.interest4);
        gender = (Spinner) findViewById(R.id.genderSpinner);
        genderlayout= (LinearLayout) findViewById(R.id.genderlayout);
        StrInterest = interest1.getSelectedItem().toString();


        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("帳戶設定");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
            {
            Uri ImageUri = data.getData();


            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }



        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("設定個人圖片");
                loadingBar.setMessage("正在上傳圖片請稍後...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri=result.getUri();//This contains the cropped image

                final StorageReference filePath=UserProfileImagesRef.child(currentUserID+".jpg");//This way we link the userId with image. This is the file name of the image stored in firebase database.

                UploadTask uploadTask=filePath.putFile(resultUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Toast.makeText(SettingsActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                            if (downloadUri != null) {

                                String downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                RootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadingBar.dismiss();
                                        if(!task.isSuccessful()){
                                            String error=task.getException().toString();
                                            Toast.makeText(SettingsActivity.this,"錯誤 : "+error,Toast.LENGTH_LONG).show();
                                        }else{

                                        }
                                    }
                                });
                            }

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(SettingsActivity.this,"錯誤 :",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String setAge = age.getText().toString();
        String setSelf_introduction = Self_introduction.getText().toString();
        String StrGender = gender.getSelectedItem().toString();
        String StrInterest1 = interest1.getSelectedItem().toString();
        String StrInterest2 = interest2.getSelectedItem().toString();
        String StrInterest3 = interest3.getSelectedItem().toString();
        String StrInterest4 = interest4.getSelectedItem().toString();

        if (StrInterest1==StrInterest || StrInterest2==StrInterest || StrInterest3==StrInterest || StrInterest4==StrInterest)
        {
            Toast.makeText(this, "請選擇興趣...", Toast.LENGTH_SHORT).show();
        }
        else  if ( StrInterest1 == StrInterest2 || StrInterest1 == StrInterest3 ||  StrInterest1 == StrInterest4 || StrInterest2 == StrInterest3 || StrInterest2 == StrInterest4 || StrInterest3 == StrInterest4)
        {
            Toast.makeText(this, "興趣重複...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "請輸入使用者名稱...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "請輸入使用者狀態...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setSelf_introduction))
        {
            Toast.makeText(this, "請輸入自我介紹...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(setAge))
        {
            Toast.makeText(this, "請輸入年齡...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> profileMap = new HashMap<>();
                profileMap.put("uid",currentUserID);
                profileMap.put("name",setUserName);
                profileMap.put("age",setAge);
                profileMap.put("gender",StrGender);
                profileMap.put("status",setStatus);
                profileMap.put("Self_introduction",setSelf_introduction);
                profileMap.put("interest1",StrInterest1);
                profileMap.put("interest2",StrInterest2);
                profileMap.put("interest3",StrInterest3);
                profileMap.put("interest4",StrInterest4);
             RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful())
                             {
                                 SendUserToMainActivity();
                                 Toast.makeText(SettingsActivity.this, "個人檔案上傳成功!!", Toast.LENGTH_SHORT).show();
                             }
                             else
                             {
                                 String message = task.getException().toString();
                                 Toast.makeText(SettingsActivity.this, "錯誤 : " + message, Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
        }
    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("gender")))
                        {
                            genderlayout.setVisibility(View.INVISIBLE);
                        }

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))&& (dataSnapshot.hasChild("age")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveAge = dataSnapshot.child("age").getValue().toString();
                            String retrieveSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                            age.setFocusable(false);
                            age.setText(retrieveAge);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Self_introduction.setText(retrieveSelf_introduction);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Self_introduction.setText(retrieveSelf_introduction);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))&& (dataSnapshot.hasChild("age")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveAge = dataSnapshot.child("age").getValue().toString();
                            String retrieveSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();

                            age.setFocusable(false);
                            age.setText(retrieveAge);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Self_introduction.setText(retrieveSelf_introduction);
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveSelf_introduction = dataSnapshot.child("Self_introduction").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Self_introduction.setText(retrieveSelf_introduction);
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "請設定個人資料!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
