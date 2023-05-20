package com.example.firebase_crud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authentication_Activity extends AppCompatActivity {

    EditText et_pwdEmail, et_pwdPassword;
    EditText et_phoneNum, et_phoneOTP;
    Button btn_pwdRegister, btn_pwdSignIn;
    Button btn_getOTP, btn_phoneRegister, sign_Out;
    SignInButton googleSignIn;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private BeginSignInRequest signInRequest;
    private GoogleSignInClient googleSignInClient;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        et_pwdEmail = findViewById(R.id.et_pwdEmail);
        et_pwdPassword = findViewById(R.id.et_pwdPassword);
        btn_pwdRegister = findViewById(R.id.btn_pwdRegister);
        btn_pwdSignIn = findViewById(R.id.btn_pwdSignIn);
        sign_Out = findViewById(R.id.bt_sign_out);
        et_phoneNum = findViewById(R.id.et_phoneNum);
        btn_getOTP = findViewById(R.id.btn_phone_getOTP);
        et_phoneOTP = findViewById(R.id.et_phoneOTP);
        btn_phoneRegister = findViewById(R.id.phone_register);
        googleSignIn = findViewById(R.id.bt_sign_in);

        preferences = getSharedPreferences("myPref", MODE_PRIVATE);
        editor = preferences.edit();
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
//        if (isLoggedIn) {
//            startActivity(new Intent(Authentication_Activity.this, AddData_Activity.class));
//        }

        mAuth = FirebaseAuth.getInstance();
        btn_pwdRegister.setOnClickListener(view -> createUser());
        btn_pwdSignIn.setOnClickListener(view ->
        {
            if (isLoggedIn) {
                Toast.makeText(this, "Signed in user="+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Authentication_Activity.this, ProfileActivity.class);
                intent.putExtra("mAuth",mAuth.getCurrentUser().getEmail());
                startActivity(intent);
            } else {
                signInUser();
            }
        });
        btn_getOTP.setOnClickListener(view -> sendVerificationCode());
        btn_phoneRegister.setOnClickListener(view -> phoneRegister());

        initGoogleSingin();

        googleSignIn.setOnClickListener(view ->
        {
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            startActivityForResult(intent, 100);
        });
        sign_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut().
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful sign out from firebase
                                    mAuth.signOut();
                                    // Display Toast
                                    Log.d("SSS", "onComplete: logged out");
                                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                                    // Finish activity
                                    editor.putBoolean("isLoggedIn", false);
                                    editor.commit();
                                    //finish();
                                }
                            }
                        });
            }

        });
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(et_pwdEmail.getText().toString(), et_pwdPassword.getText().toString())
                .addOnCompleteListener(Authentication_Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RRR", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RRR", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Authentication_Activity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void signInUser() {
        mAuth.signInWithEmailAndPassword(et_pwdEmail.getText().toString(), et_pwdPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SSS", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();
                            Intent intent = new Intent(Authentication_Activity.this, ProfileActivity.class);
                            intent.putExtra("mAuth",mAuth.getCurrentUser().getEmail());
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SSS", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Authentication_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void sendVerificationCode() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + et_phoneNum.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(Authentication_Activity.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void phoneRegister() {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, et_phoneOTP.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //credential = PhoneAuthProvider.getCredential(mVerificationId, et_phoneOTP.getText().toString());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                        editor.putBoolean("isLoggedIn", true);
                            editor.commit();
                            Intent intent = new Intent(Authentication_Activity.this, ProfileActivity.class);
                            intent.putExtra("mAuth",mAuth.getCurrentUser().getEmail());
                            Toast.makeText(Authentication_Activity.this, "Logged in user="+googleSignInClient.asGoogleApiClient().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void initGoogleSingin() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("478236561665-6gp3c0bt56ebb7u7e1eor2bqrqf67gls.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(Authentication_Activity.this, googleSignInOptions);
        //FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // Initialize firebase auth
        //mAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        //FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Initialize sign in intent

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                //displayToast(s);
                // Initialize sign in account
                Toast.makeText(this, "GGG" + s, Toast.LENGTH_SHORT).show();
                Log.d("GGG", "successful");
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {


                                    Log.d("_TAG_", "onComplete: em  -->  "+task.getResult().getUser().getEmail());
                                    Log.d("_TAG_", "onComplete: "+task.getResult().getCredential().toString());
                                    Log.d("_TAG_", "onComplete: un  -->  "+task.getResult().getAdditionalUserInfo().getUsername());
                                    Log.d("_TAG_", "onComplete: pro -->  "+task.getResult().getAdditionalUserInfo().getProfile());




                                    // When task is successful redirect to profile activity display Toast
                                    Log.d("GGG", "Successfull: ");
                                    Toast.makeText(Authentication_Activity.this, "GGG", Toast.LENGTH_SHORT).show();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.commit();
                                    Intent intent=new Intent(Authentication_Activity.this, ProfileActivity.class);
                                    intent.putExtra("user",task.getResult().getUser().getEmail());
                                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                } else {
                                    // When task is unsuccessful display Toast
                                    Log.d("GGG", "Failed: ");
                                    Toast.makeText(Authentication_Activity.this, "Authentication Failed :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}