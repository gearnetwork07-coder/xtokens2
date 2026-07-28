package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.util.Const.cpxAppID;
import static com.app.rewardapp.util.Const.cpxHash;
import static com.app.rewardapp.util.Const.offerWallCallback;
import static com.app.rewardapp.util.Const.surveyResp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackOfferwall;
import com.app.rewardapp.adapters.OfferwallAdapter;
import com.app.rewardapp.databinding.FragmentOffersBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXCardConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXCardStyle;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;
import org.json.JSONArray;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersFragment extends Fragment {
    public OffersFragment() {}
    FragmentOffersBinding bind;
    Activity activity;
    OfferwallAdapter offerwallAdapter;
    OfferwallAdapter surveyAdapter;
    private CPXResearch cpxResearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind=FragmentOffersBinding.inflate(getLayoutInflater());
        activity=requireActivity();

        if(App.getPref().getIntData("uiStyle")==1){
            bind.layoutToolbar.setVisibility(View.VISIBLE);
        }

        BottomNavigationView bottomNavigationView=requireActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        bind.getRoot().setFocusableInTouchMode(true);
        bind.getRoot().requestFocus();
        bind.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                return true;
            }
            return false;
        });

        initOffer();
        getOfferwall();

        return bind.getRoot();
    }

    private void initOffer() {
        bind.recyclerViewOfferwall.setLayoutManager(new GridLayoutManager(activity, 3));
        offerwallAdapter = new OfferwallAdapter(offerWallCallback, getActivity());
        bind.recyclerViewOfferwall.setAdapter(offerwallAdapter);

        bind.recyclerViewSurvey.setLayoutManager(new GridLayoutManager(activity, 3));
        surveyAdapter = new OfferwallAdapter(surveyResp, getActivity());
        bind.recyclerViewSurvey.setAdapter(surveyAdapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getOfferwall() {
        if (!offerWallCallback.isEmpty() || !surveyResp.isEmpty()) {
            offerwallAdapter.notifyDataSetChanged();
            surveyAdapter.notifyDataSetChanged();

            if (surveyResp.isEmpty()) {
                bind.tvTopSurvey.setVisibility(View.GONE);
                bind.recyclerViewSurvey.setVisibility(View.GONE);
            } else if (offerWallCallback.isEmpty()) {
                bind.tvTopOffer.setVisibility(View.GONE);
                bind.recyclerViewOfferwall.setVisibility(View.GONE);
            }

            if (cpxAppID != null && cpxHash != null) {
                initCpx();
            }
        } else {
            Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getOfferwall().enqueue(new Callback<List<CallbackOfferwall>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<CallbackOfferwall>> call, Response<List<CallbackOfferwall>> response) {
                    if (response.isSuccessful() && !response.body().isEmpty()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getOfferwall_slug().equals("cpx_research")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.body().get(i).getOfferwall_url());
                                    cpxAppID = jsonArray.getJSONObject(0).getString("value");
                                    cpxHash = jsonArray.getJSONObject(1).getString("value");
                                    initCpx();
                                } catch (Exception ignored) {
                                }
                            } else {
                                if (response.body().get(i).getOffer_type().equals("offers")) {
                                    offerWallCallback.add(response.body().get(i));
                                } else {
                                    surveyResp.add(response.body().get(i));
                                }
                            }
                        }
                        if (surveyResp.isEmpty()) {
                            bind.tvTopSurvey.setVisibility(View.GONE);
                            bind.recyclerViewSurvey.setVisibility(View.GONE);
                        } else if (offerWallCallback.isEmpty()) {
                            bind.tvTopOffer.setVisibility(View.GONE);
                            bind.recyclerViewOfferwall.setVisibility(View.GONE);
                        }
                        surveyAdapter.notifyDataSetChanged();
                        offerwallAdapter.notifyDataSetChanged();

                    } else {
                        bind.tvTopSurvey.setVisibility(View.GONE);
                        bind.tvTopOffer.setVisibility(View.GONE);
                        bind.recyclerViewSurvey.setVisibility(View.GONE);
                        bind.recyclerViewOfferwall.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<CallbackOfferwall>> call, Throwable t) {

                }
            });
        }
    }

    private void initCpx() {
        try {
            if (!cpxAppID.isEmpty() && !cpxHash.isEmpty()) {
                CPXStyleConfiguration style = new CPXStyleConfiguration(SurveyPosition.SideRightNormal,
                        "Earn up to 3 Coins in<br> 4 minutes with surveys",
                        20,
                        "#ffffff",
                        String.valueOf(String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.colorPrimaryDark)))),

                        true);

                CPXConfiguration config = new CPXConfigurationBuilder(cpxAppID,
                        Const.auth,
                        cpxHash,
                        style)
                        .build();

                CPXCardConfiguration cardConfig = new CPXCardConfiguration.Builder()
                        .accentColor(getResources().getColor(R.color.blue))
                        .backgroundColor(getResources().getColor(R.color.toolbar))
                        .starColor(Color.parseColor("#FFAA00"))
                        .inactiveStarColor(Color.parseColor("#838393"))
                        .textColor(Color.parseColor("#8E8E93"))
                        .dividerColor(Color.parseColor("#5A7DFE"))
                        .cornerRadius(4f)
                        .cpxCardStyle(CPXCardStyle.DEFAULT)
                        .fixedCPXCardWidth(146)
                        .hideCurrencyName(true)
                        .build();

                cpxResearch = CPXResearch.Companion.init(config);
                cpxResearch.insertCPXResearchCardsIntoContainer(activity, bind.lytCpx, cardConfig);
                bind.lytCpx.setVisibility(View.VISIBLE);
                bind.tvAvailSurvey.setVisibility(View.VISIBLE);

            }
        } catch (Exception ignored) {
        }
    }

}