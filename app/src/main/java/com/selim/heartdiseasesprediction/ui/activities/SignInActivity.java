package com.selim.heartdiseasesprediction.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.selim.heartdiseasesprediction.databinding.ActivitySignInBinding;
import com.selim.heartdiseasesprediction.utilities.Constants;
import com.selim.heartdiseasesprediction.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding =ActivitySignInBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
//        preferenceManager.clear();
        listeners();
    }
    private void listeners(){
        binding.signInTvCreateOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        binding.signInBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignInDetailsIsValid()){
                    signIn();
                }
            }
        });
        binding.signInTvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,ForgetPasswordActivity.class));
            }
        });

    }

    private void signIn(){
        progressBarLoading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.signInEtEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.signInEtPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                    && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot =task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN , true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.DOC_ID,documentSnapshot.getReference().getId());
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        progressBarLoading(false);
                        showToast("Unable To Sign In");
                    }
                });
    }

    private boolean isSignInDetailsIsValid(){
        if (binding.signInEtEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signInEtEmail.getText().toString()).matches()){
            showToast("Enter Valid Email");
            return false;
        }
        else if (binding.signInEtPassword.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        }
        else {
            return true;
        }
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    public void progressBarLoading(boolean isLoading){
        if (isLoading){
            binding.signInBtnSignIn.setVisibility(View.INVISIBLE);
            binding.signInProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.signInProgressBar.setVisibility(View.INVISIBLE);
            binding.signInBtnSignIn.setVisibility(View.VISIBLE);
        }
    }

}








