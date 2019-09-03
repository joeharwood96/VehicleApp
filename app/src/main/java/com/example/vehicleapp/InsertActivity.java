package com.example.vehicleapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joseph Harwood 16041849
 * The Insert activity is used to
 * insert a new vehicle into
 * the database
 */
public class InsertActivity extends AppCompatActivity {
    // Get a handler variable
    public static Handler UIHandler;

    static
    {
        //Set the handler to the main UI thread
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable) {
        // Post to the UI handler
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        Button saveVehicleBtn = findViewById(R.id.saveVehicleBtn);

        /**
         * When the save button is clicked execute the
         * InsertData class
         */
        saveVehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Url and a valid api key
                String url = "http://10.0.2.2:8005/vehiclesdb/api?api=VCIY9TR1DRIF668R0S";

                InsertData id = new InsertData();
                id.execute(url, "Insert", "");
                //Set intent to the main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Create a connection and post the data using a post call
     * @param requestURL url being posted
     * @param postDataParams the vehicle object created
     * @return
     */
    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";

        try{
            url = new URL(requestURL);

            //create the connection object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //write/send/post data to the connection using output stream and buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            //Get the response code
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            //If the response code is success vehicle is inserted
            if(responseCode == HttpURLConnection.HTTP_OK) {
                //Toast message in main UI thread
                InsertActivity.runOnUI(new Runnable() {
                    public void run() {
                        Toast.makeText(InsertActivity.this, "Vehicle Inserted :)", Toast.LENGTH_LONG).show();
                    }
                });
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                //Toast message in main UI thread
                InsertActivity.runOnUI(new Runnable() {
                    public void run() {
                        Toast.makeText(InsertActivity.this, "Error Vehicle failed to insert :(", Toast.LENGTH_LONG).show();
                    }
                });
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("response = " + response);
        return response;
    }

    /**
     * This method gets the data being posted and turns it into
     * a String
     * @param params a vehicle
     * @return result as a string
     * @throws UnsupportedEncodingException
     */
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    /**
     * This class is used to insert a new vehicle
     * by calling the performPostCall method
     */
    class InsertData extends AsyncTask<String, Void, String> {


        //This method is ran in the background so that less is done on the main thread
        @Override
        protected String doInBackground(String... strings) {

            //Get the strings at position 0 and 1
            String url = strings[0];
            String message = strings[1];

            System.out.println(message);

            //Find the EditText fields to get the data input and store it into
            //Variables
            final EditText make = findViewById(R.id.editTextMake);
            final EditText model = findViewById(R.id.editTextModel);
            final EditText year = findViewById(R.id.editTextYear);
            final EditText price = findViewById(R.id.editTextPrice);
            final EditText licence_number = findViewById(R.id.editTextLicence);
            final EditText colour = findViewById(R.id.editTextColour);
            final EditText number_of_doors = findViewById(R.id.editTextDoors);
            final EditText transmission = findViewById(R.id.editTextTransmission);
            final EditText mileage = findViewById(R.id.editTextMileage);
            final EditText fuel_type = findViewById(R.id.editTextFuelType);
            final EditText engine_size = findViewById(R.id.editTextEngineSize);
            final EditText body_style = findViewById(R.id.editTextBodyStyle);
            final EditText condition = findViewById(R.id.editTextCondition);
            final EditText note = findViewById(R.id.editTextNote);
            final HashMap<String, String> params = new HashMap<>();
            Gson gson = new Gson();

            //Create variables getting the data input
            String makes = make.getText().toString();
            String models = model.getText().toString();
            int years = Integer.parseInt(year.getText().toString());
            int prices = Integer.parseInt(price.getText().toString());
            String licence_numbers = licence_number.getText().toString();
            String colours = colour.getText().toString();
            int doors = Integer.parseInt(number_of_doors.getText().toString());
            String transmissions = transmission.getText().toString();
            int mileages = Integer.parseInt(mileage.getText().toString());
            String fuel_types = fuel_type.getText().toString();
            int engine_sizes = Integer.parseInt(engine_size.getText().toString());
            String body_styles = body_style.getText().toString();
            String conditions = condition.getText().toString();
            String notes = note.getText().toString();
            //Create a new vehicle with the data input
            Vehicle vehicle = new Vehicle(
                    1,
                    makes,
                    models,
                    years,
                    prices,
                    licence_numbers,
                    colours,
                    doors,
                    transmissions,
                    mileages,
                    fuel_types,
                    engine_sizes,
                    body_styles,
                    conditions,
                    notes,
                    false
            );
            //Convert the vehicle to json
            String vehicleJson = gson.toJson(vehicle);
            System.out.println(vehicleJson);
            //Put the json vehicle into params
            params.put("json", vehicleJson);
            //Call the performPostCall method
            performPostCall(url, params);

            return null;
        }
    }


}
