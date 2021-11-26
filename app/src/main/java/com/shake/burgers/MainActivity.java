package com.shake.burgers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shake.burgers.libs.BaseActivity;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    // address text view
    TextView address;
    // users's prefs like address
    SharedPreferences prefs;
    // current animation button
    int currentAnimation = 0;
    // burgers list
    ArrayList<Burger> burgersList;
    RecyclerView.Adapter<BurgerHolder> burgerAdapter;


    View cart,openCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init prefs
        prefs = getSharedPreferences("data", 0);
        // init cart layout
        cart = findViewById(R.id.cart);
        // set user's address
        address = findViewById(R.id.address);
        address.setText(prefs.getString("address", "Jumeirah Lake Towers"));

// init open cart to animate it
         openCart = findViewById(R.id.open_cart);
// load sections
        RecyclerView sections = findViewById(R.id.sections);
        sections.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
// load sections online
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://all-go.net/burger/sections.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(final String r) {
                try {
                    // convert to utf8
                    String response = fixEncoding(r);
                    JSONArray sectionsList = new JSONArray(response);
// set adapter from online data
                    sections.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.section_item, parent, false)) {
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            };
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            try {
                                // load data from the json
                                JSONObject section = sectionsList.getJSONObject(position);
                                String id = section.getString("id");

                                String link = section.getString("image");
                                // change background is selected

                                (holder.itemView.findViewById(R.id.item_background)).setBackgroundResource(selected_section.equals(id) ? R.drawable.section_selected : R.drawable.section_unselected);
                                // change small image tint if selected
                                ImageView imageView = holder.itemView.findViewById(R.id.image);

                                load(link, imageView, R.drawable.loading);
                                ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(getResources().getColor(selected_section.equals(id) ? R.color.colorPrimary : R.color.colorPrimaryDark)));
// change selected on click

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        selected_section = id;
                                        notifyDataSetChanged();
                                        // load burgers after change the section
                                        loadBurgers();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public int getItemCount() {
                            return sectionsList.length();
                        }
                    });
                } catch (Exception e) {

                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);


        // load burgers list
        burgersList = new ArrayList<>();

        RecyclerView burgers = findViewById(R.id.burgers);
        burgers.setLayoutManager(new GridLayoutManager(this, 2));
        burgerAdapter = new RecyclerView.Adapter<BurgerHolder>() {
            @NonNull
            @Override
            public BurgerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new BurgerHolder(getLayoutInflater().inflate(R.layout.burger_item, parent, false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull BurgerHolder holder, int position) {
                Burger burger = burgersList.get(position);
                holder.bind(MainActivity.this, burger);
            }

            @Override
            public int getItemCount() {
                return burgersList.size();
            }
        };
        burgers.setAdapter(burgerAdapter);
        // get original Y point to compare it letter (make sure button not out of bounds)
        int originalAnimate = -Math.round(openCart.getTranslationY());
        currentAnimation = -Math.round(openCart.getTranslationY());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((NestedScrollView) findViewById(R.id.nested)).setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // get dif between current and old Y ponts
                    int dif = scrollY - oldScrollY;
                    // make if faster than normal scroll
                    currentAnimation += (dif * 2);
                    // make sure it's not out of bounds
                    if (currentAnimation > 0) {
                        currentAnimation = 0;
                    }
                    if (currentAnimation < originalAnimate) {
                        currentAnimation = originalAnimate;
                    }
                    // apply the animation
                    openCart.setTranslationY(-currentAnimation);

                }
            });

        }
        loadBurgers();
    }

    String selected_section = "1";

    void loadBurgers() {
progressDialog.show();
        // load sections online
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://all-go.net/burger/burgers.php?id=" + selected_section, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(final String r) {
                progressDialog.dismiss();
                try {
                    // convert to utf8
                    String response = fixEncoding(r);
                    JSONObject json = new JSONObject(response);
                    // get top ordered
                    Burger top = new Burger(json.getJSONObject("top"));
                    // load top ordered burger
                    new BurgerHolder(findViewById(R.id.most)).bind(MainActivity.this, top);

                    // load rest of burgers into our array
                    JSONArray list = json.getJSONArray("list");
                    burgersList.clear();
                    for (int i = 0; i < list.length(); i++) {
                        burgersList.add(new Burger(list.getJSONObject(i)));
                    }
                    // notify changes
                    burgerAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
        burgerAdapter.notifyDataSetChanged();
    }
    public static String fixEncoding(String response) {
        try {
            byte[] u = response.toString().getBytes(
                    "ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
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
                newLocation = addressData.getAddressList().get(0).getAddressLine(0);
            } catch (Exception e) {
                // if this location was have not text address
                newLocation = "";
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


    @Override
    public void onBackPressed() {

        if(cart.getVisibility()==View.VISIBLE){
            cart.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    public void close_cart(View view) {
        cart.setVisibility(View.GONE);
        openCart.setVisibility(View.VISIBLE);
    }
    public void open_cart(View view) {
        cart.setVisibility(View.VISIBLE);
        openCart.setVisibility(View.GONE);
    }

}