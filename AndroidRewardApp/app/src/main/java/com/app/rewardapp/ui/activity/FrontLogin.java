package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.restApi.WebApi.Api.BASE_URL;
import static com.app.rewardapp.util.Constant_Api.AUTH;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.deviceId;
import static com.app.rewardapp.util.Fun.encrypt;
import static com.app.rewardapp.util.Fun.hideKeyboard;
import static com.app.rewardapp.util.Fun.isConnected;
import static com.app.rewardapp.util.Fun.showToast;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityFrontLoginBinding;
import com.app.rewardapp.databinding.DialogForgotPasswordBinding;
import com.app.rewardapp.databinding.LayoutPolicyBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.onesignal.OneSignal;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontLogin extends AppCompatActivity {
    ActivityFrontLoginBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText etemail,etpassword;
    Activity activity;
    Session session;
    private AlertDialog pass_Reset,loading,alertDialog;
    GoogleSignInClient mGoogleSignInClient;
    DialogForgotPasswordBinding passwordBinding;
    private static final int RC_SIGN_IN = 100;
    BottomSheetDialog bottomSheetDialog;
    LayoutPolicyBinding layoutPolicyBinding;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFrontLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        activity = FrontLogin.this;
        session= new Session(activity);
        loading= Fun.loading(activity);

        alertDialog= Fun.Alert(activity);

        OneSignal.initWithContext(this);
        preparePolicyDialog();

        if(!TextUtils.isEmpty(session.getData(session.EMAIL)) && !TextUtils.isEmpty(session.getData(session.PASSWORD))){
            binding.email.setText(session.getData(session.EMAIL));
            binding.password.setText(session.getData(session.PASSWORD));
            reqLogin(session.getData(session.EMAIL),session.getData(session.PASSWORD));
        }

        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);

        binding.createAccountText.setOnClickListener(v -> {
            Create();
        });

        binding.loginEmail.setOnClickListener(v -> {
            hideKeyboard(v,activity);
            if(!isConnected(this)){
                Fun.showToast(activity,Const.TOAST_SUCCESS,getString(R.string.no_internet_connection));
            }
            if(!validateData()){
                return;
            }else {
                reqLogin(etemail.getText().toString().trim(),etpassword.getText().toString().trim());
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        binding.loginGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            LoginUserGoogle(acct.getDisplayName(), acct.getEmail(), String.valueOf(acct.getPhotoUrl()), acct.getId());

        } catch (ApiException e) {
            Log.w("LOgin Activtiy", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void preparePolicyDialog() {
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        layoutPolicyBinding= LayoutPolicyBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(layoutPolicyBinding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bottomSheetDialog.show();

        layoutPolicyBinding.agree.setOnClickListener(v -> {
            OneSignal.promptForPushNotifications();
            bottomSheetDialog.dismiss();
        });

        layoutPolicyBinding.disagree.setOnClickListener(v -> {
            finishAffinity();
        });

        layoutPolicyBinding.visitPrivacyPolicy.setOnClickListener(v -> {
            Fun.launchCustomTabs(activity,BASE_URL+"page/privacy-policy");
        });
        layoutPolicyBinding.visitTerms.setOnClickListener(v -> {
            Fun.launchCustomTabs(activity,BASE_URL+"page/terms-conditions");
        });

    }

    private void LoginUserGoogle(String username,String email,String profile,String google) {
        showDialog();
        Call<CallbackResp> login= Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).ApiUser(data(username,email,google,profile,deviceId(this),"",3,0,"",1));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.saveUserData(google,
                            response.body().getUser().getEmail(),
                            response.body().getUser().getPhone(),
                            response.body().getUser().getProfile(),
                            response.body().getUser().getName(),
                            etpassword.getText().toString().trim(),
                            response.body().getUser().getRefferalId(),
                            response.body().getUser().getFrom_refer(),
                            AUTH+ encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),"")
                            ,
                           "google");
                    session.setData(session.WALLET,response.body().getUser().getBalance());
                    Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    overridePendingTransition(R.anim.enter,R.anim.exit);

                }else {
                    try {
                        showAlert(response.body().getMsg());
                    }catch (Exception e){ e.getMessage();}
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }


    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(etemail.getText().toString().trim())){
            binding.email.setError(getString(R.string.error_invalid_email));
            valid=false;
        }
        else if(!etemail.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(getString(R.string.error_invalid_email));
            valid=false;
        }

        if(TextUtils.isEmpty(etpassword.getText().toString().trim())){
            binding.password.setError(getString(R.string.enter_pass));
            valid=false;
        }
            return valid;
    }

    public void Forgot(View view) {
        passwordBinding=DialogForgotPasswordBinding.inflate(getLayoutInflater());
        pass_Reset=new AlertDialog.Builder(activity).setView(passwordBinding.getRoot()).create();
        pass_Reset.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pass_Reset.getWindow().setWindowAnimations(R.style.Dialoganimation);
        pass_Reset.setCancelable(false);
        pass_Reset.show();

        passwordBinding.submit.setText(getString(R.string.verify));

        passwordBinding.close.setOnClickListener(view1 -> {pass_Reset.dismiss();});
        passwordBinding.submit.setOnClickListener(view1 -> {
            hideKeyboard(view,activity);
            if(TextUtils.isEmpty(passwordBinding.useremail.getText().toString().trim())){
                showAlert(getString(R.string.error_invalid_email));
            }
            else {
                generateOTP(passwordBinding.useremail.getText().toString().trim());
            }
        });
    }

    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if(loading.isShowing()){
            loading.dismiss();
        }
    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(getString(R.string.okay));
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void generateOTP(String emails) {
        showDialog();
        Call<CallbackResp> call=ApiClient.getClient(this).create(ApiInterface.class).ResetPass(emails);
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful()){
                  if(response.body().getCode()==201){
                      showToast(activity, Const.TOAST_SUCCESS,"Otp Has Sent on Email Please Check");
                      Intent intent = new Intent(activity,OtpVerification.class);
                      intent.putExtra("email",emails);
                      startActivity(intent);
                  }else{
                      showAlert(response.body().getMsg());
                  }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    public void Create() {
        Intent intent = new Intent(activity, FrontSignup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        overridePendingTransition(R.anim.exit,R.anim.enter);
        startActivity(intent);
    }

    private void reqLogin(String email,String password) {
        showDialog();
        Call<CallbackResp> login= ApiClient.getClient(this).create(ApiInterface.class).ApiUser(data("",email,password,"","","",1,0,"",1));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                        session.saveUserData(response.body().getUser().getGoogle(),
                                response.body().getUser().getEmail(),
                                response.body().getUser().getPhone(),
                                response.body().getUser().getProfile(),
                                response.body().getUser().getName(),
                                etpassword.getText().toString().trim(),
                                response.body().getUser().getRefferalId(),
                                response.body().getUser().getFrom_refer(),
                                AUTH+ encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),""),
                                "email");
                        session.setData(session.WALLET,response.body().getUser().getBalance());
                        Toast.makeText(getApplicationContext(), "Welcome "+ response.body().getUser().getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                        FrontLogin.this.startActivity(intent);

                }else {
                   try {
                       showAlert(response.body().getMsg());
                   }catch (Exception e){ e.getMessage();}
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }


}
