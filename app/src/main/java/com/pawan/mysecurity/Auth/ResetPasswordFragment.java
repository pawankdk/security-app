package com.atish.mysecurity.Auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.atish.mysecurity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment {

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registeredEmail;
    private Button resetPasswordBtn;
    private TextView goBack;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        registeredEmail = view.findViewById(R.id.email);
        resetPasswordBtn = view.findViewById(R.id.resetpassbtn);
        goBack = view.findViewById(R.id.goback);

        emailIconContainer = view.findViewById(R.id.forgotpass_icon_container);
        emailIcon = view.findViewById(R.id.forgotpass_email_icon);
        emailIconText = view.findViewById(R.id.forgotpass_email_icon_text);
        progressBar = view.findViewById(R.id.forgotpass_progressbar);

        parentFrameLayout = getActivity().findViewById(R.id.register_fragment);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {
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

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetPasswordBtn.setEnabled(false);
                registeredEmail.setEnabled(false);
                goBack.setEnabled(false);

                firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    emailIconText.setTextColor(getResources().getColor(R.color.green));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                    emailIcon.setColorFilter(getResources().getColor(R.color.green));

                                }else{
                                    String error = task.getException().getMessage();

                                    resetPasswordBtn.setEnabled(true);

                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.alert));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                }
                                progressBar.setVisibility(View.GONE);
                                registeredEmail.setEnabled(true);
                                goBack.setEnabled(true);

                            }
                        });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new com.atish.mysecurity.Auth.SignInFragment());
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void checkInputs() {
        if(TextUtils.isEmpty(registeredEmail.getText())){
            resetPasswordBtn.setEnabled(false);
        }else{
            resetPasswordBtn.setEnabled(true);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


}