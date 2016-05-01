package com.way.heard.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by pc on 2016/4/26.
 */
public class Util {
    public static void toast(Context context, int strId) {
        toast(context, context.getString(strId));
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

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
