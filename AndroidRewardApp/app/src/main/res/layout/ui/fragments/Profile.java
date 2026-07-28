package com.app.rewardapp.ui.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.app.rewardapp.ui.activity.AboutusActivity;
import com.app.rewardapp.ui.activity.FrontLogin;
import com.app.rewardapp.ui.activity.WithdrawActivity;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.FragmentProfileBinding;

public class Profile extends Fragment {
    FragmentProfileBinding binding;
    Session session;
    Activity activity;
    private AlertDialog dialog_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(getLayoutInflater());
        session = new Session(getActivity());
        activity=getActivity();

        dialog_logout = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_dialog, null)).create();
        dialog_logout.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog_logout.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog_logout.setCanceledOnTouchOutside(false);

        binding.email.setText("Email : "+session.getData(session.EMAIL));
        binding.username.setText("Name : "+session.getData(session.NAME));
        binding.phone.setText("Phone : "+session.getData(session.PHONE));

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                FragmentMain NameofFragment = new FragmentMain();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.container,NameofFragment);
                transaction.commit();
                return true;
            }
            return false;
        });
        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutusActivity.class));
        });

        binding.cvContact.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutusActivity.class));
        });

        binding.coin.setText(""+session.getIntData(session.WALLET));

        binding.cvFeedback.setOnClickListener(v -> {
            final String appName = getActivity().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        });

        binding.cvHistory.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new Coins());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.cvLogout.setOnClickListener(v -> {
            Logout();
        });


        binding.cvReward.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), WithdrawActivity.class));
        });

        binding.cvPrivacy.setOnClickListener(v -> {
            launchCustomTabs(activity,Constant_Api.PRIVACY_POLICY_URL);
        });

        return binding.getRoot();
    }

    private void Logout() {
        dialog_logout.show();
        dialog_logout.findViewById(R.id.yes).setOnClickListener(v -> {
            session.Logout();
            session.setBoolean(session.LOGIN,false);
            startActivity(new Intent(getActivity(), FrontLogin.class));
            getActivity().finish();
        });

        dialog_logout.findViewById(R.id.no).setOnClickListener(v -> {
            dialog_logout.dismiss();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void launchCustomTabs(Activity activity, String url) {
        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
        customIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        customIntent.setExitAnimations(activity, R.anim.exit, R.anim.enter);
        customIntent.setStartAnimations(activity, R.anim.enter, R.anim.exit);
        customIntent.setUrlBarHidingEnabled(true);
        customIntent.build().launchUrl(activity, Uri.parse(url));

    }


}
