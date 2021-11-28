package com.shake.burgers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shake.burgers.manager.Burger;
import com.shake.burgers.manager.BurgerAdapter;
import com.shake.burgers.manager.BurgerHolder;
import com.shake.burgers.libs.BaseActivity;
import com.shake.burgers.libs.BurgerAlertDialog;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    // address text view
    TextView address;
    // users's prefs like address
    SharedPreferences prefs;
    // current animation button
    int currentAnimation = 0;
    // burgers list
    ArrayList<Burger> burgersList;
    BurgerAdapter burgerAdapter;


    View openCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init prefs
        prefs = getSharedPreferences("data", 0);
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
        burgerAdapter = new BurgerAdapter(this);
        burgerAdapter.apply("" , burgersList);
        burgers.setAdapter(burgerAdapter);
        // get original Y point to compare it letter (make sure button not out of bounds)
        int originalAnimate = -dpToPx(78);
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

                    burgerAdapter.apply("" , burgersList);

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
        if (requestCode == 500 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);
                search(recognizedText);
            } else {
                search("");
            }

        } else if (resultCode == Activity.RESULT_OK && data != null) {
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
            new BurgerAlertDialog(this, "Change Your Address", newLocation, "New Address Here", "Change", new BurgerAlertDialog.OnAlertResultListener() {
                @Override
                public void OnAlertResult(String input) {

                    // update UI address text
                    address.setText(input);
                    // it's save new address in prefs
                    prefs.edit().putString("address", input).apply();
                }
            }).show();


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


    void search(String text) {
        new BurgerAlertDialog(this, "Easy and fast Search", text, "What is your favorite meal ?", "Search", new BurgerAlertDialog.OnAlertResultListener() {
            @Override
            public void OnAlertResult(String input) {
                burgerAdapter.apply(input , burgersList);
            }
        }).show();
    }

    public void open_cart(View view) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.cart_layout);

        bottomSheetDialog.show();
        bottomSheetDialog.findViewById(R.id.close_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                openCart.setVisibility(View.VISIBLE);
            }
        });
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        //bottomSheetDialog.getBehavior().setHalfExpandedRatio(0.9f);
        //   cart.setVisibility(View.VISIBLE);
        openCart.setVisibility(View.GONE);
    }

    public void speechToText(View view) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something!");
        try {

            startActivityForResult(i, 500);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this, "sorry! your device doesn,t support speach langauge!", Toast.LENGTH_LONG).show();
        }
    }

    public void search(View view) {
        search("");
    }

    @Override
    public void onBackPressed() {

        if(!burgerAdapter.lastSearch.isEmpty()){
            burgerAdapter.apply("",burgersList);
            return;
        }
        super.onBackPressed();
    }
}