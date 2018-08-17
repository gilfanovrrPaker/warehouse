package ru.npf_paker.wms;

import android.app.Activity;
import android.app.Dialog;
import android.arch.core.util.Function;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import data.Act;
import data.ActItem;
import helpers.MqttHelper;
import ru.npf_paker.wms.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InputFormFragment.OnLinkItemSelectedListener, ActsFragment.OnFragmentInteractionListener,
        BatchFragment.OnListFragmentInteractionListener, ActItemFragment.OnListFragmentInteractionListener {
    public static MqttHelper mqttHelper;
    public Dialog quantityDialog;
    BatchDialogFragment batchDialogFragment;

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

//        quantityDialog = new Dialog(MainActivity.this);
//
//        // Установите заголовок
//        quantityDialog.setTitle("Заголовок диалога");
//        // Передайте ссылку на разметку
//        quantityDialog.setContentView(R.layout.quantity_dialog);
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
//                Log.w("Debug", mqttMessage.toString());
                Function f = DataStore.functionHashMap.get(mqttHelper.subscriptionTopic);
                if (f != null){
                    f.apply(mqttMessage.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "func null", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), mqttMessage.toString().substring(0,100), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Toast.makeText(getApplicationContext(), "deliveryComplete", Toast.LENGTH_SHORT).show();
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

    private static final int REQUEST_BATCH = 1;
    private static final int REQUEST_QUANTITY = 2;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        for (Fragment fragment:getSupportFragmentManager().getFragments()) {
//            if (fragment instanceof NavigationDrawerFragment) {
//                continue;
//            }
//            else
                if (fragment!=null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
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
        } else if (id == R.id.nav_outcome_mode1) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.replace(R.id.contentLayout, fragment).commit();

//            Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
//            if(f != null)
//                getSupportFragmentManager().beginTransaction().remove(f).commit();

            Act act = new Act(false);
            BatchDialogFragment fragment = BatchDialogFragment.newInstance(act);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
            //            getSupportFragmentManager().beginTransaction().
//                    replace(R.id.contentLayout, fragment).commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BATCH:
                    String jsonS = data.getStringExtra(BatchDialogFragment.TAG_SELECTED);
//                    setBatch(s);
                    break;
                case REQUEST_QUANTITY:
                    int quantity = data.getIntExtra(QntDialogFragment.TAG_QNT_SELECTED, -1);
                    //используем полученные результаты
//                    setQuantity(Integer.toString(quantity));
                    break;
                //обработка других requestCode
            }
//            updateUI();
        }
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

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    public void newOutcomeMode1Item(Act act) {
//        BatchDialogFragment fragment = BatchDialogFragment.newInstance(act);
//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.contentLayout, fragment).commit();
        BatchDialogFragment fragment = BatchDialogFragment.newInstance(act);
        if (fragment.isAdded()){
            fragment.dismiss();
        }
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    public void OutcomeMode1Quantity(Act act) {
        QntDialogFragment fragment = QntDialogFragment.newInstance(act);
        if (fragment.isAdded()){
            fragment.dismiss();
        }
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    public void OutcomeMode1Receiver(Act act) {
        QntDialogFragment fragment = QntDialogFragment.newInstance(act);
        if (fragment.isAdded()){
            fragment.dismiss();
        }
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    public void OutcomeMode1SubmitForm(Act act) {
        ActItemFragment fragment = ActItemFragment.newInstance(act);
//        if (fragment.isAdded()){
//            fragment.dismiss();
//        }
//        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.contentLayout, fragment).commit();
    }

    @Override
    public void onListFragmentInteraction(ActItem item) {

    }
}
