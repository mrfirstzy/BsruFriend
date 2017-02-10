package firstkill.mfz.bsrufriend;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import org.jibble.simpleftp.SimpleFTP;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    //Explicit
    private EditText nameEditText, userEditText, passEditText;
    private ImageView imageView;
    private RadioGroup radioGroup;
    private Button button;
    private String nameString, userString, passString, pathImageString, nameImageString;
    private Uri uri;
    private boolean aBoolean = true;
    private int anInt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Bind Widget
        bindWidget();

        //Button Controller
        buttonController();

        //Image Controller
        ImageController();

        //Radio Controller
        radioController();

    }   // Main Method

    private void radioController() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButton:
                        anInt = 0;
                        break;
                    case R.id.radioButton2:
                        anInt = 1;
                        break;
                    case R.id.radioButton3:
                        anInt = 2;
                        break;
                    case R.id.radioButton4:
                        anInt = 3;
                        break;
                    case R.id.radioButton5:
                        anInt = 4;
                        break;
                }   //switch
            }   //onChecked
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            aBoolean = false;
            uri = data.getData();
            // Setup image Choose to ImageView
            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Find Path of Image Choose
            String[] strings = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, strings, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                pathImageString = cursor.getString(index);

            } else {
                pathImageString = uri.getPath();
            }

            Log.d("10febV1", "pathImage ==>" + pathImageString);


        }   //if


    }   //onActivityResult


    private void ImageController() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกแอพดูภาพ"), 1);

            }   //onCick
        });

    }

    private void buttonController() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get value From Edit text
                nameString = nameEditText.getText().toString().trim();
                userString = userEditText.getText().toString().trim();
                passString = passEditText.getText().toString().trim();

                //check Space
                if (nameString.equals("") || userString.equals("") || passString.equals("")) {
                    // Ture ==> Have Space
                    MyAlert myAlert = new MyAlert(SignUpActivity.this);
                    myAlert.mydialog("มีช่องว่าง", "กรุณากรอกให้ครบช่องครับ");
                } else if (aBoolean) {
                    //Non Choose Image
                    MyAlert myAlert = new MyAlert(SignUpActivity.this);
                    myAlert.mydialog("ยังไม่เลือกรูปภาพ", "กรุณาเลือกรูปด้วยเว้ยเห้ย");

                } else {
                    //EveryThing OK

                    uploadValueToServer();

                }
            }

        });   //onClick
    }

    private void uploadValueToServer() {

        try {

            //Upload Imge
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy
                    .Builder()
                    .permitAll()
                    .build();
            StrictMode.setThreadPolicy(policy);
            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com", 21, "bsru@swiftcodingthai.com",
                    "Abc12345");
            simpleFTP.bin();
            simpleFTP.cwd("Image_MFz");
            simpleFTP.stor(new File(pathImageString));
            simpleFTP.disconnect();

            // Upload Text
            String tag = "10febV2";
            Log.d(tag, "name ==> " + nameString);
            Log.d(tag, "user ==> " + userString);
            Log.d(tag, "password ==> " + passString);

            nameImageString = "http://swiftcodingthai.com/bsru/Image_MFz" + pathImageString.substring(pathImageString.lastIndexOf("/"));
            Log.d(tag, "Image ==>" + nameImageString);
            Log.d(tag, "Avata ==>" + anInt);

            AddValueToUser addValueToUser = new AddValueToUser(SignUpActivity.this,
                    nameString, userString, passString, nameImageString, Integer.toString(anInt));
            addValueToUser.execute("http://swiftcodingthai.com/bsru/add_master.php");
            String s = addValueToUser.get();
            Log.d(tag, "Result ==>" + s);

            if (Boolean.parseBoolean(s)) {
                finish();
            } else {
                MyAlert myAlert = new MyAlert(SignUpActivity.this);
                myAlert.mydialog("Cannot Upload", "Upload Fail");
            }



        } catch (Exception e) {
            Log.d("10febV1", "e upload ==>" + e.toString());
        }

    }   // upload

    private void bindWidget() {

        nameEditText = (EditText) findViewById(R.id.editText2);
        userEditText = (EditText) findViewById(R.id.editText4);
        passEditText = (EditText) findViewById(R.id.editText5);
        imageView = (ImageView) findViewById(R.id.imageView3);
        radioGroup = (RadioGroup) findViewById(R.id.ragAvata);
        button = (Button) findViewById(R.id.button3);

    }   //bindWidget

}   // Main Class
