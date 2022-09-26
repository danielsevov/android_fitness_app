package com.personal.lifttoliveapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.API.PropertyConstructor;
import com.personal.lifttoliveapp.misc.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity{

    private EditText textEmail, textPassword, textName, textPhone;
    private AutoCompleteTextView textNationality, textCoach;
    private DatePicker birthCalendar;
    private String curDate, myId;
    private TextView textBirth;

    private FloatingActionButton buttonRegister, buttonCancel;

    private static final String TAG = "Register_ACTIVITY";

    private final HashMap<String, String> coaches = new HashMap<>();
    private ArrayList<String> myRoles, countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("LiftToLiveApp");

        myRoles = new ArrayList<>();
        countries = new ArrayList<String>();

        buttonRegister = findViewById(R.id.registerUserButton);
        buttonCancel = findViewById(R.id.backButton);
        textEmail = findViewById(R.id.enterEmailText);
        textPassword = findViewById(R.id.enterPasswordText);
        textName = findViewById(R.id.enterUserText);
        textPhone = findViewById(R.id.enterPhoneNumberText);
        textNationality = findViewById(R.id.enterNationalityText);
        birthCalendar = findViewById(R.id.enterBirthDate);
        textBirth = findViewById(R.id.enterBirthText);
        textCoach = findViewById(R.id.enterCoachText);

        initializeCoaches();
        initializeDatePicker();
        initializeNationalityAutocomplete();
        initializeButtons();

    }

    private void initializeButtons() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRegister();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    private void initializeNationalityAutocomplete() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        //Getting the instance of AutoCompleteTextView

        textNationality.setThreshold(1);//will start working from first character
        textNationality.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    private void initializeDatePicker() {
        ViewCompat.setNestedScrollingEnabled(birthCalendar, false);
        birthCalendar.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                curDate = i2 + "/" + i1 + "/" + i;
                System.out.println(curDate);
                birthCalendar.setVisibility(View.GONE);
                textBirth.setVisibility(View.VISIBLE);
                textBirth.setText(curDate);
            }
        });

        birthCalendar.setVisibility(View.GONE);
        birthCalendar.setMaxDate(System.currentTimeMillis());

        textBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthCalendar.setVisibility(View.VISIBLE);
                textBirth.setVisibility(View.GONE);
            }
        });
    }

    private void initializeCoaches() {
        ArrayList<String> coachIds = new ArrayList<>();

        BackendAPI.Send(null, "whoAmI", "GET", (res) -> {
            try {
                myId = res.getString("userId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            BackendAPI.Send(null, "roles", "GET", (result) -> {
                JSONArray array;
                try {
                    array = result.getJSONArray("data");
                    int i =0;
                    while(array.length() >= i+1){
                        JSONObject obj = array.getJSONObject(i);
                        if(obj.getString("name").equals("coach")) {
                            coachIds.add(obj.getString("userId"));
                            System.out.println(obj.getString("userId"));
                        }
                        if(obj.getString("userId").equals(myId)) {
                            myRoles.add(obj.getString("name"));
                        }
                        i++;
                    }

                    if(myRoles.contains("coach")) {
                        textCoach.setText(String.format("Me (%s)", myId));
                        textCoach.setEnabled(false);
                    }
                    else if(myRoles.contains("admin")){
                        BackendAPI.Send(null, "users", "GET", (result2) -> {
                            JSONArray array2;
                            try {
                                array2 = result2.getJSONArray("data");
                                int j =0;
                                while(array2.length() >= j+1){
                                    JSONObject obj = array2.getJSONObject(j);
                                    if(coachIds.contains(obj.getString("id"))) {
                                        coaches.put(obj.getString("name") + " ( " + obj.getString("id") + " )", obj.getString("id"));
                                        System.out.println(obj.getString("name"));
                                    }
                                    j++;
                                }

                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                                        (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(coaches.keySet()));
                                //Getting the instance of AutoCompleteTextView

                                textCoach.setThreshold(1);//will start working from first character
                                textCoach.setAdapter(adapter2);//setting the adapter data into the AutoCompleteTextView

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Method for processing the register form. Is called from register button
     */
    private void processRegister() {
            PropertyConstructor constructor = new PropertyConstructor()
                    .addProperty("name", textName.getText().toString())
                    .addProperty("email", textEmail.getText().toString())
                    .addProperty("password", textPassword.getText().toString())
                    .addProperty("phone_number", textPhone.getText().toString())
                    .addProperty("nationality", textNationality.getText().toString())
                    .addProperty("date_of_birth", curDate);

            if(myRoles.contains("coach")) constructor.addProperty("coach_id", myId);
            else if(myRoles.contains("admin")) constructor.addProperty("coach_id", coaches.get(textCoach.getText().toString()));

            if(isValid()) {
                BackendAPI.Send(constructor, "signup", "POST");
                goBack();
            }
    }

    private boolean isValid() {
        StringBuilder builder = new StringBuilder();
        builder.append("Errors:\n");
        int i = 0;

        if(textName.getText().length() < 2) {
            builder.append(++i + ": Name is too short!\n");
        }

        if(textEmail.getText().length() < 6) {
            builder.append(++i + ": Email address is too short!\n");
        }

        if(Helper.patternMatches(textEmail.getText().toString(), "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")) {
            builder.append(++i + ": Email pattern is not valid!\n");
        }

        if(textPhone.getText().length() < 9) {
            builder.append(++i + ": Phone number is too short!\n");
        }

        if(textPassword.getText().length() < 8) {
            builder.append(++i + ": Password must be at least 8 characters long!\n");
        }

        if(!countries.contains(textNationality.getText().toString())) {
            builder.append(++i + ": Valid nationality must be selected!\n");
        }

        if(textBirth.getText().toString().isEmpty()) {
            builder.append(++i + ": Birth date must be selected!\n");
        }

        if(!myRoles.contains("coach") && myRoles.contains("admin") && !coaches.containsKey(textCoach.getText().toString())) {
            builder.append(++i + ": Valid coach must be selected!\n");
        }

        if(!builder.toString().equals("Errors:\n")) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), builder.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        return true;
    }

    public void goBack() {
        Intent intent = new Intent(this, OverviewTraineesActivity.class);
        startActivity(intent);
        finish();
    }
}