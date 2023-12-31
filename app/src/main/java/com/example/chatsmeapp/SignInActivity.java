package com.example.chatsmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatsmeapp.Models.Users;
import com.example.chatsmeapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    private EditText txtEmail;
    ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        txtEmail=findViewById(R.id.txtEmail);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("please Wait\n Vlaidation in progress ");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if(!binding.txtEmail.getText().toString().isEmpty() && !binding.txtPassword.getText().toString().isEmpty())
               {
                   progressDialog.show();
                   mAuth.signInWithEmailAndPassword(binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString())
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   progressDialog.dismiss();
                                   if(task.isSuccessful())
                                   {
                                       Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                                       startActivity(intent);
                                   }
                                   else
                                   {
                                       Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });
               }
               else {
                   Toast.makeText(SignInActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
               }
           }
       });
  if(mAuth.getCurrentUser()!=null)
  {
      Intent intent =new Intent(SignInActivity.this,MainActivity.class);
      startActivity(intent);
  }
        binding.txtClickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });



  binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          signIn();
      }
  });
    }
    int RC_SIGN_IN=65;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e)
            {
                Log.w("TAG","Google sign in failed", e);
            }
        }
    }

   private  void firebaseAuthWithGoogle(String idToken){
    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
    mAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();

                Users users=new Users();
                users.setUserId(user.getUid());
                users.setUserName(user.getDisplayName());
                users.setProfilePic(user.getPhotoUrl().toString());
                firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(users);

                Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(SignInActivity.this, "Sign in with Google", Toast.LENGTH_SHORT).show();

            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.getException());

            }
        }
    });
}

    public void reset(View view) {
        if(txtEmail.length() ==0)
        {
            Toast.makeText(this, "Please Enter Your Email ID", Toast.LENGTH_SHORT).show();
        }
        else
        {
            FirebaseAuth.getInstance().setLanguageCode("en"); // Set to English
            FirebaseAuth.getInstance().sendPasswordResetEmail(txtEmail.getText().toString());
            Toast.makeText(this, "Reset request sent on your email", Toast.LENGTH_SHORT).show();
        }
    }
}
