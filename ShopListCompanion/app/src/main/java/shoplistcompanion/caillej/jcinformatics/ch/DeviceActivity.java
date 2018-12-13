/**
 * Copyright (C) 2015 Garmin International Ltd.
 * Subject to Garmin SDK License Agreement and Wearables Application Developer Agreement.
 */
package shoplistcompanion.caillej.jcinformatics.ch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationInfoListener;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus;
import com.garmin.android.connectiq.ConnectIQ.IQSendMessageListener;
import com.garmin.android.connectiq.ConnectIQ.IQOpenApplicationListener;
import com.garmin.android.connectiq.ConnectIQ.IQOpenApplicationStatus;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import org.json.JSONException;

public class DeviceActivity extends Activity {
    //tag for debug purpose
    private static final String TAG = DeviceActivity.class.getSimpleName();

    public static final String IQDEVICE = "IQDevice";
    //public static final String MY_APP = "a3421feed289106a538cb9547ab12095";
    //public static final String MY_APP = "558ac0370958481494fd42eb07747d3d";
    public static final String MY_APP = "983e19e394fd47919160008c6888c78b";

    //data model
    MyPrefs prefs;

    //ui el
    private LinearLayout mShopListsLayout;
    private EditText mTxtNewListName;
    private Button mBtnStartApp;
    private Button mBtnUploadLists;
    private TextView mDeviceName;
    private TextView mDeviceStatus;
    private ConnectIQ mConnectIQ;
    private IQDevice mDevice;
    private IQApp mMyApp;
    private boolean mAppIsOpen;

