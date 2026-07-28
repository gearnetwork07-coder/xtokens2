package com.app.rewardapp.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.rewardapp.databinding.FragmentRatingDialogBinding;
import com.app.rewardapp.util.Session;

public class RatingDialogFragment extends DialogFragment {

    FragmentRatingDialogBinding binding;
    String appName;
    Session session;
    public RatingDialogFragment() {
    }

    public static RatingDialogFragment newInstance() {
        return new RatingDialogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatingDialogBinding.inflate(getLayoutInflater());
        String appName = getActivity().getPackageName();
        session=new Session(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.btnSubmit.setOnClickListener(v -> {
            session.setBoolean("exit",true);
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));

            }
        });

        binding.btnSkip.setOnClickListener(v -> {
            dismiss();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        binding.unbind();
    }
}