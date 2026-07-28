package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.ActivityFrontSignupBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontSignup extends AppCompatActivity {
    ActivityFrontSignupBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Activity activity;
    Session session;
    private AlertDialog dialog,alertDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        binding=ActivityFrontSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session=new Session(this);
        activity=FrontSignup.this;
        dialog= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(binding.username.getText().toString().trim())) {
            binding.username.setError(getResources().getString(R.string.error_username));
            valid = false;
        }
        if(TextUtils.isEmpty(binding.email.getText().toString().trim())){
            binding.email.setError(getResources().getString(R.string.error_invalid_email));
            valid=false;
        }
        else if(!binding.email.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(getResources().getString(R.string.email_error));
            valid=false;
        }

        if(TextUtils.isEmpty(binding.phone.getText().toString().trim())) {
            binding.phone.setError(getResources().getString(R.string.error_phone));
            valid = false;
        }

        if(TextUtils.isEmpty(binding.passwordText.getText().toString().trim())) {
            binding.passwordText.setError(getResources().getString(R.string.error_password));
            valid = false;
        }
        if(binding.passwordText.getText().toString().trim().length()<5) {
            binding.passwordText.setError("Enter Minimum 5 Digit or word password");
            valid = false;
        }
        return valid;
    }

    public void Signup(View view) {
        if(!Fun.isConnected(activity)){
            showAlert(getString(R.string.no_internet));
        }
        if(!validateData()){
            return;
        }else {
            reqSignup(binding.username.getText().toString().trim(),binding.email.getText().toString().trim(),binding.phone.getText().toString().trim(),binding.passwordText.getText().toString().trim(),binding.refer.getText().toString().trim());
        }
    }

    private void reqSignup(String name, String email, String phone , String passowrd, String refer) {
        showDialog();
        Constant_Api.API_TYPE="signup";
        Constant_Api.P1=name;
        Constant_Api.P2=email;
        Constant_Api.P3=passowrd;
        Constant_Api.P4=phone;
        Constant_Api.P5= Fun.deviceId(this);
        Constant_Api.P6=refer;
        Call<SignupResponse> call = ApiClient.getClient(this).create(ApiInterface.class).Signup();
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getResponse()==201){
                        session.saveUserData("",email,"","",passowrd,"","");
                        session.setBoolean(session.LOGIN,true);
                        Fun.Success(activity,response.body().getMessage());
                    Toast.makeText(FrontSignup.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, FrontLogin.class);
                        startActivity(intent);
                }else{
                    binding.msg.setText(response.body().getMessage());
                    binding.msg.setTextColor(Color.parseColor("#ff2200"));
                    showAlert(response.body().getMessage());
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    public void login(View view) {
        Intent intent = new Intent(activity, FrontLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        alertDialog.findViewById(R.id.close).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

}