    private IQOpenApplicationListener mOpenAppListener = new IQOpenApplicationListener() {
        @Override
        public void onOpenApplicationResponse(IQDevice device, IQApp app, IQOpenApplicationStatus status) {
            Toast.makeText(getApplicationContext(), "App Status: " + status.name(), Toast.LENGTH_SHORT).show();
            if (status == IQOpenApplicationStatus.APP_IS_ALREADY_RUNNING) {
                mAppIsOpen = true;
                mBtnStartApp.setText("Already started");
                mBtnUploadLists.setEnabled(true);
            } else {
                mAppIsOpen = false;
                mBtnStartApp.setText("Start app");
                //mBtnUploadLists.setEnabled(false);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.prefs = DataHolder.getInstance(this).getPrefs();

        Log.d(TAG,"LISTS:"+prefs.listsNames.toString());
        Log.d(TAG,"CONTENT:"+prefs.listsContent.toString());
        Log.d(TAG,"STATES:"+prefs.listsStates.toString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Intent intent = getIntent();
        mDevice = (IQDevice)intent.getParcelableExtra(IQDEVICE);
        mMyApp = new IQApp(MY_APP);
        mAppIsOpen = false;

        //get ui el
        mShopListsLayout = (LinearLayout)findViewById(R.id.shopListsLayout);
        mTxtNewListName = (EditText)findViewById(R.id.txtNewListName);
        mBtnStartApp = (Button)findViewById(R.id.btnRun);
        mBtnUploadLists = (Button)findViewById(R.id.btnUpload);

        mDeviceName = (TextView)findViewById(R.id.devicename);
        mDeviceStatus = (TextView)findViewById(R.id.devicestatus);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();

        if (mDevice != null) {
            mDeviceName.setText(mDevice.getFriendlyName());
            mDeviceStatus.setText(mDevice.getStatus().name());

            //no more listener
            //mOpenAppButton.setOnClickListener(this);

            // Get our instance of ConnectIQ.  Since we initialized it
            // in our MainActivity, there is no need to do so here, we
            // can just get a reference to the one and only instance.
            mConnectIQ = ConnectIQ.getInstance();
            try {
                mConnectIQ.registerForDeviceEvents(mDevice, new IQDeviceEventListener() {

                    @Override
                    public void onDeviceStatusChanged(IQDevice device, IQDeviceStatus status) {
                        // Since we will only get updates for this device, just display the status
                        mDeviceStatus.setText(status.name());
                    }

                });
            } catch (InvalidStateException e) {
                Log.wtf(TAG, "InvalidStateException:  We should not be here!");
            }

            // Let's check the status of our application on the device.
            try {
                mConnectIQ.getApplicationInfo(MY_APP, mDevice, new IQApplicationInfoListener() {

                    @Override
                    public void onApplicationInfoReceived(IQApp app) {
                        // This is a good thing. Now we can show our list of message options.

                        //todo change by my own !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                        /*
                        String[] options = getResources().getStringArray(R.array.send_message_display);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_list_item_1, options);
                        setListAdapter(adapter);
                        */

                        // Send a message to open the app
                        try {
                            Toast.makeText(getApplicationContext(), "Opening app...", Toast.LENGTH_SHORT).show();
                            mConnectIQ.openApplication(mDevice, app, mOpenAppListener);
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onApplicationNotInstalled(String applicationId) {
                        // The Comm widget is not installed on the device so we have
                        // to let the user know to install it.
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceActivity.this);
                        dialog.setTitle(R.string.missing_widget);
                        dialog.setMessage(R.string.missing_widget_message);
                        dialog.setPositiveButton(android.R.string.ok, null);
                        dialog.create().show();
                    }

                });
            } catch (InvalidStateException e1) {
            } catch (ServiceUnavailableException e1) {
            }

            // Let's register to receive messages from our application on the device.
            try {
                mConnectIQ.registerForAppEvents(mDevice, mMyApp, new IQApplicationEventListener() {

                    @Override
                    public void onMessageReceived(IQDevice device, IQApp app, List<Object> message, IQMessageStatus status) {
                        //receive message from watch
                        ArrayList list = (ArrayList<Object>)message.get(0);
                        prefs.listsNames   = (ArrayList<String>)list.get(0);
                        prefs.listsContent = (ArrayList<ArrayList<String>>)list.get(1);
                        prefs.listsStates  = (ArrayList<ArrayList<Boolean>>)list.get(2);
                        updateUI();

                        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceActivity.this);
                        dialog.setTitle("Lists received");
                        dialog.setMessage("The lists have been updated");
                        dialog.setPositiveButton(android.R.string.ok, null);
                        dialog.create().show();
                    }

                });
            } catch (InvalidStateException e) {
                Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //save settings
        DataHolder.getInstance(this).savePrefs();

        if (mDevice != null) {
            // It is a good idea to unregister everything and shut things down to
            // release resources and prevent unwanted callbacks.
            try {
                mConnectIQ.unregisterForDeviceEvents(mDevice);

                if (mMyApp != null) {
                    mConnectIQ.unregisterForApplicationEvents(mDevice, mMyApp);
                }
            } catch (InvalidStateException e) {
            }
        }
    }

    public void btnUploadClick(View v){
        ArrayList<ArrayList> output = new ArrayList<ArrayList>();
        output.add(prefs.listsNames);
        output.add(prefs.listsContent);
        output.add(prefs.listsStates);

        try {
            mConnectIQ.sendMessage(mDevice, mMyApp, output, new IQSendMessageListener() {

                @Override
                public void onMessageStatus(IQDevice device, IQApp app, IQMessageStatus status) {
                    Toast.makeText(DeviceActivity.this, status.name(), Toast.LENGTH_SHORT).show();
                }

            });
        } catch (InvalidStateException e) {
            Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_SHORT).show();
        } catch (ServiceUnavailableException e) {
            Toast.makeText(this, "ConnectIQ service is unavailable.   Is Garmin Connect Mobile installed and running?", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        //create lists dynamically

        //param for the inside layout (line)
        LinearLayout.LayoutParams paramsInnerLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //param for the inside layout elements
        LinearLayout.LayoutParams paramsWrapContentWeight1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        LinearLayout.LayoutParams paramsWrapContentWeight2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);


        //wipe all layout content
        mShopListsLayout.removeAllViews();


        for(int j=0;j<prefs.listsNames.size();j++) {
            LinearLayout layout2 = new LinearLayout(this);
            layout2.setOrientation(LinearLayout.HORIZONTAL);
            layout2.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);

            //lbl and btn of shopping list
            EditText txt = new EditText(this);
            txt.setText(prefs.listsNames.get(j));
            txt.setWidth(0);
            Button btn = new Button(this);
            btn.setText("Del");

            final int jj = j;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.listsNames.remove(jj);
                    prefs.listsStates.remove(jj);
                    prefs.listsContent.remove(jj);
                    updateUI();
                }
            });
            btn.setWidth(0);

            //add to inside layout
            layout2.addView(txt, paramsWrapContentWeight2);
            layout2.addView(btn, paramsWrapContentWeight1);

            //add layout to upper layout
            mShopListsLayout.addView(layout2, paramsInnerLayout);

            //each list item
            for (int i = 0; i < prefs.listsContent.get(j).size(); i++) {
                final int ii = i;

                LinearLayout layout3 = new LinearLayout(this);
                layout3.setOrientation(LinearLayout.HORIZONTAL);
                layout3.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
                //chk, lbl and btn of list item
                CheckBox chk = new CheckBox(this);
                chk.setChecked(prefs.listsStates.get(j).get(i));
                chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        prefs.listsStates.get(jj).set(ii,b);
                    }
                });


