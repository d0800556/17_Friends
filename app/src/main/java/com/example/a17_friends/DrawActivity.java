package com.example.a17_friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DrawActivity extends AppCompatActivity {
    private Button btn_save, btn_resume;
    private ImageView iv_canvas;
    private Bitmap baseBitmap,firebasebitmap;
    private Canvas canvas;
    private Paint paint;
    private TextView TV1;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageTask uploadTask;
    private String checker = "",myUrl="";
    private String saveCurrentTime, saveCurrentDate;
    private Uri fileUrl;
    private ProgressDialog loadingBar;

    private String messageReceiverID,messageSenderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        // 初始化一个画笔，笔触宽度为5，颜色为红色
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        if (getIntent().hasExtra("visit_user_id")) {
            messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        }
        RootRef = FirebaseDatabase.getInstance().getReference();
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        TV1 = (TextView)findViewById(R.id.TV1);
        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_resume = (Button) findViewById(R.id.btn_resume);

        btn_save.setOnClickListener(click);
        btn_resume.setOnClickListener(click);
        iv_canvas.setOnTouchListener(touch);
        loadingBar = new ProgressDialog(this);

        Random x=new Random();
        int y=x.nextInt(50);
        String drawguess[]=getResources().getStringArray(R.array.drawguess);
        TV1.setText(drawguess[y]);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    private View.OnTouchListener touch = new View.OnTouchListener() {
        // 定义手指开始触摸的坐标
        float startX;
        float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                // 用户按下动作
                case MotionEvent.ACTION_DOWN:
                    // 第一次绘图初始化内存图片，指定背景为白色
                    if (baseBitmap == null) {
                        baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
                                iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }
                    // 记录开始触摸的点的坐标
                    startX = event.getX();
                    startY = event.getY();
                    break;
                // 用户手指在屏幕上移动的动作
                case MotionEvent.ACTION_MOVE:
                    // 记录移动位置的点的坐标
                    float stopX = event.getX();
                    float stopY = event.getY();

                    //根据两点坐标，绘制连线
                    canvas.drawLine(startX, startY, stopX, stopY, paint);

                    // 更新开始点的位置
                    startX = event.getX();
                    startY = event.getY();

                    // 把图片展示到ImageView中
                    iv_canvas.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_save:
                    saveBitmap();
                    break;
                case R.id.btn_resume:
                    resumeCanvas();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 保存图片到SD卡上
     */
    protected void saveBitmap() {
        try {


            loadingBar.setTitle("保存中");
            loadingBar.setMessage("正在上傳保存圖畫請稍候...");
            loadingBar.show();
            // 保存图片到SD卡上
            File file = new File(Environment.getExternalStorageDirectory()+"/Download/"+
                    System.currentTimeMillis() + ".JPG");
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Toast.makeText(DrawActivity.this, "保存图片成功", Toast.LENGTH_SHORT).show();

            // Android设备Gallery应用只会在启动的时候扫描系统文件夹
            // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.parse("file://" +Environment.getExternalStorageDirectory()+"/Download/"));
                sendBroadcast(mediaScanIntent);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
                intent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()+"/Download/"));
                sendBroadcast(intent);
            }
            uploadFile();
        } catch (Exception e) {
            Toast.makeText(DrawActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * 清除画板
     */
    protected void resumeCanvas() {
        // 手动清除画板的绘图，重新创建一个画板
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
                    iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            iv_canvas.setImageBitmap(baseBitmap);
            Toast.makeText(DrawActivity.this, "清除畫板成功，可以重新開始繪圖", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFile() {


            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
            final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();
            final String messagePushID = userMessageKeyRef.getKey();

            final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] data2 = outputStream.toByteArray();
           uploadTask = filePath.putBytes(data2);

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
                    messageTextBody.put("name", messagePushID + "." + "jpg");
                    messageTextBody.put("type", "image");
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
                                Toast.makeText(DrawActivity.this, "傳送成功...", Toast.LENGTH_SHORT).show();
                               finish();
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(DrawActivity.this, "上傳失敗...", Toast.LENGTH_SHORT).show();
                                finish();

                            } }
                    });
                }
            }
        });

        }
    }

