package com.shake.burgers.libs;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.shake.burgers.R;

public class BurgerAlertDialog {
    public interface OnAlertResultListener{
        public  void  OnAlertResult(String input);
    }
    AlertDialog.Builder alert;
    public  BurgerAlertDialog(Activity activity , String title, String inputText  , String inputHolder , String okButton , OnAlertResultListener onAlertResultListener){

        // show dialog to manually edit
        alert = new AlertDialog.Builder(activity);

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.colorPrimary));
        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(title);
        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        alert.setTitle(ssBuilder);
        // add edit text to alert
        final EditText edittext = new EditText(activity);
        edittext.setHint(inputHolder);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 100;
        layoutParams.rightMargin = 100;
        layoutParams.topMargin = 100;
        edittext.setTypeface(ResourcesCompat.getFont(activity, R.font.futura_medium_bt));
        edittext.setLayoutParams(layoutParams);
        edittext.setText(inputText);
        RelativeLayout layout = new RelativeLayout(activity);
        layout.addView(edittext);
        // set view to alert dialog
        alert.setView(layout);
        alert.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // when user click apply
                String input = edittext.getText().toString();
                // send result input
             onAlertResultListener.OnAlertResult(input);
              }
        });
        // back button
        alert.setNegativeButton("Back", null);
        // show dialog

    }
    public void show(){
        alert.show();
    }
}
