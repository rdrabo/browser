package com.example.admin.mybrowser;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

//Note: this activity is based off of the connection module with the addition of the
//check boxes,end button, and prompt

public class MainActivity extends Activity {

    //Declare the checkbox ojects
    private CheckBox wifi, celldata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the check box objects to the appropriate views in the xml
        wifi = (CheckBox) (findViewById(R.id.checkWifi));
        celldata = (CheckBox) (findViewById(R.id.checkCellulardata));

    }

    public void buttonClicked(View view) {
        ConnectivityManager cmanager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //Get info about the network the device is on
        NetworkInfo networkInfoObj = cmanager.getActiveNetworkInfo();
        String message = null;

        //If connected, Check for WIFI
        if (networkInfoObj != null && networkInfoObj.isConnected()) {
            //If Wifi checked
            if (networkInfoObj.getType() == ConnectivityManager.TYPE_WIFI && wifi.isChecked()) {
                message = "Connected to WIFI";
                startActivity(new Intent(this, Browser.class));
            }
            //If connected to WIFI then notify user to check off the box in order to make it clear
            //to the user that they are using wifi
            else if(networkInfoObj.getType() == ConnectivityManager.TYPE_WIFI && wifi.isChecked()== false){
                message = "You are connected to WI-FI. Please check off WIFI to proceed.";
            }
            //If cellular data is checked and wifi is not then goto youtube
            else if (networkInfoObj.getType() == ConnectivityManager.TYPE_MOBILE && celldata.isChecked()) {
                message = "Using data plan";
                startActivity(new Intent(this, Browser.class));
            }
            //If not connected to WIFI but connected to cellular data then notify user to check
            //off the box in order to make it clear to the user that they are using cellular data
            else if (networkInfoObj.getType() == ConnectivityManager.TYPE_MOBILE && celldata.isChecked()==false){
                message = "You are are using your provider data plan. Please check off cellular data to proceed.";
            }
            //If some how not connected to either wi-fi or cellular data
            else
                message = "Not connected to CELLULAR DATA or WI-FI";

            //If not connected to the internet when the button is pushed then close the app
        } else {
            message = "You are not connected to the Internet!";
            finish();
        }

        // 6. Show a toast that was set by one of the statements
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
                .show();
    }

    //Allow the user a button to exit the application
    public void end(View view){
        finish();
    }

}
