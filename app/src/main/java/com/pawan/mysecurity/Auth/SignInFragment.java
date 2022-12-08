package com.atish.mysecurity.Auth;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static com.atish.mysecurity.Auth.RegisterActivity.onResetPasswordFragment;
import static com.atish.mysecurity.Auth.RegisterActivity.onSignUPFragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atish.mysecurity.MainActivity;
import com.atish.mysecurity.Others.AboutUsActivity;
import com.atish.mysecurity.R;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class SignInFragment extends Fragment {

    private static final int REQUEST_CODE = 101010;
    private TextView needAnAccount, forgotPassword;
    private FrameLayout parentFrameLayout;
    private EditText email, password;
    private CheckBox rememberme;
    private Button signInBtn;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUSer;

    private LinearLayout fingerprint;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    int attempt = 1;
    RequestQueue queue;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        parentFrameLayout = requireActivity().findViewById(R.id.register_fragment);

        needAnAccount = view.findViewById(R.id.orsign_up);
        forgotPassword = view.findViewById(R.id.forgotpass);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        rememberme = view.findViewById(R.id.rememberme);
        signInBtn = view.findViewById(R.id.signinbtn);
        progressBar = view.findViewById(R.id.signin_progess);
        fingerprint =view.findViewById(R.id.fingerprint);

        firebaseAuth = FirebaseAuth.getInstance();
        mUSer = firebaseAuth.getCurrentUser();

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        BiometricManager biometricManager = BiometricManager.from(getActivity());
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getActivity(), "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                signInBtn.setEnabled(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getActivity(), "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                signInBtn.setEnabled(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(getActivity());
        biometricPrompt = new BiometricPrompt(getActivity(),
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity().getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                checkInputs();
                signInBtn.setEnabled(true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity().getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
                signInBtn.setEnabled(false);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my security")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();
        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        needAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUPFragment = true;
                onResetPasswordFragment = false;
                setFragment(new SignUpFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUPFragment = false;
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });

//        to check email edit text
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//              no use for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {
//              no use for now
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                no use for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {
//              no use for now
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attempt < 3){
                    checkEmailAndPassword();
                }else if(attempt == 3){
                    checkEmailAndPassword();
                    Toast.makeText(getActivity(), "Login Limit exceed", Toast.LENGTH_SHORT).show();
                }else if (attempt > 3){
                    verifyGoogleReCAPTCHA();
                    Toast.makeText(getActivity(), "Verify You are not robot.", Toast.LENGTH_SHORT).show();
                }
                attempt++;

            }
        });

        if(sharedPreferences.getBoolean("rememberme", false)){

//            To make email and password pre saved
            String registeredEmail = sharedPreferences.getString("email", "");
//            String registeredPassword = sharedPreferences.getString("password", "");
            email.setText(registeredEmail);
//            password.setText(registeredPassword);
//            fingerprint.setVisibility(View.VISIBLE);
        }

    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_form_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceAsColor")
    private void checkInputs() {
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(password.getText())){
//               change signin button
                signInBtn.setEnabled(false);
            }else {
                signInBtn.setEnabled(false);
            }
        }else {
            signInBtn.setEnabled(false);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void checkEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){

//          if every thing goes well than change these
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setEnabled(false);
            email.setEnabled(false);
            password.setEnabled(false);
            rememberme.setEnabled(false);
            forgotPassword.setEnabled(false);
            needAnAccount.setEnabled(false);


            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if(user.isEmailVerified()){
                                    if(rememberme.isChecked()){
                                        sharedPreferences.edit().putBoolean("rememberme", true).commit();
                                    }

                                    String emailValue = email.getText().toString();
                                    String passwordValue = password.getText().toString();

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", emailValue);
                                    editor.putString("password", passwordValue);
                                    editor.putBoolean("isLogin", true);
                                    editor.commit();


                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("email", emailValue);
                                    contentValues.put("password", passwordValue);

                                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                    Toast.makeText(getActivity(), "User Logged In", Toast.LENGTH_SHORT).show();
                                    startActivity(mainIntent);
                                    getActivity().finish();

                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInBtn.setEnabled(true);
                                    email.setEnabled(true);
                                    password.setEnabled(true);
                                    rememberme.setEnabled(true);
                                    forgotPassword.setEnabled(true);
                                    needAnAccount.setEnabled(true);
                                    user.sendEmailVerification();
                                    Toast.makeText(getActivity(), "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                                }


                            }else{
//                                    if every thing doesn't go well than change these
                                progressBar.setVisibility(View.INVISIBLE);
                                signInBtn.setEnabled(true);
                                email.setEnabled(true);
                                password.setEnabled(true);
                                rememberme.setEnabled(true);
                                forgotPassword.setEnabled(true);
                                needAnAccount.setEnabled(true);
                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(getActivity(), "Incorrect email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyGoogleReCAPTCHA() {
        SafetyNet.getClient(getActivity()).verifyWithRecaptcha(" ")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {

                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        String captchaToken=recaptchaTokenResponse.getTokenResult();
                        if(captchaToken!=null){
                            if(!captchaToken.isEmpty()){
                                handleVerification(captchaToken);
                            }
                            else {
                                Toast.makeText(getActivity(), "Invalid Captcha Response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Load Captcha", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void handleVerification(final String responseToken) {
        // inside handle verification method we are
        // verifying our user with response token.
        // url to sen our site key and secret key
        // to below url using POST method.
        String url = "https://www.google.com/recaptcha/api/siteverify";

        // in this we are making a string request and
        // using a post method to pass the data.
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // inside on response method we are checking if the
                        // response is successful or not.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                // if the response is successful then we are
                                // showing below toast message.
                                checkEmailAndPassword();
                                Toast.makeText(getActivity(), "User verified with reCAPTCHA", Toast.LENGTH_SHORT).show();
                            } else {
                                // if the response if failure we are displaying
                                // a below toast message.
                                Toast.makeText(getActivity(), String.valueOf(jsonObject.getString("error-codes")), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            // if we get any exception then we are
                            // displaying an error message in logcat.
                            Log.d("TAG", "JSON exception: " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // inside error response we are displaying
                        // a log message in our logcat.
                        Log.d("TAG", "Error message: " + error.getMessage());
                    }
                }) {
            // below is the getParamns method in which we will
            // be passing our response token and secret key to the above url.
            @Override
            protected Map<String, String> getParams() {
                // we are passing data using hashmap
                // key and value pair.
                Map<String, String> params = new HashMap<>();
                params.put("secret", " ");
                params.put("response", responseToken);
                return params;
            }
        };
        // below line of code is use to set retry
        // policy if the api fails in one try.
        request.setRetryPolicy(new DefaultRetryPolicy(
                // we are setting time for retry is 5 seconds.
                50000,

                // below line is to perform maximum retries.
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // at last we are adding our request to queue.
        queue.add(request);
    }


}