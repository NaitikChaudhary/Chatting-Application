package com.example.chattingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText mPhoneNoText;
    Button sendOTPBtn;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNoText = findViewById(R.id.phoneNoUse);
        sendOTPBtn = findViewById(R.id.sendOTPButton);

//        Intent i = new Intent(MainActivity.this, UserInfoActivity.class);
//        startActivity(i);

        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPhoneNoText.getText().toString().length() < 10 || mPhoneNoText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Valid phone no.", Toast.LENGTH_SHORT).show();
                } else {
                    phoneNo = "+91"+mPhoneNoText.getText().toString();

                    Intent i = new Intent(MainActivity.this, OTPConfirmActivity.class);
                    i.putExtra("phone", phoneNo);
                    startActivity(i);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Intent intent = new Intent(this, StartActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);



            startActivity(intent);

        }

    }
}
