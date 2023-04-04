package com.Aniket.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Aniket.example.letstalk.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {

    FirebaseAuth auth;
    String backendOtp;
    ActivityPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Phone Number Verification");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0088cc")));

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(binding.phonebox.getText().toString().trim().isEmpty())) {
                    if ((binding.phonebox.getText().toString().trim().length()) == 10) {
                        binding.progressbarSendotp.setVisibility(View.VISIBLE);
                        binding.continuebtn.setVisibility(View.INVISIBLE);

                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber("+91" + binding.phonebox.getText().toString())
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(PhoneNumberActivity.this)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        binding.progressbarSendotp.setVisibility(View.GONE);
                                        binding.continuebtn.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        binding.progressbarSendotp.setVisibility(View.GONE);
                                        binding.continuebtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(PhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(backendOtp, forceResendingToken);
                                        Toast.makeText(PhoneNumberActivity.this, "OTP sent successfully", Toast.LENGTH_LONG).show();
                                        binding.progressbarSendotp.setVisibility(View.GONE);
                                        binding.continuebtn.setVisibility(View.VISIBLE);
                                        String codeSent = s;
                                        Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
                                        intent.putExtra("phoneNumber", binding.phonebox.getText().toString());
                                        intent.putExtra("backendotp", codeSent);
                                        startActivity(intent);
                                    }
                                }).build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    } else {
                        Toast.makeText(PhoneNumberActivity.this, "Please enter Correct Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhoneNumberActivity.this, "Please provide phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}