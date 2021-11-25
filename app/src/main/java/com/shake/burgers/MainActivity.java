package com.shake.burgers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;

public class MainActivity extends BaseActivityApp {
    TextView address;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("data", 0);
        address = findViewById(R.id.address);
        address.setText(prefs.getString("address", "Jumeirah Lake Towers"));

        RecyclerView sections = findViewById(R.id.sections);
        sections.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.section_item,parent,false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 5;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.dismiss();
        if (resultCode == Activity.RESULT_OK && data != null) {
            AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
            String newLocation;
            try {
                newLocation=  addressData.getAddressList().get(0).getAddressLine(0);
            }catch (Exception e){
                newLocation="";
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            // Specify the alert dialog title
            String titleText = "Change Your Address";
            // Initialize a new foreground color span instance
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            // Initialize a new spannable string builder instance
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
            // Apply the text color span
            ssBuilder.setSpan(
                    foregroundColorSpan,
                    0,
                    titleText.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            alert.setTitle(ssBuilder);
            final EditText edittext = new EditText(this);
            edittext.setHint("New Address Here");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 100;
            layoutParams.rightMargin = 100;
            layoutParams.topMargin = 100;
            edittext.setTypeface(ResourcesCompat.getFont(this, R.font.futura_medium_bt));
            edittext.setLayoutParams(layoutParams);
            edittext.setText(newLocation);
            RelativeLayout layout = new RelativeLayout(this);
            layout.addView(edittext);
            alert.setView(layout);
            alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String msg = edittext.getText().toString();
                    address.setText(msg);
                    prefs.edit().putString("address", msg).apply();
                }
            });
            alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });
            alert.show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void change_address(View view) {
        progressDialog.show();
        setupLocationManager(new OnGetLocationListener() {
            @Override
            public void OnGetLocation(String lat, String lng) {

                detectLocation(MainActivity.this, lat, lng);
            }
        });


    }
}