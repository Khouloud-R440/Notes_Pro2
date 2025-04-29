package com.example.notespro;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button SignUpBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            emailEditText = findViewById(R.id.email_edit_text);
            passwordEditText = findViewById(R.id.password_edit_text);
            confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
            SignUpBtn = findViewById(R.id.Sign_Up_btn);
            progressBar = findViewById(R.id.progress_Bar);
            loginBtnTextView = findViewById(R.id.login_text_view_btn);
            loginBtnTextView.setOnClickListener(v1 -> finish());
            SignUpBtn.setOnClickListener(v1 -> SignUp());

            return insets;
        });

    }
    void SignUp(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String ConfirmPassword = confirmPasswordEditText.getText().toString();
        boolean isValidated = validateData(email,password,ConfirmPassword);
        if (!isValidated){
            return;
        }
        SignUpInFirebase(email,password);
    }
    void SignUpInFirebase(String email,String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    //creating account is done
                    Utility.showToast(CreateAccountActivity.this, "Successfuly create account ,Check email to verify");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else {
                    //failure
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());

                }

            }
        });

    }
    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            SignUpBtn.setVisibility(View.GONE);

        }
        else{
            progressBar.setVisibility(View.GONE);
            SignUpBtn.setVisibility(View.VISIBLE);

        }
    }
    boolean validateData(String email,String password,String confirmPassword){

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;

        }
        if (password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("password not matched");
            return false;

        }
        return true;
    }
}