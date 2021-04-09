package com.example.recyclerlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

//import android.hardware.biometrics.BiometricPrompt;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 10001;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }



        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //textView.setText("Error");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                onActivityResult(AUTHUI_REQUEST_CODE, RESULT_OK, intent);

                //textView.setText("Success");
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                //textView.setText("Failure");
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Programmer World Authentication")
                .setNegativeButtonText("Cancel/ Use Password")
                .setConfirmationRequired(false)
                .build();

    }

    public void buttonAuthenticate(View view){
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){

            //textView.setText("Biometric Not Supported");
            Toast.makeText(getApplicationContext(), "Biometric Not Supported", Toast.LENGTH_LONG).show();

            return;
        }
        biometricPrompt.authenticate(promptInfo);
    }

    public void handleLoginRegister(View view) {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()


        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                .setLogo(R.drawable.notes)
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHUI_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // We have signed in the user or we have a new userl
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.toString());
                //Checking for User (New/Old)
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    //This is a New User
                } else {
                    //This is a returning user
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();

            } else {
                // Signing in failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Log.d(TAG, "onActivityResult: the user has cancelled the sign in request");
                } else {
                    Log.e(TAG, "onActivityResult: ", response.getError());
                }
            }
        }
    }
}
