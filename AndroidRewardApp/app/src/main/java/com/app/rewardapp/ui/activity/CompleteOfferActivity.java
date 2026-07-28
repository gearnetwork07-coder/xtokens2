package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.util.Fun.showToast;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityCompleteOfferBinding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteOfferActivity extends AppCompatActivity implements OnResponseListener {
    private ActivityCompleteOfferBinding bind;
    private Activity activity;
    private BottomSheetDialog uploadSheet;
    private AlertDialog  bonusDialog;
    androidx.appcompat.app.AlertDialog loading;
    private LayoutCollectBonusBinding layoutCollectBonusBinding;
    private View bottomView;

    private static final String PARAM_ID  = "id";
    private static final String PARAM_URL = "url";

    private String offerId, offerUrl;
    private Uri    selectedImageUri;
    private String cacheFilePath;

    private AdManager adManager;
    private ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> pickImageLauncher;
    private ActivityResultLauncher<Intent>    openDocLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind     = ActivityCompleteOfferBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity = this;
        adManager = new AdManager(activity);

        loading = Fun.loading(activity);
        layoutCollectBonusBinding = LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonusDialog = new AlertDialog.Builder(activity)
                .setView(layoutCollectBonusBinding.getRoot())
                .create();
        Objects.requireNonNull(bonusDialog.getWindow())
                .setBackgroundDrawableResource(R.color.transparent);
        bonusDialog.getWindow()
                .setWindowAnimations(R.style.Dialoganimation);
        bonusDialog.setCanceledOnTouchOutside(false);

        // read intent params
        Bundle b = getIntent().getExtras();
        offerId  = b.getString(PARAM_ID);
        offerUrl = b.getString(PARAM_URL);

        bind.tvTitle.setText(b.getString("title"));
        bind.coins.setText(b.getString("coin"));
        bind.desc.setText(Html.fromHtml(b.getString("description")));
        Glide.with(activity).load(WebApi.Api.IMAGES+b.getString("image")).into(bind.images);

        setupLaunchers();

        // start offer: open browser
        bind.startoffer.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(offerUrl)));
            } catch (Exception e) {
                Toast.makeText(activity, "Url Broken", Toast.LENGTH_SHORT).show();
            }
        });

        bind.filloffer.setOnClickListener(v -> showUploadDialog());

        bind.back.setOnClickListener(v -> onBackPressed());
    }

    private void setupLaunchers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        handleImageUri(uri);
                    }
                }
        );

        openDocLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            handleImageUri(uri);
                        }
                    }
                }
        );
    }

    private void showUploadDialog() {
        uploadSheet = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        bottomView = LayoutInflater.from(activity)
                .inflate(R.layout.layout_upload_dailyoffer_ss,
                        findViewById(R.id.uploadLayouts), false);
        uploadSheet.setContentView(bottomView);
        uploadSheet.setCancelable(true);

        EditText link = bottomView.findViewById(R.id.link);
        TextView tv  = bottomView.findViewById(R.id.tv_attach_proof);
        Button submit = bottomView.findViewById(R.id.submit);

        View.OnClickListener attachClick = v -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Dexter.withContext(this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).
                        withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                launchImagePicker();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                showToast(activity, Const.TOAST_WARNING,"Permission not Granted");
                            }
                        }).check();
            } else {
                launchImagePicker();
            }
        };
        bottomView.findViewById(R.id.uploadImage).setOnClickListener(attachClick);
        bottomView.findViewById(R.id.uploadImage1).setOnClickListener(attachClick);

        submit.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                showBonus("Please attach a screenshot to continue", true);
            } else {
                submitDetail(link.getText().toString().trim());
            }
        });

        if (!activity.isFinishing()) {
            uploadSheet.show();
        }
    }

    private void launchImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickImageLauncher.launch(
                    new androidx.activity.result.PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            openDocLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        }
    }

    private void handleImageUri(@NonNull Uri uri) {
        selectedImageUri = uri;
        try {
            // 1) compute a temp file in cache
            String fileName = queryName(getContentResolver(), uri);
            File tmp = new File(getCacheDir(), fileName);
            try ( InputStream in = getContentResolver().openInputStream(uri);
                  FileOutputStream out = new FileOutputStream(tmp) ) {
                byte[] buf;
                int bytesRead;
                while ((bytesRead = in.read(buf = new byte[4096])) > 0) {
                    out.write(buf, 0, bytesRead);
                }
            }
            cacheFilePath = tmp.getAbsolutePath();

            // 2) update the bottom-sheet UI
            TextView tv = bottomView.findViewById(R.id.tv_attach_proof);
            tv.setText(tmp.getName());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }

    private String queryName(ContentResolver cr, Uri uri) {
        Cursor cursor = cr.query(uri, null, null, null, null);
        int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(idx);
        cursor.close();
        return name;
    }

    private void submitDetail(String link) {
        showProgress();
        Call<CallbackResp> call;
        if (selectedImageUri == null) {
            call = ApiClient.getClient(activity)
                    .create(ApiInterface.class)
                    .submit(link, offerId, App.getPref().Auth());
        } else {
            File file = new File(cacheFilePath);
            RequestBody requestBody = RequestBody.create(
                    file, MediaType.parse("image/*")
            );
            MultipartBody.Part part = MultipartBody.Part.createFormData(
                    "newimage", file.getName(), requestBody
            );
            call = ApiClient.getClient(activity)
                    .create(ApiInterface.class)
                    .submitWithAttach(part, link, offerId, App.getPref().Auth());
        }

        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> resp) {
                dismissProgress();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 201) {
                    uploadSheet.dismiss();
                    HotOfferActivity.removeItem = true;
                    showBonus(resp.body().getMsg(), false);
                } else {
                    showBonus(
                            resp.body() != null ? resp.body().getMsg() : "Server error",
                            true
                    );
                }
            }
            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissProgress();
                Toast.makeText(activity, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress() {
        if (!loading.isShowing()) loading.show();
    }
    private void dismissProgress() {
        if (loading.isShowing()) loading.dismiss();
    }

    private void showBonus(String msg, boolean error) {
        bonusDialog.show();
        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(getString(R.string.close));

        layoutCollectBonusBinding.congrts.setText(
                error ? getString(R.string.oops) : getString(R.string.congratulations)
        );
        layoutCollectBonusBinding.congrts.setTextColor(
                ContextCompat.getColor(activity, error ? R.color.red : R.color.green)
        );

        layoutCollectBonusBinding.closebtn.setOnClickListener(v -> bonusDialog.dismiss());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override public void onRewarded()    { }
    @Override public void onAdNotLoaded() { }
}
