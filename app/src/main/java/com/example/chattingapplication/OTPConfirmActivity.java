package com.example.chattingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPConfirmActivity extends AppCompatActivity {

    private String phonenumber, verificationId;

    FirebaseAuth mAuth;

    private EditText enteredOTP;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);

        phonenumber = getIntent().getStringExtra("phone");
        sendVerificationCode(phonenumber);

        mAuth = FirebaseAuth.getInstance();

        enteredOTP = findViewById(R.id.OTPUser);
        confirm = findViewById(R.id.confirmOTP);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = enteredOTP.getText().toString().trim();



                if (code.isEmpty() || code.length() < 6) {



                    enteredOTP.setError("Enter code...");

                    enteredOTP.requestFocus();

                    return;

                }

                verifyCode(code);
            }
        });

    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithCredential(credential);

    }

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                number,

                60,

                TimeUnit.SECONDS,

                TaskExecutors.MAIN_THREAD,

                mCallBack

        );

    }




    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {



                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                                loadProfileActivity();


                        } else {

                            Toast.makeText(OTPConfirmActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }

                });

    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



        @Override

        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
            Toast.makeText(OTPConfirmActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();

        }



        @Override

        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {

                enteredOTP.setText(code);

                verifyCode(code);

            } else if(phoneAuthCredential.getProvider().equals("phone")) {
                signInWithCredential(phoneAuthCredential);
            }

        }



        @Override

        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(OTPConfirmActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

    };

    private void loadProfileActivity() {

        Intent intent = new Intent(OTPConfirmActivity.this, UserInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


}