                EditText txt2 = new EditText(this);
                txt2.setText(prefs.listsContent.get(j).get(i));
                txt2.setWidth(0);
                Button btn2 = new Button(this);
                btn2.setText("x");

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prefs.listsContent.get(jj).remove(ii);
                        prefs.listsStates.get(jj).remove(ii);
                        updateUI();
                    }
                });
                btn2.setWidth(0);

                //add to inside layout
                layout3.addView(chk, paramsWrapContentWeight1);
                layout3.addView(txt2, paramsWrapContentWeight2);
                layout3.addView(btn2, paramsWrapContentWeight1);

                //add layout to upper layout
                mShopListsLayout.addView(layout3, paramsInnerLayout);
            }

            //to add list item

            LinearLayout layout4 = new LinearLayout(this);
            layout4.setOrientation(LinearLayout.HORIZONTAL);
            layout4.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
            //chk, lbl and btn of list item
            CheckBox chk = new CheckBox(this);
            EditText txt3 = new EditText(this);
            txt3.setHint("New item");
            txt3.setWidth(0);
            Button btn2 = new Button(this);
            btn2.setText("Add");

            final EditText tsttt = txt3;
            final CheckBox chk2  = chk;
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //WORKS ????????????????????????????????????????????????????????????????
                    prefs.listsContent.get(jj).add(tsttt.getText().toString());
                    prefs.listsStates.get(jj).add(chk2.isChecked());
                    updateUI();
                }
            });
            btn2.setWidth(0);

            //add to inside layout
            layout4.addView(chk, paramsWrapContentWeight1);
            layout4.addView(txt3, paramsWrapContentWeight2);
            layout4.addView(btn2, paramsWrapContentWeight1);

            //add layout to upper layout
            mShopListsLayout.addView(layout4, paramsInnerLayout);
        }
    }

    public void onAddListBtnClick(View v){
        prefs.listsNames.add(mTxtNewListName.getText().toString());
        prefs.listsContent.add(new ArrayList<String>());
        prefs.listsStates.add(new ArrayList<Boolean>());
        mTxtNewListName.setText("");
        updateUI();
        Log.d(TAG,"onAddListBtnClick");
    }

    public void btnOpenAppClick(View view) {
        Toast.makeText(getApplicationContext(), "Opening app...", Toast.LENGTH_SHORT).show();
        // Send a message to open the app
        try {
            mConnectIQ.openApplication(mDevice, mMyApp, mOpenAppListener);
        } catch (Exception ex) {
        }
    }
}
