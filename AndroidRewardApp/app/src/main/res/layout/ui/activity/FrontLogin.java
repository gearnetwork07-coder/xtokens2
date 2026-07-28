package com.app.rewardapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.callback.CallbackLogin;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.ActivityFrontLoginBinding;
import com.app.rewardapp.databinding.DialogForgotPasswordBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
    DialogForgotPasswordBinding passwordBinding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFrontLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        activity = FrontLogin.this;
        session= new Session(activity);
        loading= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        if(!TextUtils.isEmpty(session.getData(session.EMAIL)) && !TextUtils.isEmpty(session.getData(session.PASSWORD))){
            binding.email.setText(session.getData(session.EMAIL));
            binding.password.setText(session.getData(session.PASSWORD));
            reqLogin(session.getData(session.EMAIL),session.getData(session.PASSWORD));
        }

        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);

        passwordBinding=DialogForgotPasswordBinding.inflate(getLayoutInflater());
        pass_Reset=new AlertDialog.Builder(activity).setView(passwordBinding.getRoot()).create();
        pass_Reset.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pass_Reset.getWindow().setWindowAnimations(R.style.Dialoganimation);
        pass_Reset.setCancelable(false);

    }

    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(etemail.getText().toString().trim())){
            binding.email.setError(getResources().getString(R.string.error_invalid_email));
            valid=false;
        }
        else if(!etemail.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(getResources().getString(R.string.email_error));
            valid=false;
        }

        if(TextUtils.isEmpty(etpassword.getText().toString().trim())){
            binding.password.setError(getResources().getString(R.string.error_invalid_password));
            valid=false;
        }
            return valid;
    }

    public void Forgot(View view) {
        pass_Reset.show();
        passwordBinding.close.setOnClickListener(view1 -> {pass_Reset.dismiss();});
        passwordBinding.submit.setOnClickListener(view1 -> {
            Fun.hideKeyboard(view,activity);
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
        alertDialog.findViewById(R.id.close).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }


    private void generateOTP(String emails) {
        showDialog();
        Call<BonusResponse> call=ApiClient.getClient(this).create(ApiInterface.class).ResetPass(emails);
        call.enqueue(new Callback<BonusResponse>() {
            @Override
            public void onResponse(Call<BonusResponse> call, Response<BonusResponse> response) {
                dismissDialog();
                if(response.isSuccessful()){
                  if(response.body().getStatus()==201){
                      Toast.makeText(activity,"Otp Sended to Email Please Check",Toast.LENGTH_LONG).show();
                      Intent intent = new Intent(activity,OtpVerification.class);
                      intent.putExtra("email",emails);
                      startActivity(intent);
                  }else{
                      showAlert(response.body().getMessage());
                  }
                }
            }

            @Override
            public void onFailure(Call<BonusResponse> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    public void Create(View view) {
        Intent intent = new Intent(activity, FrontSignup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void Login(View view) {
        Fun.hideKeyboard(view,activity);
        if(!Fun.isConnected(this)){
            Fun.Error(activity,getString(R.string.no_internet));
        }

        if(!validateData()){
            return;
        }else {
            reqLogin(etemail.getText().toString().trim(),etpassword.getText().toString().trim());
        }
    }

    private void reqLogin(String email,String password) {
        showDialog();
        Constant_Api.API_TYPE="login";
        Constant_Api.P1=email;
        Constant_Api.P2=password;
        Call<CallbackLogin> login= ApiClient.getClient(this).create(ApiInterface.class).Login();
        login.enqueue(new Callback<CallbackLogin>() {
            @Override
            public void onResponse(Call<CallbackLogin> call, Response<CallbackLogin> response) {
                dismissDialog();
                if(response.isSuccessful()){
                    if(response.body().getResponse()==201){
                        session.saveUserData(response.body().getUser().getCustId(),
                                response.body().getUser().getEmail(),
                                response.body().getUser().getPhone(),
                                response.body().getUser().getName(),
                                etpassword.getText().toString().trim(),
                                response.body().getUser().getRefferalId(),
                                Constant_Api.AUTH+ Fun.encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),""));
                        session.setData(session.WALLET,response.body().getUser().getBalance());
                        session.setBoolean(session.LOGIN,true);
                        Toast.makeText(getApplicationContext(), "Welcome "+ response.body().getUser().getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                        FrontLogin.this.startActivity(intent);
                    }else{
                        showAlert(response.body().getMessage());
                    }
                }else {
                    showAlert(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<CallbackLogin> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.READ_PHONE_STATE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            finish();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }

                    }).check();
        }
    }

}
