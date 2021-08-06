package rk.android.app.privacydashboard.activities.settings.indicator;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivitySettingsIndicatorBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.util.Dialogs;
import rk.android.app.privacydashboard.util.Utils;

public class IndicatorActivity extends AppCompatActivity{

    Context context;

    PreferenceManager preferenceManager;
    ActivitySettingsIndicatorBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsIndicatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = IndicatorActivity.this;
        preferenceManager =  new PreferenceManager(getApplicationContext());

        setupToolbar();
        initValues();
        initOnClickListeners();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_dots_custom));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED,null);
            finish();
        });

        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.scrollView.canScrollVertically(Constants.SCROLL_DIRECTION_UP)){
                toolbar.setElevation(Constants.TOOLBAR_SCROLL_ELEVATION);
            }else{
                toolbar.setElevation(Constants.TOOLBAR_DEFAULT_ELEVATION);
            }
        });

    }

    private void initValues(){

        switch (preferenceManager.getPrivacyDotType()) {
            case Constants.DOTS_TYPE_ICON:
                binding.indicatorType.setInfo(getString(R.string.dots_custom_type_icon));
                break;

            case Constants.DOTS_TYPE_DOT:
                binding.indicatorType.setInfo(getString(R.string.dots_custom_type_dot));
                break;
        }

        switch (preferenceManager.getPrivacyDotPosition()) {
            case Gravity.TOP | Gravity.START:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_tl));
                break;

            case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_tc));
                break;

            case Gravity.TOP | Gravity.END:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_tr));
                break;

            case Gravity.END | Gravity.CENTER_VERTICAL:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_rc));
                break;

            case Gravity.BOTTOM | Gravity.END:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_br));
                break;

            case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_bc));
                break;

            case Gravity.BOTTOM | Gravity.START:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_bl));
                break;

            case Gravity.START | Gravity.CENTER_VERTICAL:
                binding.indicatorPosition.setInfo(getString(R.string.dots_custom_position_lc));
                break;
        }

        binding.indicatorMargin.setSwitchState(preferenceManager.isPrivacyDotMargin());
        binding.indicatorClick.setSwitchState(preferenceManager.isPrivacyDotClick());
        binding.indicatorSize.setProgress(preferenceManager.getPrivacyDotSize());
        binding.indicatorOpacity.setProgress(preferenceManager.getPrivacyDotOpacity());
        binding.indicatorMinimize.setSwitchState(preferenceManager.isPrivacyDotAutoHide());
        binding.indicatorMinimizeTimer.setProgress(preferenceManager.getPrivacyDotAutoHideTimer());

        if (preferenceManager.isPrivacyDotAutoHide()) {
            binding.indicatorMinimizeTimer.setAlpha(1f);
            binding.indicatorMinimizeTimer.setClickable(true);
            binding.indicatorMinimizeTimer.disableSeekbar(true);
        }else {
            binding.indicatorMinimizeTimer.setAlpha(0.5f);
            binding.indicatorMinimizeTimer.setClickable(false);
            binding.indicatorMinimizeTimer.disableSeekbar(false);
        }

        initPreview();

    }

    private void initPreview(){

        if (preferenceManager.isPrivacyDots()) {
            switch (preferenceManager.getPrivacyDotType()){
                case Constants.DOTS_TYPE_ICON:
                    binding.dots.iconCamera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_camera));
                    binding.dots.iconMicrophone.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_microphone));
                    binding.dots.iconLocation.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_location));
                    binding.dots.lyOverlay.setBackgroundResource(R.drawable.overlay_background);
                    break;

                case Constants.DOTS_TYPE_DOT:
                    binding.dots.iconCamera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_camera));
                    binding.dots.iconMicrophone.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_mic));
                    binding.dots.iconLocation.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_location));
                    binding.dots.lyOverlay.setBackground(null);
                    break;
            }
        } else {
            binding.dots.iconCamera.setImageDrawable(null);
            binding.dots.iconMicrophone.setImageDrawable(null);
            binding.dots.iconLocation.setImageDrawable(null);
        }

        initPreviewSize();
        initPreviewOpacity();

    }

    private void initPreviewSize(){

        int size = (int) (30 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);
        int padding = (int) (5 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);
        int image_padding = (int) (5 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);

        binding.dots.iconCamera.setPadding(image_padding, image_padding, image_padding, image_padding);
        binding.dots.iconMicrophone.setPadding(image_padding, image_padding, image_padding, image_padding);
        binding.dots.iconLocation.setPadding(image_padding, image_padding, image_padding, image_padding);
        binding.dots.iconCamera.setLayoutParams(params);
        binding.dots.iconMicrophone.setLayoutParams(params);
        binding.dots.iconLocation.setLayoutParams(params);

        binding.dots.lyOverlay.setPadding(padding, 0, padding, 0);

    }

    private void initPreviewOpacity(){
        binding.dots.lyOverlay.setAlpha((float) preferenceManager.getPrivacyDotOpacity()/100);
    }

    private void initOnClickListeners(){

        binding.indicatorType.setOnClickListener(view -> Dialogs.showIndicatorType(context, getLayoutInflater(), preferenceManager, this::initValues));

        binding.indicatorPosition.setOnClickListener(view -> Dialogs.showIndicatorPosition(context, getLayoutInflater(), preferenceManager, this::initValues));

        binding.indicatorMargin.setOnSwitchListener((compoundButton, b) -> preferenceManager.setPrivacyDotMargin(b));
        binding.indicatorMargin.setOnClickListener(view -> binding.indicatorMargin.performSwitchClick());

        binding.indicatorClick.setOnSwitchListener((compoundButton, b) -> preferenceManager.setPrivacyDotClick(b));
        binding.indicatorClick.setOnClickListener(view -> binding.indicatorClick.performSwitchClick());

        binding.indicatorSize.setListener(progress -> {
            preferenceManager.setPrivacyDotSize(progress);
            initPreviewSize();
        });

        binding.indicatorOpacity.setListener(progress -> {
            preferenceManager.setPrivacyDotOpacity(progress);
            initPreviewOpacity();
        });

        binding.indicatorMinimize.setOnSwitchListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotAutoHide(b);
            if (b) {
                binding.indicatorMinimizeTimer.setAlpha(1f);
                binding.indicatorMinimizeTimer.setClickable(true);
                binding.indicatorMinimizeTimer.disableSeekbar(true);
            }else {
                binding.indicatorMinimizeTimer.setAlpha(0.5f);
                binding.indicatorMinimizeTimer.setClickable(false);
                binding.indicatorMinimizeTimer.disableSeekbar(false);
            }
        });
        binding.indicatorMinimize.setOnClickListener(view -> binding.indicatorMinimize.performSwitchClick());
        binding.indicatorMinimizeTimer.setListener(progress -> preferenceManager.setPrivacyDotAutoHideTimer(progress));

    }


    public interface OnDialogSubmit{
        void OnSubmit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }
}
