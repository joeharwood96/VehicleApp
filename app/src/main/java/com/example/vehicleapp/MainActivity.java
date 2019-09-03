package com.example.vehicleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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
 * @version 1.0
 * This main activity is used to show a list
 * of vehicle objects, the list can be clicked on
 * to show more details and a long click can be used
 * to delete vehicles
 */

public class MainActivity extends AppCompatActivity {
    //Initialize variables
    String[] vehicleValue;
    ArrayList<Vehicle> allVehicles = new ArrayList<>();
    final HashMap<String, String> params = new HashMap<>();
    Toolbar toolbar;

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
        setContentView(R.layout.activity_main);

        //Find the tool bar
        toolbar = findViewById(R.id.toolbar);
        // Set a title and logo for the tool bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setLogo(R.drawable.vlogo);

        // Execute the GetVehicleData class
        GetVehicleData vehicleData = new GetVehicleData();
        vehicleData.execute();


        ListView vehicleList = findViewById(R.id.vehicleList);

        /**
         * When an item in the vehicle list is clicked
         * show a toast message indicating which vehicle was clicked
         * and then send that specific vehicle to the details activity
         */
        vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "you pressed " + allVehicles.get(i).getMake(),Toast.LENGTH_SHORT).show();

                // declare a new intent and give it the context and
                // specify which activity you want to open/start
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                // add/put the selected vehicle object in to the intent which will
                // be passed over to the activity that is started
                // note we use a KEY:VALUE structure to pass variable/objects
                // between activities. Here the key is ‘vehicle’ and the value is
                // the cheese object from the allVehicles array list using the position
                // which is specified by the ‘i’ variable.
                intent.putExtra("vehicle", allVehicles.get(i));
                // launch the activity
                startActivity(intent);
            }
        });

        /**
         * When a long click has been done on a vehicle in the list send a toast message
         * indicating which vehicle had been pressed and call the showAlertDialog
         */
        vehicleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "you pressed " + allVehicles.get(i).getMake(),Toast.LENGTH_SHORT).show();
                showAlertDialog(view, i);
                return true;
            }
        });
    }
    //Over ride the onResume and run the GetVehicleData class
    @Override
    public void onResume()
    {
        super.onResume();
        GetVehicleData vehicleData = new GetVehicleData();
        vehicleData.execute();

    }
    //Create a menu for buttons to be placed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //Create a button in the menu that sends intent to the insert activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
    //Convert the incoming json into string
    public String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * This method is used to delete a vehicle
     * it results in a pop up asking the user if the are
     * sure they want to delete with two buttons cancel and
     * delete. If delete is pressed the DeleteData class is executed
     * @param v
     * @param i The position of the vehicle pressed in the list
     */
    public void showAlertDialog(View v, final int i) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Vehicle");
        alert.setMessage("Do you wish to delete vehicle?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Delete " + allVehicles.get(i).getMake(), Toast.LENGTH_SHORT).show();
                //The url with a hard coded valid api key and the vehicle id of the vehicle clicked
                String url = "http://10.0.2.2:8005/vehiclesdb/api?api=VCIY9TR1DRIF668R0S&vehicle_id=" + allVehicles.get(i).getVehicle_id();
                performPostCall(url, params);

               DeleteData dd = new DeleteData();

               dd.execute(url, "Delete" , "");

                //Set the intent to the Main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
            }
        });
        //If the cancel button it pressed do nothing but show a toast message
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }

    /**
     * This method is used to create a Http connection
     * then delete a vehicle from the database
     * @param requestURL The url of the api
     * @param postDataParams A hash map of vehicles
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
            conn.setRequestMethod("DELETE");
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
            //If the response code is 200 then toast vehicle deleted
            if(responseCode == HttpURLConnection.HTTP_OK) {
                //Run in main UI thread
                MainActivity.runOnUI(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Vehicle deleted :)", Toast.LENGTH_LONG).show();
                    }
                });
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                //Run in main UI thread
                MainActivity.runOnUI(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error Vehicle failed to delete :(", Toast.LENGTH_LONG).show();
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
     * @return
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
     * This class is used to get all of the vehicle data
     * using Http call. Then adds each vehicle to a list
     * @return allVehicles
     */
    private class GetVehicleData extends AsyncTask<Void, Void, ArrayList<Vehicle>> {

        //This method is ran in the background so that less is done on the main thread
        protected ArrayList<Vehicle> doInBackground(Void... v) {

            //Making a http call
            HttpURLConnection urlConnection;
            InputStream in = null;
            try {
                // the url we wish to connect to and a valid api key
                URL url = new URL("http://10.0.2.2:8005/vehiclesdb/api?api=VCIY9TR1DRIF668R0S");
                // open the connection to the specified URL
                urlConnection = (HttpURLConnection) url.openConnection();
                // get the response from the server in an input stream
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // covert the input stream to a string
            String response = convertStreamToString(in);
            // print the response to android monitor/log cat
            System.out.println("Server response = " + response);

            try {
                // declare a new json array and pass it the string response from the server
                // this will convert the string into a JSON array which we can then iterate
                // over using a loop
                JSONArray jsonArray = new JSONArray(response);
                // instantiate the vehicleValue array and set the size
                // to the amount of cheese object returned by the server
                vehicleValue = new String[jsonArray.length()];

                // use a for loop to iterate over the JSON array
                for (int i=0; i < jsonArray.length(); i++)
                {
                    // the following lines of code will get the name of the vehicles from the
                    // current JSON object and store it in a string and integer variables
                    int vehicle_id = Integer.parseInt(jsonArray.getJSONObject(i).get("vehicle_id").toString());
                    String make = jsonArray.getJSONObject(i).get("make").toString();
                    String model = jsonArray.getJSONObject(i).get("model").toString();
                    int year = Integer.parseInt(jsonArray.getJSONObject(i).get("year").toString());
                    int price = Integer.parseInt(jsonArray.getJSONObject(i).get("price").toString());
                    String license_number = jsonArray.getJSONObject(i).get("license_number").toString();
                    String colour = jsonArray.getJSONObject(i).get("colour").toString();
                    int number_doors = Integer.parseInt(jsonArray.getJSONObject(i).get("number_doors").toString());
                    String transmission = jsonArray.getJSONObject(i).get("transmission").toString();
                    int mileage = Integer.parseInt(jsonArray.getJSONObject(i).get("mileage").toString());
                    String fuel_type = jsonArray.getJSONObject(i).get("fuel_type").toString();
                    int engine_size = Integer.parseInt(jsonArray.getJSONObject(i).get("engine_size").toString());
                    String body_style = jsonArray.getJSONObject(i).get("body_style").toString();
                    String condition = jsonArray.getJSONObject(i).get("condition").toString();
                    String notes = jsonArray.getJSONObject(i).get("notes").toString();
                    boolean sold = jsonArray.getJSONObject(i).getBoolean("sold");

                    // print the make to log cat
                    System.out.println("make = " + make);
                    //Create a ne vehicle object with the results
                    Vehicle vehicle = new Vehicle(
                            vehicle_id,
                            make,
                            model,
                            year,
                            price,
                            license_number,
                            colour,
                            number_doors,
                            transmission,
                            mileage,
                            fuel_type,
                            engine_size,
                            body_style,
                            condition,
                            notes,
                            sold
                    );
                    allVehicles.add(vehicle);
                    // add the name of the current model to the vehicleValue array
                    vehicleValue [i] = make + " " + model + ": " + license_number;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return allVehicles;
        }
        //Once the doInBackground has run add the vehicles to the list
        protected void onPostExecute (ArrayList<Vehicle> vehicleArrayList) {
            super.onPostExecute(vehicleArrayList);

            ListView vehicleList = findViewById(R.id.vehicleList);

            ArrayAdapter arrayAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, vehicleValue);
            vehicleList.setAdapter(arrayAdapter);

        }
    }

    /**
     * This class is used to delete the vehicle
     * by getting the url and creating a new hash map
     * then calling performPostCall
     * @return null
      */
    class DeleteData extends AsyncTask<String, Void, String> {

        //This method is ran in the background so that less is done on the main thread
         @Override
         protected String doInBackground(String... strings) {
             //Get the strings at position 0 and 1
             String url = strings[0];
             String message = strings[1];

             System.out.println(message);

             final HashMap<String, String> params = new HashMap<>();
             //Call performPostCall
             performPostCall(url, params);

             return null;
         }
     }

}
