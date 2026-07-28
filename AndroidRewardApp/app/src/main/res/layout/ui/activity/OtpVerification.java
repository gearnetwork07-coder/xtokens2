package com.app.rewardapp.ui.activity;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.ActivityOtpVerificationBinding;
import com.app.rewardapp.databinding.PassresetDialogBinding;

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

        binding.layoutToolbar.toolbar.setTitle(Constant_Api.TITLE);
        setSupportActionBar(binding.layoutToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity=OtpVerification.this;
        dialog= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
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
                    showAlert("Invalid Otp");
                }
            }else {
                showAlert(getString(R.string.no_internet));
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
        alertDialog.findViewById(R.id.close).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void verifyotp(String otp) {
        showDialog();
        Call<BonusResponse> call=ApiClient.getClient(this).create(ApiInterface.class).Verify_OTP(otp,email);
        call.enqueue(new Callback<BonusResponse>() {
            @Override
            public void onResponse(Call<BonusResponse> call, Response<BonusResponse> response) {
                dismissDialog();
                if(response.isSuccessful()){
                    if(response.body().getStatus()==201){
                        newpass.show();
                        pass_Reset.close.setOnClickListener(view -> {newpass.dismiss();});
                        pass_Reset.submit.setOnClickListener(view -> {
                            if(TextUtils.isEmpty(pass_Reset.newpass.getText().toString().trim()) || TextUtils.isEmpty(pass_Reset.confpass.getText().toString().trim())){
                                showAlert(getString(R.string.fil_detail));
                            }else if(pass_Reset.newpass.getText().toString().trim().equals(pass_Reset.confpass.getText().toString().trim())){
                                update(pass_Reset.newpass.getText().toString().trim());
                            }else {
                                showAlert("New Password and Confirm Password Not Matched!!");
                            }
                        });
                    }else {
                        pinEntry.setText(null);
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

    private void update(String pass) {
        showDialog();
        Call<BonusResponse> call = ApiClient.getClient(this).create(ApiInterface.class).UpdatePass(email,pass);
        call.enqueue(new Callback<BonusResponse>() {
            @Override
            public void onResponse(Call<BonusResponse> call, Response<BonusResponse> response) {
                dismissDialog();
                if(response.isSuccessful()){
                    if(response.body().getStatus()==201){
                        newpass.dismiss();
                        showAlert("Password Updated Successfully Go back and Login Now!!");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
