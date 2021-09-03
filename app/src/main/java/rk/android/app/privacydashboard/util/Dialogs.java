package rk.android.app.privacydashboard.util;

import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.appinfo.AppInfoActivity;
import rk.android.app.privacydashboard.activities.donation.DonationActivity;
import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.activities.settings.indicator.IndicatorActivity;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;

public class Dialogs {

    public static void showWhatsNewDialog(Context context, LayoutInflater inflater, PreferenceManager preferenceManager, boolean force){

        int currentVersionNumber = 1;

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionNumber  = packageInfo.versionCode;
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (currentVersionNumber > preferenceManager.getVersionNumber() || force) {
            preferenceManager.setVersionNumber(currentVersionNumber);

            final View dialogView = inflater.inflate(R.layout.dialog_release, null);

            AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setCancelable(true)
                    .show();

            final MaterialButton button = dialogView.findViewById(R.id.button_ok);
            final MaterialButton github = dialogView.findViewById(R.id.button_github);
            final MaterialButton donate = dialogView.findViewById(R.id.button_donate);
            button.setOnClickListener(view -> dialogs.dismiss());
            github.setOnClickListener(view -> Utils.openLink(context,Constants.LINK_GITHUB));
            donate.setOnClickListener(view -> {
                Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                context.startActivity(new Intent(context, DonationActivity.class), bundle);
            });
            dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public static void showAutoStartDialog(Context context, LayoutInflater inflater){

            final View dialogView = inflater.inflate(R.layout.dialog_start_permission, null);

            AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setCancelable(true)
                    .show();

            final MaterialButton button = dialogView.findViewById(R.id.button_ok);
            button.setOnClickListener(view -> {
                Utils.openAutoStartAccordingToManufacturer(context);
                dialogs.dismiss();
            });

            dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void showHelpDialog(Context context, LayoutInflater inflater){

        final View dialogView = inflater.inflate(R.layout.dialog_help, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        final MaterialButton button = dialogView.findViewById(R.id.button_close);
        button.setOnClickListener(view -> dialogs.dismiss());

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void deleteLogs(Application application, Context context, LayoutInflater inflater, String title, String info, String permission){

        final View dialogView = inflater.inflate(R.layout.dialog_delete_logs, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        final TextView titleText = dialogView.findViewById(R.id.alertTitle);
        final TextView titleInfo = dialogView.findViewById(R.id.alertInfo);
        final MaterialButton buttonYes = dialogView.findViewById(R.id.button_yes);
        final MaterialButton buttonNo = dialogView.findViewById(R.id.button_no);

        titleText.setText(title);
        titleInfo.setText(info);

        buttonYes.setOnClickListener(view -> {
            LogsRepository logsRepository = new LogsRepository(application);
            if (permission!=null) {
                logsRepository.clearLogs(permission);
            }else {
                logsRepository.clearLogs();
            }
            dialogs.dismiss();
        });

        buttonNo.setOnClickListener(view -> dialogs.dismiss());

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void deleteAppLogs(Application application, Context context, LayoutInflater inflater, String title, String info, String packageName){

        final View dialogView = inflater.inflate(R.layout.dialog_delete_logs, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        final TextView titleText = dialogView.findViewById(R.id.alertTitle);
        final TextView titleInfo = dialogView.findViewById(R.id.alertInfo);
        final MaterialButton buttonYes = dialogView.findViewById(R.id.button_yes);
        final MaterialButton buttonNo = dialogView.findViewById(R.id.button_no);

        titleText.setText(title);
        titleInfo.setText(info);

        buttonYes.setOnClickListener(view -> {
            LogsRepository logsRepository = new LogsRepository(application);
            if (packageName!=null) {
                logsRepository.clearAppLogs(packageName);
            }else {
                logsRepository.clearLogs();
            }
            dialogs.dismiss();
        });

        buttonNo.setOnClickListener(view -> dialogs.dismiss());

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void excludeApp(ExcludedRepository repository, Context context, LayoutInflater inflater, String title,
                                  String info, String packageName, AppInfoActivity.OnDialogSubmit listener){

        final View dialogView = inflater.inflate(R.layout.dialog_exclude, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        final TextView titleText = dialogView.findViewById(R.id.alertTitle);
        final TextView titleInfo = dialogView.findViewById(R.id.alertInfo);
        final MaterialButton buttonYes = dialogView.findViewById(R.id.button_yes);
        final MaterialButton buttonNo = dialogView.findViewById(R.id.button_no);

        titleText.setText(title);
        titleInfo.setText(info);

        buttonYes.setOnClickListener(view -> {
            if (packageName!=null) {
                listener.OnSubmit();
            }
            dialogs.dismiss();
        });

        buttonNo.setOnClickListener(view -> dialogs.dismiss());

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @SuppressWarnings("deprecation")
    public static void showThemeDialog(Context context, LayoutInflater inflater, PreferenceManager preferenceManager, Class<?> cls){

        final View dialogView = inflater.inflate(R.layout.dialog_theme, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final RadioButton radioLight = dialogView.findViewById(R.id.radio_light);
        final RadioButton radioDark = dialogView.findViewById(R.id.radio_dark);
        final RadioButton radioSystem = dialogView.findViewById(R.id.radio_system);
        final RadioButton radioBattery = dialogView.findViewById(R.id.radio_battery);

        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                radioDark.setChecked(true);
                break;

            case AppCompatDelegate.MODE_NIGHT_NO:
                radioLight.setChecked(true);
                break;

            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                radioBattery.setChecked(true);
                break;


            default:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
            case AppCompatDelegate.MODE_NIGHT_AUTO_TIME:
                radioSystem.setChecked(true);
                break;
        }


        radioLight.setOnCheckedChangeListener((compoundButton, b) -> {
            Utils.setTheme(context,preferenceManager,AppCompatDelegate.MODE_NIGHT_NO, cls);
            dialogs.dismiss();
        });

        radioDark.setOnCheckedChangeListener((compoundButton, b) -> {
            Utils.setTheme(context,preferenceManager,AppCompatDelegate.MODE_NIGHT_YES, cls);
            dialogs.dismiss();
        });

        radioBattery.setOnCheckedChangeListener((compoundButton, b) -> {
            Utils.setTheme(context,preferenceManager,AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, cls);
            dialogs.dismiss();
        });

        radioSystem.setOnCheckedChangeListener((compoundButton, b) -> {
            Utils.setTheme(context,preferenceManager,AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, cls);
            dialogs.dismiss();
        });
    }

    public static void showIndicatorType(Context context, LayoutInflater inflater, PreferenceManager preferenceManager, IndicatorActivity.OnDialogSubmit listener){

        final View dialogView = inflater.inflate(R.layout.dialog_indicator_type, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final RadioButton radioIcon = dialogView.findViewById(R.id.radio_type_icon);
        final RadioButton radioDot = dialogView.findViewById(R.id.radio_type_dot);

        switch (preferenceManager.getPrivacyDotType()) {
            case Constants.DOTS_TYPE_ICON:
                radioIcon.setChecked(true);
                break;

            case Constants.DOTS_TYPE_DOT:
                radioDot.setChecked(true);
                break;
        }


        radioIcon.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotType(Constants.DOTS_TYPE_ICON);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioDot.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotType(Constants.DOTS_TYPE_DOT);
            listener.OnSubmit();
            dialogs.dismiss();
        });
    }

    public static void showExcludeType(Context context, LayoutInflater inflater, PreferenceManager preferenceManager, IndicatorActivity.OnDialogSubmit listener){

        final View dialogView = inflater.inflate(R.layout.dialog_exclude_type, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(false)
                .show();

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final CheckBox checkIndicator = dialogView.findViewById(R.id.check_type_indicator);
        final CheckBox checkNotification = dialogView.findViewById(R.id.check_type_notification);
        final CheckBox checkLogs = dialogView.findViewById(R.id.check_type_log);
        final MaterialButton buttonClose = dialogView.findViewById(R.id.button_close);
        final MaterialButton buttonDone = dialogView.findViewById(R.id.button_done);


        checkIndicator.setChecked(preferenceManager.isPrivacyExcludeIndicator());
        checkNotification.setChecked(preferenceManager.isPrivacyExcludeNotification());
        checkLogs.setChecked(preferenceManager.isPrivacyExcludeLogs());

        buttonDone.setOnClickListener(view -> {
            preferenceManager.setPrivacyExcludeIndicator(checkIndicator.isChecked());
            preferenceManager.setPrivacyExcludeNotification(checkNotification.isChecked());
            preferenceManager.setPrivacyExcludeLogs(checkLogs.isChecked());
            dialogs.dismiss();
            listener.OnSubmit();
        });

        buttonClose.setOnClickListener(view -> dialogs.dismiss());


    }

    public static void showIndicatorPosition(Context context, LayoutInflater inflater, PreferenceManager preferenceManager, IndicatorActivity.OnDialogSubmit listener){

        final View dialogView = inflater.inflate(R.layout.dialog_indicator_position, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final RadioButton radioTL = dialogView.findViewById(R.id.radio_position_tl);
        final RadioButton radioTC = dialogView.findViewById(R.id.radio_position_tc);
        final RadioButton radioTR = dialogView.findViewById(R.id.radio_position_tr);
        final RadioButton radioRC = dialogView.findViewById(R.id.radio_position_rc);
        final RadioButton radioBR = dialogView.findViewById(R.id.radio_position_br);
        final RadioButton radioBC = dialogView.findViewById(R.id.radio_position_bc);
        final RadioButton radioBL = dialogView.findViewById(R.id.radio_position_bl);
        final RadioButton radioLC = dialogView.findViewById(R.id.radio_position_lc);

        switch (preferenceManager.getPrivacyDotPosition()) {
            case Gravity.TOP | Gravity.START:
                radioTL.setChecked(true);
                break;

            case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
                radioTC.setChecked(true);
                break;

            case Gravity.TOP | Gravity.END:
                radioTR.setChecked(true);
                break;

            case Gravity.END | Gravity.CENTER_VERTICAL:
                radioRC.setChecked(true);
                break;

            case Gravity.BOTTOM | Gravity.END:
                radioBR.setChecked(true);
                break;

            case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                radioBC.setChecked(true);
                break;

            case Gravity.BOTTOM | Gravity.START:
                radioBL.setChecked(true);
                break;

            case Gravity.START | Gravity.CENTER_VERTICAL:
                radioLC.setChecked(true);
                break;
        }

        radioTL.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.TOP | Gravity.START);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioTC.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioTR.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.TOP | Gravity.END);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioRC.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.END | Gravity.CENTER_VERTICAL);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioBR.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.BOTTOM | Gravity.END);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioBC.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioBL.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.BOTTOM | Gravity.START);
            listener.OnSubmit();
            dialogs.dismiss();
        });

        radioLC.setOnCheckedChangeListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDotPosition(Gravity.START | Gravity.CENTER_VERTICAL);
            listener.OnSubmit();
            dialogs.dismiss();
        });


    }

}
