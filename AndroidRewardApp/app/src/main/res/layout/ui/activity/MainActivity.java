package com.app.rewardapp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.rewardapp.ui.fragments.FragmentMain;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.app.rewardapp.databinding.ActivityMainBinding;
import com.app.rewardapp.ui.fragments.Coins;
import com.app.rewardapp.ui.fragments.Profile;
import com.app.rewardapp.ui.fragments.Invite;
import com.google.firebase.messaging.FirebaseMessaging;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.UnityAds;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static TextView textpoints;
    private boolean doubleBackToExitPressedOnce =false;
    Session session;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        StartAppSDK.init(this, Constant_Api.STARTAPP_ID, false);
        StartAppAd.disableSplash();

        UnityAds.initialize (this, Constant_Api.UNITY_GAMEID,false);

        AppLovinSdk.getInstance(this).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk(this, configuration -> {
        });

        session =new Session(this);

        session.setBoolean(session.ENABLE_SESSION,true);
        session.save(this);


        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                default:
                    loadFragment(new FragmentMain());
                case R.id.navigation_home:
                    loadFragment(new FragmentMain());
                    return true;
                case R.id.navigation_invite:
                    loadFragment(new Invite());
                    return true;
                case R.id.navigation_coin:
                    loadFragment(new Coins());
                    return true;
                case R.id.navigation_profile:
                    loadFragment(new Profile());
                    return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
//
//        header.findViewById(R.id.history).setOnClickListener(view -> { loadFragment(new Coins());             binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });
//        header.findViewById(R.id.contact).setOnClickListener(view -> { startActivity(new Intent(MainActivity.this,AboutusActivity.class));             binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });
//        header.findViewById(R.id.about).setOnClickListener(view -> { startActivity(new Intent(MainActivity.this,AboutusActivity.class));             binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });
//
//        header.findViewById(R.id.privacy).setOnClickListener(view -> {
//            try {
//                String url = Constant_Api.PRIVACY_POLICY_URL;
//                if (!url.startsWith("http://") && !url.startsWith("https://")) {
//                    url = "http://" + url;
//                }
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);
//                binding.drawerLayout.closeDrawer(GravityCompat.START);
//            } catch (Exception e) {
//                Method.alert(this,getResources().getString(R.string.wrong));}
//        });

//        header.findViewById(R.id.rateus).setOnClickListener(view -> {
//            final String appName = getPackageName();
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
//            } catch (ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
//            }
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });
//        header.findViewById(R.id.logoutbtn).setOnClickListener(view -> {
//            Logout();
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

   @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        RatingDialogFragment dialog = RatingDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "rating_dialog");
    }

//    private void Logout() {
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View promptView = layoutInflater.inflate(R.layout.dialog_logout, null);
//        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setView(promptView);
//        alert.setPositiveButton(getString(R.string.confirm), (dialog, whichButton) -> {
//            session.Logout();
//            session.setBoolean(session.LOGIN,false);
//                startActivity(new Intent(this,FrontLogin.class));
//        });
//        alert.setNegativeButton("CANCEL",
//                (dialog, whichButton) -> {
//                });
//        AlertDialog alert1 = alert.create();
//        alert1.show();
//    }


}
