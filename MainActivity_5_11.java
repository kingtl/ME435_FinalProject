package edu.rosehulman.kingtl.integratedimagerec;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends GolfBallDeliveryActivity {

    private long mFirebaseUpdateCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Write a message to the database


        sendMessage("Still works!");
    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        super.onLocationChanged(x, y, heading, location);
        sendGpsInfo(x, y, heading);
    }

    private void sendGpsInfo(double x, double y, double heading) {
        mFirebaseRef.child("gps").child("x").setValue(x);
        mFirebaseRef.child("gps").child("y").setValue(y);
        if (heading > -180.0 && heading <= 180.0) {
            mFirebaseRef.child("gps").child("heading").setValue("No heading");
        }
    }

    @Override
    public void setState(State newState) {
        super.setState(newState);
        mFirebaseRef.child("state").setValue(newState);
    }

    public void sendMessage(String messageToSend) {
        mFirebaseRef.child("message").setValue(messageToSend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_next) {
            mViewFlipper.showNext();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
