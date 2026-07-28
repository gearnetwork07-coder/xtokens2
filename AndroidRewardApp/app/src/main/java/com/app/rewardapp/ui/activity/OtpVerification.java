package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityOtpVerificationBinding;
import com.app.rewardapp.databinding.PassresetDialogBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Fun;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerification extends AppCompatActivity {
    ActivityOtpVerificationBinding binding;
    Activity activity;
    private AlertDialog newpass,dialog,alertDialog;
    PinEntryEditText pinEntry;
    PassresetDialogBinding pass_Reset;
    String email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity=OtpVerification.this;
        dialog= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        pinEntry = findViewById(R.id.txt_pin_entry);
        email=getIntent().getStringExtra("email");

        pass_Reset= PassresetDialogBinding.inflate(getLayoutInflater());

        newpass=new AlertDialog.Builder(activity).setView(pass_Reset.getRoot()).create();
        newpass.getWindow().setBackgroundDrawableResource(R.color.transparent);
        newpass.getWindow().setWindowAnimations(R.style.Dialoganimation);
        newpass.setCancelable(false);

        binding.verify.setOnClickListener(view -> {

            if (Fun.isConnected(this)){
                if (binding.txtPinEntry.getText().toString().trim().length()==6) {
                            verifyotp(binding.txtPinEntry.getText().toString().trim());
                } else {
                    showAlert(getString(R.string.invalid_otp));
                }
            }else {
                showAlert(getString(R.string.no_internet_connection));
            }

        });
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
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

    private void verifyotp(String otp) {
        showDialog();
        Call<CallbackResp> call=ApiClient.getClient(this).create(ApiInterface.class).Verify_OTP(otp,email);
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful()){
                    if(response.body().getCode()==201){
                        newpass.show();

                        pass_Reset.close.setOnClickListener(view -> {newpass.dismiss();});
                        pass_Reset.submit.setOnClickListener(view -> {
                            if(TextUtils.isEmpty(pass_Reset.newpass.getText().toString().trim()) || TextUtils.isEmpty(pass_Reset.confpass.getText().toString().trim())){
                                showAlert(getString(R.string.fill_required_detail));
                            }else if(pass_Reset.newpass.getText().toString().trim().equals(pass_Reset.confpass.getText().toString().trim())){
                                update(pass_Reset.newpass.getText().toString().trim());
                            }else {
                                showAlert(getString(R.string.password_not_match));
                            }
                        });
                    }else {
                        pinEntry.setText(null);
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

    private void update(String pass) {
        showDialog();
        Call<CallbackResp> call = ApiClient.getClient(this).create(ApiInterface.class).UpdatePass(email,pass,binding.txtPinEntry.getText().toString().trim());
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful()){
                    if(response.body().getCode()==201){
                        newpass.dismiss();
                        Toast.makeText(OtpVerification.this,getString(R.string.password_update_message), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity,FrontLogin.class));
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
