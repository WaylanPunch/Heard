package com.way.heard.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by pc on 2016/4/26.
 */
public class Util {
    public static ProgressDialog showSpinnerDialog(Activity activity) {
        // activity = modifyDialogContext(activity);

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage("Loading");
        if (activity.isFinishing() == false) {
            dialog.show();
        }
        return dialog;
    }
}
