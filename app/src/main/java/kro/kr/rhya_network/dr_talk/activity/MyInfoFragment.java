package kro.kr.rhya_network.dr_talk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;
import kro.kr.rhya_network.dr_talk.util.DialogManager;
import kro.kr.rhya_network.dr_talk.util.RhyaAsyncTask;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;
import kro.kr.rhya_network.dr_talk.util.RhyaRequestHttpURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyInfoFragment extends Fragment {
    // UI Object
    private ProgressWheel progressWheel;
    private ImageView imageView;
    private ScrollView scrollView;
    private ImageView userImageView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView userNameTextView;
    private Button editInfoButton;
    private Button logoutButton;
    private ImageButton appLockButton;
    private CheckBox noticeCbx;
    private CheckBox saveDataCbx;
    // ?????? ?????? ??????
    private boolean isLoadTask = false;
    // RHYA.Network API
    private RhyaNetworkAPI rhyaNetworkAPI;


    public MyInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyInfoFragment newInstance() {
        return new MyInfoFragment();
    }


    /**
     * onCreate - ?????? ??? ??????
     *
     * @param savedInstanceState [Override]
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ?????????
        rhyaNetworkAPI = new RhyaNetworkAPI(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_info, container, false);
    }


    /**
     * onViewCreated - View ?????? ??? ??????
     *
     * @param view               [Override]
     * @param savedInstanceState [Override]
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI ?????????
        imageView = view.findViewById(R.id.imageView);
        progressWheel = view.findViewById(R.id.progress_wheel_for_load);
        scrollView = view.findViewById(R.id.scrollView);
        userImageView = view.findViewById(R.id.userImageView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        editInfoButton = view.findViewById(R.id.editInfoButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        appLockButton = view.findViewById(R.id.setting_lock_app);
        noticeCbx = view.findViewById(R.id.settingButton_notice);
        saveDataCbx = view.findViewById(R.id.settingButton_save_data);

        // ?????? ??????
        showAD(view);

        // ????????? ????????? ?????? ??? ??????
        loadUserData();

        // ????????? ??????
        String noticeResult = rhyaNetworkAPI.rhyaSharedPreferences.getStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_NOTICE_ON_OFF, requireContext());
        String saveDataResult = rhyaNetworkAPI.rhyaSharedPreferences.getStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF, requireContext());
        noticeCbx.setChecked(!noticeResult.equals(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_OFF) && !noticeResult.equals(rhyaNetworkAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE));
        saveDataCbx.setChecked(!saveDataResult.equals(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_OFF) && !saveDataResult.equals(rhyaNetworkAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE));

        // ????????? ??????
        editInfoButton.setOnClickListener(v -> {
            // Intent ??????
            Intent intent = new Intent(requireContext(), EditMyInfoActivity.class);
            // ?????? ?????????
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.anim_next_activity_up_enter, R.anim.anim_next_activity_stay);
        });
        logoutButton.setOnClickListener(v -> {
            // Dialog ??????
            new DialogManager().createDialog_v3(requireContext(),
                    requireActivity().getWindowManager().getDefaultDisplay(),
                    "????????? ???????????? ???????????????????",
                    "????????????",
                    "??????",
                    false,
                    new DialogManager.DialogListener_v3() {
                        @Override
                        public void onClickListenerButtonYes(Dialog dialog) {
                            // ????????? ??????
                            rhyaNetworkAPI.rhyaSharedPreferences.removeString(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_AUTH_TOKEN, requireContext());
                            // ?????? ?????? --> activity_login.xml
                            // ??????????????? [ ????????? --> ?????? ]
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().overridePendingTransition(R.anim.anim_next_activity_out_left, R.anim.anim_next_activity_in_right);
                            requireActivity().finish();
                        }

                        @Override
                        public void onClickListenerButtonNo(Dialog dialog) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });
        });
        appLockButton.setOnClickListener(v -> {
            // Intent ??????
            Intent intent = new Intent(requireContext(), AppLockSettingActivity.class);
            // ?????? ?????????
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.anim_next_activity_up_enter, R.anim.anim_next_activity_stay);
        });
        noticeCbx.setOnClickListener(v -> {
            // ?????? ??????
            try {
                // ????????? ??????
                if (noticeCbx.isChecked()) {
                    rhyaNetworkAPI.rhyaSharedPreferences.setStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_NOTICE_ON_OFF, rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_ON, requireContext());
                }else {
                    rhyaNetworkAPI.rhyaSharedPreferences.setStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_NOTICE_ON_OFF, rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_OFF, requireContext());
                }
            }catch (Exception ex) {
                ex.printStackTrace();

                // ?????? ????????? ??????
                Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        });
        saveDataCbx.setOnClickListener(v -> {
            // ?????? ??????
            try {
                // ????????? ??????
                if (saveDataCbx.isChecked()) {
                    rhyaNetworkAPI.rhyaSharedPreferences.setStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF, rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_ON, requireContext());
                }else {
                    rhyaNetworkAPI.rhyaSharedPreferences.setStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF, rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_OFF, requireContext());
                }
            }catch (Exception ex) {
                ex.printStackTrace();

                // ?????? ????????? ??????
                Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * loadUserData - ????????? ????????? ??????
     */
    private void loadUserData() {
        // ?????? ?????? ??????
        if (!isLoadTask) {
            // ?????? ?????? ?????? ??????
            isLoadTask = true;
            // ????????? ?????? ??????
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    // ?????? ?????? ??????
                    requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> scrollView.setVisibility(View.GONE));

                    // Json key ?????????
                    final String json_key_result = "result";
                    final String json_result_success = "S";
                    final String json_key_id = "id";
                    final String json_key_name = "name";
                    final String json_key_type = "type";
                    final String json_key_phone = "phone";
                    final String json_key_image = "image";
                    final String json_key_location = "location";
                    final String json_key_location_name = "location_name";
                    final String json_key_location_phone = "location_phone";
                    final String json_key_description = "description";
                    final String json_key_license = "license";
                    final String json_key_others = "others";
                    final String json_key_foc = "foc";
                    final String json_key_is_doctor = "isdoctor";
                    final String json_key_email = "email";
                    final String json_key_birthday = "birthday";
                    final String json_key_reg_date = "rdate";

                    // ?????? ??????
                    try {
                        // ?????? ??????
                        ContentValues urlParm = new ContentValues(); // ????????????
                        urlParm.put(rhyaNetworkAPI.DR_TALK_AUTH_TOKEN_PARM, rhyaNetworkAPI.getAutoLogin());
                        RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                        String result = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_USER_INFO, urlParm); // ?????? URL ??? ?????? ???????????? ????????????.
                        result = URLDecoder.decode(result, "UTF-8");
                        // ?????? ????????? ??????
                        JSONObject jsonObject = new JSONObject(result);

                        // ?????? ??????
                        try {
                            // ?????? ??????
                            if (!jsonObject.getString(json_key_result).equals(json_result_success)) {
                                // Activity ?????? ??????
                                Activity activity = getActivity();
                                if (activity != null) {
                                    if (!activity.isFinishing()) {
                                        // ????????? ??????
                                        requireActivity().runOnUiThread(() -> ((MainActivity) getActivity()).onErrorAccountShowMsg());
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Json ????????? ??????
                        try {
                            // ?????? ??????
                            RhyaApplication.MyInfo.MyInfoVO myInfoVO;
                            if (jsonObject.getString(json_key_is_doctor).equals("T")) {
                                myInfoVO = new RhyaApplication.MyInfo.MyInfoVO(true,
                                        jsonObject.getString(json_key_id),
                                        jsonObject.getString(json_key_email),
                                        jsonObject.getString(json_key_birthday),
                                        jsonObject.getString(json_key_reg_date),
                                        jsonObject.getString(json_key_name),
                                        jsonObject.getString(json_key_description),
                                        jsonObject.getString(json_key_type),
                                        jsonObject.getString(json_key_phone),
                                        jsonObject.getString(json_key_image),
                                        jsonObject.getString(json_key_location),
                                        jsonObject.getString(json_key_location_name),
                                        jsonObject.getString(json_key_location_phone),
                                        jsonObject.getString(json_key_license),
                                        jsonObject.getString(json_key_others),
                                        jsonObject.getString(json_key_foc));
                                // Activity ?????? ??????
                                Activity activity = getActivity();
                                if (activity != null) {
                                    if (!activity.isFinishing()) {
                                        // UI ??????
                                        requireActivity().runOnUiThread(() -> {
                                            Glide.with(requireContext())
                                                    .load(R.drawable.ic_applogo_doctor)
                                                    .apply(new RequestOptions().circleCrop().centerCrop()).circleCrop()
                                                    .placeholder(R.drawable.ic_applogo_doctor)
                                                    .error(R.drawable.ic_applogo_doctor)
                                                    .fallback(R.drawable.ic_applogo_doctor)
                                                    .skipMemoryCache(false)
                                                    .into(userImageView); // ImageView

                                            // ?????? ??????
                                            try {
                                                userNameTextView.setText(jsonObject.getString(json_key_name));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                }
                            }else {
                                myInfoVO = new RhyaApplication.MyInfo.MyInfoVO(false,
                                        jsonObject.getString(json_key_id),
                                        jsonObject.getString(json_key_email),
                                        jsonObject.getString(json_key_birthday),
                                        jsonObject.getString(json_key_reg_date),
                                        jsonObject.getString(json_key_name),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null);
                                // Activity ?????? ??????
                                Activity activity = getActivity();
                                if (activity != null) {
                                    if (!activity.isFinishing()) {
                                        // UI ??????
                                        requireActivity().runOnUiThread(() -> {
                                            Glide.with(requireContext())
                                                    .load(R.drawable.ic_patient)
                                                    .apply(new RequestOptions().circleCrop().centerCrop()).circleCrop()
                                                    .placeholder(R.drawable.ic_patient)
                                                    .error(R.drawable.ic_patient)
                                                    .fallback(R.drawable.ic_patient)
                                                    .skipMemoryCache(false)
                                                    .into(userImageView); // ImageView

                                            // ?????? ??????
                                            try {
                                                userNameTextView.setText(jsonObject.getString(json_key_name));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                }
                            }

                            // ????????? ??????
                            RhyaApplication.MyInfo.myInfoVO = myInfoVO;
                        }catch (JSONException e) {
                            e.printStackTrace();

                            // ?????? ????????? ??????
                            ShowToastMessage(e.getMessage());
                        }

                        // Activity ?????? ??????
                        Activity activity = getActivity();
                        if (activity != null) {
                            if (!activity.isFinishing()) {
                                // ?????? ?????? ??????
                                requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                                requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                                requireActivity().runOnUiThread(() -> scrollView.setVisibility(View.VISIBLE));
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();

                        // ?????? ????????? ??????
                        ShowToastMessage(e.getMessage());
                    }

                    // ?????? ??????
                    isLoadTask = false;

                    return null;
                }

                @Override
                protected void onPostExecute(String result) { }
            }.execute(null);
        }
    }


    /**
     * createString - String ????????? ??????
     * @param array string ??????
     * @return ????????? ?????????
     */
    private String createString(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : array) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }


    /**
     * Toast ????????? ??????
     * @param msg ????????? ??????
     *
     */
    private void ShowToastMessage(String msg) {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
    }


    /**
     * ShowAD - ?????? ??????
     * @param view View
     */
    private void showAD(View view) {
        // ?????? ??????
        MobileAds.initialize(requireContext(), initializationStatus -> { // ????????? ?????? ?????????
            // ?????? ??????
            FrameLayout frameLayout = view.findViewById(R.id.googleAdContainerView);
            ConstraintLayout constraintLayout = view.findViewById(R.id.googleAdContainerLoadingView);
            AdView adView = new AdView(requireContext());


            // ================================================================
            // ============================ ?????? ID ============================
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            // ================================================================
            // ================================================================


            // layout ??????
            frameLayout.addView(adView);

            // ?????? ????????????
            AdRequest adRequest = new AdRequest.Builder().build();
            // ?????? ?????? ??????
            adView.setAdSize(getAdSize());
            adView.loadAd(adRequest);

            // ?????? ??????
            adView.setVisibility(View.GONE);

            // ????????? ??????
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    // ?????? ??????
                    constraintLayout.setVisibility(View.GONE);
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
        });
    }


    /**
     * getAdSize - ?????? ??????
     * @return AdSize
     */
    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth);
    }
}