package com.shake.burgers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shake.burgers.libs.BaseActivity;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;

public class MainActivity extends BaseActivity {
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
        sections.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

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
                // change background is selected
                (holder.itemView.findViewById(R.id.item_background)).setBackgroundResource(position==selected_section ? R.drawable.section_selected: R.drawable.section_unselected);
                // change small image tint if selected
                ImageView imageView = holder.itemView.findViewById(R.id.image);
                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(getResources().getColor(position==selected_section ?R.color.colorPrimary:R.color.colorPrimaryDark)));
// change selected on click

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selected_section = position;
                        notifyDataSetChanged();
                        // load burgers after change the section
                        loadBurgers();
                    }
                });


            }

            @Override
            public int getItemCount() {
                return 5;
            }
        });

        RecyclerView burgers = findViewById(R.id.burgers);
        burgers.setLayoutManager(new GridLayoutManager(this,2));
        burgers.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.burger_item,parent,false)) {
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
    int selected_section = 0;

    void loadBurgers(){

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // when get text address hide loading dialog
        progressDialog.dismiss();
        if (resultCode == Activity.RESULT_OK && data != null) {
            // if data is correct get address line
            AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
            String newLocation;
            try {
                newLocation=  addressData.getAddressList().get(0).getAddressLine(0);
            }catch (Exception e){
                // if this location was have not text address
                newLocation="";
            }
            // show dialog to manually edit
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
            // add edit text to alert
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
            // set view to alert dialog
            alert.setView(layout);
            alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                  // when user click apply
                    String msg = edittext.getText().toString();
                    // update UI address text
                    address.setText(msg);
                    // it's save new address in prefs
                    prefs.edit().putString("address", msg).apply();
                }
            });
            // back button
            alert.setNegativeButton("Back", null);
            // show dialog
            alert.show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void change_address(View view) {
        // show loading while get current location
        progressDialog.show();
        setupLocationManager(new OnGetLocationListener() {
            @Override
            public void OnGetLocation(String lat, String lng) {
               // after get current location it's get text address
                detectLocation(MainActivity.this, lat, lng);
            }
        });


    }
}