package com.atish.mysecurity.Auth;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atish.mysecurity.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }

//    //    define variables
    private TextView alreadyHaveAnAccount, ok1, ok2, ok3, ok4;

    private LinearLayout strongpass;

    private ImageView check1, check2, check3, check4;

    private boolean isAtLeast8 = false, hasUpperCase = false, hasNumber = false, hasSymbol = false;

    private FrameLayout parentFrameLayout;

    private EditText email, fullName, password, confirmPassword;
    private Button signUp;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


//    SITE_KEY = " ";
//    SECRET_KEY = " ";
    RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        parentFrameLayout = getActivity().findViewById(R.id.register_fragment);

        alreadyHaveAnAccount = view.findViewById(R.id.orsign_in);

        ok1 = view.findViewById(R.id.ok1);
        ok2 = view.findViewById(R.id.ok2);
        ok3 = view.findViewById(R.id.ok3);
        ok4 = view.findViewById(R.id.ok4);

        strongpass = view.findViewById(R.id.strongpass);

        check1 = view.findViewById(R.id.check1);
        check2 = view.findViewById(R.id.check2);
        check3 = view.findViewById(R.id.check3);
        check4 = view.findViewById(R.id.check4);

        email = view.findViewById(R.id.email);
        fullName = view.findViewById(R.id.full_name);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        signUp = view.findViewById(R.id.signup);
        progressBar = view.findViewById(R.id.signup_progess);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        what to do if user clicked on already have an account
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new com.atish.mysecurity.Auth.SignInFragment());
            }
        });

//        to check email edit text
        email.addTextChangedListener(new TextWatcher() {
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
//          no use for now
            }
        });

//        to check fullname edit text
        fullName.addTextChangedListener(new TextWatcher() {
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
//                no use for now
            }
        });

//        to check password edit text
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
                checkpasswordStrong();
            }
        });

//        to check confirm password edit text
        confirmPassword.addTextChangedListener(new TextWatcher() {
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
//                no use for now
            }
        });



////        what to o if user clicked in signup button

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                send data to firebase
                verifyGoogleReCAPTCHA();
            }
        });



    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceAsColor")
    private void checkInputs() {
//        check if email is empty or not
        if (!TextUtils.isEmpty(email.getText())) {
//            check if full name is empty or not
            if (!TextUtils.isEmpty(fullName.getText())) {
//                check if password have length 6 or not
                if (isAtLeast8 && hasUpperCase && hasNumber && hasSymbol) {
//                    check confirm password is empty or not
                    strongpass.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(confirmPassword.getText())) {
//                        change signup button
                        signUp.setEnabled(true);
                    } else {
                        signUp.setEnabled(false);
                    }

                } else {
                    strongpass.setVisibility(View.GONE);
                    signUp.setEnabled(false);
                }

            } else {
                signUp.setEnabled(false);
            }
        } else {
            signUp.setEnabled(false);
        }
    }

    private void checkpasswordStrong(){

        String gpassword = password.getText().toString();

//        to check characyers
        if (gpassword.length() >= 8){
            isAtLeast8 = true;
            check1.setImageResource(R.drawable.ic_baseline_check_circle_24_blue);
            ok1.setTextColor(getResources().getColor(R.color.blue));
        }
        else {
            isAtLeast8 = false;
            check1.setImageResource(R.drawable.ic_baseline_check_circle_24);
            ok1.setTextColor(getResources().getColor(R.color.ash));
        }

//        for uppercase
        if (gpassword.matches("(.*[A-Z].*)")){
            hasUpperCase = true;
            check2.setImageResource(R.drawable.ic_baseline_check_circle_24_blue);
            ok2.setTextColor(getResources().getColor(R.color.blue));
        }
        else {
            hasUpperCase = false;
            check2.setImageResource(R.drawable.ic_baseline_check_circle_24);
            ok2.setTextColor(getResources().getColor(R.color.ash));
        }

        //        for number
        if (gpassword.matches("(.*[0-9].*)")){
            hasNumber = true;
            check3.setImageResource(R.drawable.ic_baseline_check_circle_24_blue);
            ok3.setTextColor(getResources().getColor(R.color.blue));
        }
        else {
            hasNumber = false;
            check3.setImageResource(R.drawable.ic_baseline_check_circle_24);
            ok3.setTextColor(getResources().getColor(R.color.ash));
        }

//        for symbol
        if (gpassword.matches("^(?=.*[,.@#$%&+()*':;!?_/~`|•√π÷×¶∆£¢€¥^°={}©®™℅]).*$")){
            hasSymbol = true;
            check4.setImageResource(R.drawable.ic_baseline_check_circle_24_blue);
            ok4.setTextColor(getResources().getColor(R.color.blue));
        }
        else {
            hasSymbol = false;
            check4.setImageResource(R.drawable.ic_baseline_check_circle_24);
            ok4.setTextColor(getResources().getColor(R.color.ash));
        }
    }



    @SuppressLint("ResourceAsColor")
    private void checkEmailAndPassword() {
//        check if given email is valid or not
        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
//            check if given password is equals to confirm password
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {

//                change progress bar  and signup button
                progressBar.setVisibility(View.VISIBLE);
                signUp.setEnabled(false);
                fullName.setEnabled(false);
                email.setEnabled(false);
                password.setEnabled(false);
                confirmPassword.setEnabled(false);
                alreadyHaveAnAccount.setEnabled(false);

//                connect with firebase
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Map<Object, String> userdata = new HashMap<>();
                                    userdata.put("fullname", fullName.getText().toString());
                                    userdata.put("password", password.getText().toString());
                                    userdata.put("email", email.getText().toString());
                                    firebaseFirestore.collection("USERS")
                                            .add(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {

//                                    if every thing is good then return to login page
                                                        Intent mainIntent = new Intent(getActivity(), RegisterActivity.class);
                                                        Toast.makeText(getActivity(), "User successfully registered", Toast.LENGTH_SHORT).show();
                                                        startActivity(mainIntent);
                                                        getActivity().finish();

                                                        FirebaseUser usr = firebaseAuth.getCurrentUser();
                                                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(fullName.getText().toString())
                                                                .build();
                                                        usr.updateProfile(request);

                                                    } else {
//                                    if every thing doesn't go well than change these
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        signUp.setEnabled(true);
                                                        fullName.setEnabled(true);
                                                        email.setEnabled(true);
                                                        password.setEnabled(true);
                                                        confirmPassword.setEnabled(true);
                                                        alreadyHaveAnAccount.setEnabled(true);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
//                                    if every thing doesn't go well than change these
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUp.setEnabled(true);
                                    fullName.setEnabled(true);
                                    email.setEnabled(true);
                                    password.setEnabled(true);
                                    confirmPassword.setEnabled(true);
                                    alreadyHaveAnAccount.setEnabled(true);
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
//                if password not matched
                confirmPassword.setError("Password doesn't matched!");
            }
        } else {
//            if email address is not valid
            email.setError("Invalid Email!");
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