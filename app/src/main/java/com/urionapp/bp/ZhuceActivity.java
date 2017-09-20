package com.urionapp.bp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.example.urionbean.User;
import com.example.uriondb.DBOpenHelper;
import com.urionapp.bp.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ZhuceActivity extends Activity implements OnClickListener {
    private ImageButton save, ignore, home, thread, history;
    private ImageView touxiangs;
    private EditText nameet, ageet, heightet, weightet;
    private RadioGroup radiogroup;
    private RadioButton man, femal;
    private Button nname;
    private User user;
    private String ggname;
    private String sex = "";
    private DBOpenHelper dbOpenHelper;
    private Intent intent;
    private boolean zc = true;
    Drawable drawable;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce);
        thread = (ImageButton) this.findViewById(R.id.treads);
        home = (ImageButton) this.findViewById(R.id.home);
        history = (ImageButton) this.findViewById(R.id.history1);
        save = (ImageButton) this.findViewById(R.id.save);
        ignore = (ImageButton) this.findViewById(R.id.delete);
        touxiangs = (ImageView) this.findViewById(R.id.image);
        nameet = (EditText) this.findViewById(R.id.name);
        ageet = (EditText) this.findViewById(R.id.age);
        heightet = (EditText) this.findViewById(R.id.height);
        weightet = (EditText) this.findViewById(R.id.weight);
        nname = (Button) this.findViewById(R.id.user);
        ggname = getIntent().getExtras().getString("gname");
        radiogroup = (RadioGroup) this.findViewById(R.id.group);
        man = (RadioButton) this.findViewById(R.id.man);
        femal = (RadioButton) this.findViewById(R.id.femal);
        dbOpenHelper = new DBOpenHelper(ZhuceActivity.this);
        home.setOnClickListener(this);
        thread.setOnClickListener(this);
        history.setOnClickListener(this);
        save.setOnClickListener(this);
        ignore.setOnClickListener(this);
        touxiangs.setOnClickListener(this);
        Drawable d = touxiangs.getBackground();
        BitmapDrawable bd = (BitmapDrawable) d;
        photo = bd.getBitmap();
        nname.setText(ggname);
        nameet.setText(ggname);
        if (ggname.equals("User")) {
            nameet.setText("");
        } else {
            nameet.setText(ggname);
            nameet.setFocusable(false);
            select();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.treads:
            Intent serverIntent = new Intent(ZhuceActivity.this,
                                             TwoActivity.class);
            ggname = "User";
            serverIntent.putExtra("gname", ggname);
            startActivity(serverIntent);
            break;
        case R.id.history1:
            Intent two = new Intent(ZhuceActivity.this, ThreadActivity.class);
            ggname = "User";
            two.putExtra("gname", ggname);
            startActivity(two);
            break;
        case R.id.home:
            Intent hh = new Intent(ZhuceActivity.this, MainActivity.class);
            ggname = "User";
            hh.putExtra("gname", ggname);
            startActivity(hh);
            break;
        case R.id.save:
            if (nameet.isFocusable()) {
                zhuce();
            } else {
                nameet.setText(ggname);
                updates();
            }
            break;
        case R.id.image:
            ShowPickDialog();
            break;
        case R.id.delete:
            new AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(
                "All the information that whether you want to delete this user？")
            .setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which) {
                    delete();
                    Intent tip = new Intent(ZhuceActivity.this,
                                            MainActivity.class);
                    startActivity(tip);
                }
            })
            .setNegativeButton("No",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which) {
                }
            }).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 选择提示对话框
     */
    private void ShowPickDialog() {
        new AlertDialog.Builder(this)
        .setTitle("Portrait settings...")
        .setNegativeButton("Photo album",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK,
                                           null);
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
                startActivityForResult(intent, 1);
            }
        })
        .setPositiveButton("Take a photo",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int whichButton) {
                dialog.dismiss();
                Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                .fromFile(new File(Environment
                                                   .getExternalStorageDirectory(),
                                                   "xiaoma.jpg")));
                startActivityForResult(intent, 2);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        // 如果是直接从相册获取
        case 1:
            startPhotoZoom(data.getData());
            break;
        // 如果是调用相机拍照时
        case 2:
            File temp = new File(Environment.getExternalStorageDirectory()
                                 + "/xiaoma.jpg");
            startPhotoZoom(Uri.fromFile(temp));
            break;
        // 取得裁剪后的图片
        case 3:
            if (data != null) {
                setPicToView(data);
            }
            break;
        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            drawable = new BitmapDrawable(photo);
            touxiangs.setBackgroundDrawable(drawable);
        }
    }

    public void delete() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("user", "name = ?", new String[] { ggname });
        db.delete("sdp", "name = ?", new String[] { ggname });
        db.close();
    }

    public void updates() {
        String ages = ageet.getText().toString();
        int age = Integer.parseInt(ages);
        String heights = heightet.getText().toString();
        int height = Integer.parseInt(heights);
        String weights = weightet.getText().toString();
        int weight = Integer.parseInt(weights);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, os);
        String sex = "";
        int s = radiogroup.getCheckedRadioButtonId(); // 获取性别
        if (s == R.id.man) {
            sex = "Male";
        } else {
            sex = "Female";
        }
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL(
            "update user set sex=?,age=?,height=?,weight=?,touxiang=? where name=?",
            new Object[] { sex, age, height, weight, os.toByteArray(),
                           ggname
                         });
        db.close();
        Intent back = new Intent();
        back.putExtra("bname", ggname);
        setResult(20, back);
        finish();
    }

    public void select() {
        dbOpenHelper = new DBOpenHelper(ZhuceActivity.this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user",
                                 new String[] { "sex,age,height,weight,touxiang" }, "name=?",
                                 new String[] { ggname }, null, null, null);
        while (cursor.moveToNext()) {
            String sex = cursor.getString(cursor.getColumnIndex("sex"));
            String age = cursor.getString(cursor.getColumnIndex("age"));
            String height = cursor.getString(cursor.getColumnIndex("height"));
            String weight = cursor.getString(cursor.getColumnIndex("weight"));
            byte[] in = cursor.getBlob(cursor.getColumnIndex("touxiang"));
            photo = BitmapFactory.decodeByteArray(in, 0, in.length);
            drawable = new BitmapDrawable(photo);
            touxiangs.setBackgroundDrawable(drawable);
            ageet.setText(age);
            if (sex.equals("male")) {
                radiogroup.check(R.id.man);
            } else {
                radiogroup.check(R.id.femal);
            }
            heightet.setText(height);
            weightet.setText(weight);
        }
        cursor.close(); // 游标使用完成后记得关闭
        db.close();
    }

    public void zhuce() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String sex = "";
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, os);
        int s = radiogroup.getCheckedRadioButtonId(); // 获取性别
        if (s == R.id.man) {
            sex = "Male";
        } else {
            sex = "Female";
        }
        if (nameet == null || nameet.getText().toString().length() == 0) {
            Toast.makeText(this, "The user name can not be empty！",
                           Toast.LENGTH_SHORT).show();
            nameet.setText("");
            zc = false;
        } else {
            String zname = nameet.getText().toString();
            Cursor cursor = db.query("user", new String[] { "name" }, null,
                                     null, null, null, null);
            while (cursor.moveToNext()) {
                String ssname = cursor.getString(cursor.getColumnIndex("name"));
                if (ssname.equals(zname)) {
                    Toast.makeText(this, "User name already exists！",
                                   Toast.LENGTH_SHORT).show();
                    nameet.setText("");
                    zc = false;
                } else {
                    zc = true;
                }
            }
            if (weightet == null || weightet.getText().toString().length() == 0) {
                Toast.makeText(this, "The weight can not be empty！",
                               Toast.LENGTH_SHORT).show();
                weightet.setText("");
                zc = false;
            }
            if (heightet == null || heightet.getText().toString().length() == 0) {
                Toast.makeText(this, "The height can not be empty！",
                               Toast.LENGTH_SHORT).show();
                heightet.setText("");
                zc = false;
            }
            if (ageet == null || ageet.getText().toString().length() == 0) {
                Toast.makeText(this, "The age can not be empty！",
                               Toast.LENGTH_SHORT).show();
                ageet.setText("");
                zc = false;
            }
        }
        if (zc == true) {
            String name = nameet.getText().toString();
            System.out.println("-----------" + name);
            String ages = ageet.getText().toString();
            int age = Integer.parseInt(ages);
            System.out.println("-----------" + age);
            String heights = heightet.getText().toString();
            int height = Integer.parseInt(heights);
            String weights = weightet.getText().toString();
            int weight = Integer.parseInt(weights);
            db.execSQL(
                "insert into user(name,sex,age,height,weight,touxiang) values(?,?,?,?,?,?)",
                new Object[] { name, sex, age, height, weight,
                               os.toByteArray()
                             });
            System.out.println("aa..........");
            db.close();
            Intent back = new Intent();
            back.putExtra("bname", name);
            setResult(20, back);
            finish();
        }
    }

}
