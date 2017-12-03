package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationException;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.zello.sdk.Tab;
import com.zello.sdk.Zello;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements com.zello.sdk.Events {

    final String TAG = "IdentificationActivity"; // For logs.
    private String profileId;
    private Handler logoutHandler = new Handler();
    private SpeakerIdentificationRestClient azureIdentificationClientObj = new SpeakerIdentificationRestClient(AzureUsersInfo.SUBSCRIPTION_KEY);
    private final List<UUID> AZURE_PROFILE_UUID = Arrays.asList(UUID.fromString("705edfbf-089e-4ab7-ad29-e62980ee4683"));
    private OperationLocation gIdentifyResult = null;
    private int callCount = 0;
    private AudioController audioControllerObj = new AudioController();
    private boolean enableIdentification = false;
    private AlertDialog alert;
    private AlertDialog alert2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private com.zello.sdk.AppState _appState = new com.zello.sdk.AppState();
    private com.zello.sdk.MessageIn _messageIn = new com.zello.sdk.MessageIn();
    private com.zello.sdk.MessageOut _messageOut = new com.zello.sdk.MessageOut();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            profileId = extras.getString("authProfileId");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.unauthorised_popup, null);
        builder.setView(mView);
        Button btnDismiss = (Button) mView.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                finish();
            }
        });
        alert = builder.create();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
        View mView2 = getLayoutInflater().inflate(R.layout.authorised_popup, null);
        builder2.setView(mView2);
        Button btnDismiss2 = (Button) mView2.findViewById(R.id.btnDismiss2);
        btnDismiss2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.dismiss();
            }
        });
        alert2 = builder2.create();

        Zello.getInstance().configure("net.loudtalks", this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        TextView callerID = ((TextView)findViewById(R.id.caller));
//        callerID.setVisibility(View.INVISIBLE);
//        callerID.setText("TRANSMITTING....");
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Zello.getInstance().setSelectedUserOrGateway("Everyone");
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView callerID = ((TextView)findViewById(R.id.caller));
                Button fab = (Button) findViewById(R.id.fab);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fab.setBackgroundResource(R.mipmap.rptt);
                        Zello.getInstance().beginMessage();
                        callerID.setVisibility(View.VISIBLE);
                        callerID.setText("TRANSMITTING....");
                        if (enableIdentification) {
                            audioControllerObj.startRecord();
                        } else {
                            logoutHandler.removeCallbacks(handlerCallback);
                        }
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        fab.setBackgroundResource(R.mipmap.gptt);
                        Zello.getInstance().endMessage();
                        callerID.setVisibility(View.INVISIBLE);
                        if (enableIdentification) {
                            fab.setEnabled(false);
                            audioControllerObj.stopRecord(true);
                            new AzureVerificationRequestTask().execute(AudioController.WAV_FILE);
                        } else {
                            logoutHandler.postDelayed(handlerCallback, 8000);
                        }
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        if (!enableIdentification) {
            logoutHandler.postDelayed(handlerCallback, 8000);
        }
    }

    @Override
    public void onBackPressed() {
        if (!enableIdentification) {
            logoutHandler.removeCallbacks(handlerCallback);
        }
        Intent intent = new Intent(MainActivity.this, profile.class);
        intent.putExtra("authProfileId", profileId);
        startActivity(intent);
        finish();
    }

    private Runnable handlerCallback = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    public void onSelectedContactChanged() {
        //updateSelectedContact();
    }
    @Override
    public  void onContactsChanged(){}
    @Override
    public void onMicrophonePermissionNotGranted(){}
    @Override
    public void onAudioStateChanged(){}
    @Override
    public void onLastContactsTabChanged(Tab var1){}
    @Override
    public void onAppStateChanged(){}
    @Override
    public void onMessageStateChanged() {
        updateMessageState();
    }

    private void updateMessageState() {
        TextView callerID = ((TextView)findViewById(R.id.caller));
        Zello.getInstance().getMessageIn(_messageIn);
        Zello.getInstance().getMessageOut(_messageOut);

        boolean incoming = _messageIn.isActive(); // Is incoming message active?
        boolean outgoing = _messageOut.isActive(); // Is outgoing message active?
        if (outgoing) {
            callerID.setText(_messageOut.getTo().getDisplayName());
        }
        else{
            String author = _messageIn.getAuthor().getDisplayName(); // Is message from channel?
            if (author != null && author.length() > 0) {
                callerID.setText(_messageIn.getFrom().getDisplayName() + " \\ " + author); // Show channel and author names
            } else {
                callerID.setText(_messageIn.getFrom().getDisplayName()); // Show sender name
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,  //change the fragment here
                                 Bundle savedInstanceState) {
            View rootView;
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.ptt1, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.callGroupText);
                textView.setText("Call Group: IPK Innoplex");
                textView = (TextView) rootView.findViewById(R.id.TXfreqText);
                textView.setText("Transmit Frequency: 159.5 MHz");
                textView = (TextView) rootView.findViewById(R.id.RXFreqText);
                textView.setText("Receive Frequency: 159.5 MHz");
                textView = (TextView) rootView.findViewById(R.id.TxPwrText);
                textView.setText("Tx Power: High");
            }
            else
            {
                rootView = inflater.inflate(R.layout.ptt_log, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.logTxt1);
                textView.setText("Call from ID: 1664513 5s ago");
                textView = (TextView) rootView.findViewById(R.id.logTxt2);
                textView.setText("Call from ID: 3216549 15s ago");
                textView = (TextView) rootView.findViewById(R.id.logTxt3);
                textView.setText("Call from ID: 584232 50s ago");
                textView = (TextView) rootView.findViewById(R.id.logTxt4);
                textView.setText("Call from ID: 54151632 59s ago");
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }

    }

    private class AzureVerificationRequestTask extends AsyncTask<String, Void, OperationLocation> {

        protected OperationLocation doInBackground(String... params) {
            if (params != null) {
                try {
                    File audio = new File(params[0]);
                    FileInputStream fis = null;

                    try {
                        fis = new FileInputStream(audio);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Audio file not found.");
                    }

                    if (fis != null) {
                        OperationLocation identificationResponse = null;

                        try {
                            identificationResponse = azureIdentificationClientObj.identify(fis, AZURE_PROFILE_UUID, true);
                        } catch (IOException | IdentificationException e) {
                            Log.e(TAG, "Error encountered during identification process. - " + e.getMessage());
                        }

                        gIdentifyResult = identificationResponse;
                        callCount = 0;
                        return identificationResponse;
                    }
                } catch(RuntimeException e){
                    Log.e(TAG, e.getMessage());
                }
            }

            return null;
        }

        protected void onPostExecute(OperationLocation response) {
            new AzureIdentifyChkStatRequest().execute(response);
        }
    }

    private class AzureIdentifyChkStatRequest extends AsyncTask<OperationLocation, Void, IdentificationOperation> {

        protected IdentificationOperation doInBackground(OperationLocation... params) {
            IdentificationOperation enrollmentResult = null;

            if (params != null) {
                try {
                    enrollmentResult = azureIdentificationClientObj.checkIdentificationStatus(params[0]);
                } catch (IOException | IdentificationException e) {
                    Log.e(TAG, "Error encountered during enrollment. - " + e.getMessage());
                }
            }

            return enrollmentResult;
        }

        protected void onPostExecute(IdentificationOperation result) {
            callCount ++;

            Log.e(TAG, "1");
            if (result == null || result.processingResult == null) {
                if (callCount < 3) {
                    new Handler().postDelayed(identificationHandlerCallback, 3000);
                } else {
                    Toast.makeText(MainActivity.this, ("Azure fails to response."), Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                callCount = 0;
                Log.e(TAG, "Authenticate person - " + result.processingResult.confidence + " " + result.processingResult.identifiedProfileId);
                if (result.processingResult.identifiedProfileId.toString().equals("00000000-0000-0000-0000-000000000000")){
                    alert.show();
                } else {
                    // Toast.makeText(MainActivity.this, ("Is an authorised person."), Toast.LENGTH_LONG).show();
                    Button fab = (Button) findViewById(R.id.fab);
                    fab.setEnabled(true);
                    alert2.show();
                }
            }
        }
    }

    private Runnable identificationHandlerCallback = new Runnable() {
        @Override
        public void run() {
            new AzureIdentifyChkStatRequest().execute(gIdentifyResult);
        }
    };
}
