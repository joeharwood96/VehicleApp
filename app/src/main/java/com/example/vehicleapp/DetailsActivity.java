package com.example.vehicleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joseph Harwood 16041849
 * The DetailsActivity is used to
 * show the detials of a single vehicle which has been
 * clicked on
 */
public class DetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Find the update button
        Button updateBtn = findViewById(R.id.updateButton);
        //Set a title and logo to the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setLogo(R.drawable.vlogo);

        // get the intent
        Bundle extras = getIntent().getExtras();
        // create a vehicle object from the vehicle object that was passed over from
        // the MainActivity.
        Vehicle vehicle = (Vehicle) extras.get("vehicle");
        System.out.println("received from the intent: "+vehicle.getMake());

        //Find the text views and set them to variables
        TextView heading = findViewById(R.id.textViewHeading);
        TextView model = findViewById(R.id.textViewModel);
        TextView licenceNumber = findViewById(R.id.textViewLicenceNumber);
        TextView year = findViewById(R.id.textViewYear);
        TextView price = findViewById(R.id.textPrice);
        TextView colour = findViewById(R.id.textColour);
        TextView transmission = findViewById(R.id.textTransmission);
        TextView mileage = findViewById(R.id.textMileage);
        TextView fuelType = findViewById(R.id.textFuelType);
        TextView engineSize = findViewById(R.id.textEngineSize);
        TextView doors = findViewById(R.id.textDoors);
        TextView body_style = findViewById(R.id.textBodyStyle);
        TextView condition = findViewById(R.id.textCondition);
        TextView notes = findViewById(R.id.textNotes);

        //Set the text of the text views
        heading.setText(vehicle.getMake());
        model.setText(vehicle.getModel());
        licenceNumber.setText(vehicle.getLicense_number());
        year.setText(String.valueOf(vehicle.getYear()));
        price.setText(String.valueOf(vehicle.getPrice()));
        colour.setText(vehicle.getColour());
        transmission.setText(vehicle.getTransmission());
        mileage.setText(String.valueOf(vehicle.getMileage()));
        fuelType.setText(vehicle.getFuel_type());
        engineSize.setText(String.valueOf(vehicle.getEngine_size()));
        doors.setText(String.valueOf(vehicle.getNumber_doors()));
        body_style.setText(String.valueOf(vehicle.getBody_style()));
        condition.setText(String.valueOf(vehicle.getCondition()));
        notes.setText(String.valueOf(vehicle.getNotes()));
        //Set string and integer variables to the vehicle
        // details sent through the intent
        String makes = vehicle.getMake();
        String models = vehicle.getModel();
        int years = vehicle.getYear();
        int prices = vehicle.getPrice();
        String licence_numbers = vehicle.getLicense_number();
        String colours = vehicle.getColour();
        int number_doors = vehicle.getNumber_doors();
        String transmissions = vehicle.getTransmission();
        int mileages = vehicle.getMileage();
        String fuel_types = vehicle.getFuel_type();
        int engine_sizes = vehicle.getEngine_size();
        String body_styles = vehicle.getBody_style();
        String conditions = vehicle.getCondition();
        String note = vehicle.getNotes();
        int vehicleID = vehicle.getVehicle_id();
        //Create a new vehicle object with the details
        final Vehicle updateVehicle = new Vehicle(
                vehicleID,
                makes,
                models,
                years,
                prices,
                licence_numbers,
                colours,
                number_doors,
                transmissions,
                mileages,
                fuel_types,
                engine_sizes,
                body_styles,
                conditions,
                note,
                false
        );

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // declare a new intent and give it the context and
                // specify which activity you want to open/start
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                // add/put the selected vehicle object in to the intent which will
                // be passed over to the activity that is started
                // note we use a KEY:VALUE structure to pass variable/objects
                // between activities. Here the key is ‘vehicle’ and the value is
                // the cheese object from the updateVehicle array list using the position
                // which is specified by the ‘i’ variable.
                intent.putExtra("vehicle", updateVehicle);
                // launch the activity
                startActivity(intent);
            }
        });

    }
}
