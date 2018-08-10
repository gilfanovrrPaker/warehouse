package ru.npf_paker.wms;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import helpers.MqttHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InputFormFragment.OnLinkItemSelectedListener, ActsFragment.OnFragmentInteractionListener {
    MqttHelper mqttHelper;
    public Dialog quantityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.openDrawer(GravityCompat.START);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startMqtt();

        quantityDialog = new Dialog(MainActivity.this);

        // Установите заголовок
        quantityDialog.setTitle("Заголовок диалога");
        // Передайте ссылку на разметку
        quantityDialog.setContentView(R.layout.quantity_dialog);
        // Найдите элемент TextView внутри вашей разметки
        // и установите ему соответствующий текст
//        TextView text = (TextView) dialog.findViewById(R.id.dialogTextView);
//        text.setQuantity("Текст в диалоговом окне. Вы любите котов?");
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(getApplicationContext(), "connectComplete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "connectionLost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                Toast.makeText(getApplicationContext(), mqttMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_income) {
            try {
                InputFormFragment inputFormFragment = InputFormFragment.newInstance("", "");
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.contentLayout, inputFormFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_outcome) {
            try {
                InputFormFragment inputFormFragment = InputFormFragment.newInstance("", "");
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.contentLayout, inputFormFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_balance) {
            Toast.makeText(getApplicationContext(), "Вы выбрали камеру", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_acts) {
            ActsFragment actsFragment = ActsFragment.newInstance("", "");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.contentLayout, actsFragment).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_share) {

        }
        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Now we can define the action to take in the activity when the fragment event fires
    @Override
    public void onLinkItemSelected(String json) {
        mqttHelper.PublishToTopic("warehouse-input", json);
        ActsFragment actsFragment = ActsFragment.newInstance("", "");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.contentLayout, actsFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
