package com.Aniket.example.letstalk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Aniket.example.letstalk.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding binding;
    FirebaseAuth auth;
    String backendOtp;
    String phoneNumber;
    //EditText inputOtp;
    Button verifybtn;
    EditText inputnumber1, inputnumber2, inputnumber3, inputnumber4, inputnumber5, inputnumber6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setTitle("Verify Your Phone Number");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0088cc")));

        inputnumber1 = findViewById(R.id.inputotp1);
        inputnumber2 = findViewById(R.id.inputotp2);
        inputnumber3 = findViewById(R.id.inputotp3);
        inputnumber4 = findViewById(R.id.inputotp4);
        inputnumber5 = findViewById(R.id.inputotp5);
        inputnumber6 = findViewById(R.id.inputotp6);

        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        ProgressBar progressBar = findViewById(R.id.progressbar_sendotp);
        verifybtn = findViewById(R.id.verifybutton);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        backendOtp = getIntent().getStringExtra("backendotp");

        TextView phoneLbl = findViewById(R.id.phoneLbl);
        phoneLbl.setText(String.format("Verify +91%s", phoneNumber));

        //inputOtp = findViewById(R.id.inputOtp);

        verifybtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            verifybtn.setVisibility(View.INVISIBLE);
            if (inputnumber1.getText().toString().trim().isEmpty() ||
                    inputnumber2.getText().toString().trim().isEmpty() ||
                    inputnumber3.getText().toString().trim().isEmpty() ||
                    inputnumber4.getText().toString().trim().isEmpty() ||
                    inputnumber5.getText().toString().trim().isEmpty() ||
                    inputnumber6.getText().toString().trim().isEmpty()) {
                progressBar.setVisibility(View.GONE);
                verifybtn.setVisibility(View.VISIBLE);
                Toast.makeText(OtpActivity.this, "Please Enter OTP", Toast.LENGTH_LONG).show();
            } else {
                if(backendOtp != null){
                    String enteredotp = inputnumber1.getText().toString() +
                            inputnumber2.getText().toString() +
                            inputnumber3.getText().toString() +
                            inputnumber4.getText().toString() +
                            inputnumber5.getText().toString() +
                            inputnumber6.getText().toString();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendOtp, enteredotp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        verifybtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(OtpActivity.this, "Phone Number Verified!!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(OtpActivity.this, SetProfile.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        verifybtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(OtpActivity.this, "Incorrect Otp", Toast.LENGTH_LONG).show();
                                    }

                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    verifybtn.setVisibility(View.VISIBLE);
                    Toast.makeText(OtpActivity.this, "Check Internet Collection", Toast.LENGTH_LONG).show();
                }
            }
        });
        numberOtpMove();
    }

    private void numberOtpMove() {
        inputnumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputnumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputnumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputnumber3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputnumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputnumber4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputnumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputnumber5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputnumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputnumber6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}