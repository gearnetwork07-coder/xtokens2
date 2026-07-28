package com.app.rewardapp.ui.dialogfrag;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.FragmentBonusAdBinding;
import java.util.Objects;

public class BonusAdFragment extends DialogFragment {
    FragmentBonusAdBinding bind;
    Activity activity;
    BonusDialogListener listener;
    Bundle bundle;
    public static BonusAdFragment newInstance(Bundle bundle,BonusDialogListener bonusDialogListener) {
        return new BonusAdFragment(bundle, bonusDialogListener);
    }

    public BonusAdFragment(Bundle bundle,BonusDialogListener bonusDialogListener) {
        this.bundle = bundle;
        listener=bonusDialogListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        bind=FragmentBonusAdBinding.inflate(getLayoutInflater());
        dialog.setContentView(bind.getRoot());
        activity=requireActivity();

        if(bundle.getString("coin")!=null){
            bind.coinLayout.setVisibility(View.VISIBLE);
            bind.tvRewardAmount.setText("+"+bundle.getString("coin"));
        }

        bind.close.setOnClickListener(v -> {
            listener.onClose();
            dismiss();
        });

        bind.claim.setOnClickListener(v -> {
            listener.onClaim();
            dismiss();
        });

        return dialog;
    }

}