package com.example.xjh786.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitive.speakerrecognition.SpeakerVerificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.verification.Result;
import com.microsoft.cognitive.speakerrecognition.contract.verification.Verification;
import com.microsoft.cognitive.speakerrecognition.contract.verification.VerificationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

//

/**
 * Created by XJH786 on 9/8/2017.
 */

public class firstPage extends AppCompatActivity {

    /**
     * Constant variables.
     */
    final String TAG = "AuthenticationActivity"; // For logs.
    final int HANDLER_TIMEOUT_DURATION = 15000 + 1000; // 15sec recording timeout duration.
    final String[] AZURE_PROFILE_ID = {
//            "042fbc8d-7081-4ee9-aae2-90a691bf1cb6", // TH
            "3209c42f-545c-474e-8d3f-1022d52ac765", // CY
//            "016ab45a-dc53-4ccb-a97d-7e848266ad4a", // Marilyn
            "78540070-d192-474b-b089-a2190bd57347", // Amir
//            "76fd3c3a-fab3-4692-8473-e9d19c48875d", // Adrian
//            "4fef5be3-a534-4788-b521-7d899d5cf96a", // SoonLengYap
//            "ab7bb31c-1c00-4caa-a975-863999e6bd5b"  // Eugene
    };
    private enum BUTTON_STATE
    {
        IDLE,
        RECORDING,
        PROCESSING
    }

    /**
     * Class level variables.
     */
    private AudioController audioControllerObj; // Control enable/disable mic and audio conversion.
    private Handler recorderHandler;  // For recording timeout.
    private SpeakerVerificationRestClient azureVerificationClientObj;
    private AzureVerificationResponseController azureVerificationResponseController; // To keep track verification asynctask result.
    private BUTTON_STATE buttonState = BUTTON_STATE.IDLE;
    private Map<BUTTON_STATE, String> buttonStateString = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);
        Button btnStart = ((Button) findViewById(R.id.btnStart));
        btnStart.setOnClickListener(btnClick);
        Button btnDummyNext = ((Button) findViewById(R.id.button2));
        TextView heading = ((TextView)findViewById(R.id.textView));
        heading.setText("SPEAK YOUR PASS PHRASE");
        btnDummyNext.setOnClickListener(btnClick);
        btnDummyNext.setVisibility(View.INVISIBLE);
        audioControllerObj = new AudioController();
        recorderHandler = new Handler();
        azureVerificationClientObj = new SpeakerVerificationRestClient(AzureUsersInfo.SUBSCRIPTION_KEY);
        azureVerificationResponseController = new AzureVerificationResponseController(AZURE_PROFILE_ID.length);

        buttonStateString.put(BUTTON_STATE.IDLE, "TAP TO SPEAK");
        buttonStateString.put(BUTTON_STATE.RECORDING, "RECORDING");
        buttonStateString.put(BUTTON_STATE.PROCESSING, "AUTHENTICATING");
        btnStart.setEnabled(true);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            TextView heading = ((TextView)findViewById(R.id.textView));
            switch (v.getId()) {
                case R.id.btnStart: {
                    Button btnStart = ((Button) findViewById(R.id.btnStart));
                    int return_value;

                    switch (buttonState) {
                        case RECORDING:
                            btnStart.setEnabled(true);
                            return_value = audioControllerObj.stopRecord(true);
                            switch (return_value) {
                                case ReturnCode.SUCCESS:
                                    recorderHandler.removeCallbacks(handlerCallback);
                                    for (String profileId : AZURE_PROFILE_ID) {
                                        new AzureVerificationRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profileId, AudioController.WAV_FILE);
                                    }
                                    buttonState = BUTTON_STATE.PROCESSING;
                                    btnStart.setEnabled(false);

                                    break;

                                case ReturnCode.AUDIO_CONTROLLER_CONVERSION_FAILURE:
                                    Toast.makeText(firstPage.this, ("Audio conversion fails."), Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    break;
                            }

                            break;

                        case IDLE:

                            return_value = audioControllerObj.startRecord();
                            btnStart.setEnabled(true);
                            switch (return_value) {
                                case ReturnCode.SUCCESS:
                                    if (azureVerificationResponseController.reset()) {
                                        recorderHandler.postDelayed(handlerCallback, HANDLER_TIMEOUT_DURATION);
                                        buttonState = BUTTON_STATE.RECORDING;
                                    } else {
                                        audioControllerObj.stopRecord(false);
                                        Toast.makeText(firstPage.this, ("Previous authentication" +
                                                "is running."),  Toast.LENGTH_LONG).show();
                                    }
                                    break;

                                case ReturnCode.AUDIO_CONTROLLER_INVALID_BUFFER_SIZE:
                                    Toast.makeText(firstPage.this, ("Invalid buffer size."),
                                            Toast.LENGTH_LONG).show();
                                    break;

                                case ReturnCode.AUDIO_CONTROLLER_RECORDING_IS_RUNNING:
                                    Toast.makeText(firstPage.this, ("Recording was running."),
                                            Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case PROCESSING:
                            break;

                        default:
                            break;
                    }
                    if(buttonState == BUTTON_STATE.PROCESSING)
                    {   heading.setText("PROCESSING...");
                        heading.setTextColor(Color.RED);
                    }
                    else if(buttonState == BUTTON_STATE.RECORDING)
                    {   heading.setText("RECORDING");
                        heading.setTextColor(Color.BLACK);
                    }
                    else //todo: not working
                    {   heading.setText("SPEAK YOUR PASS PHRASE");
                        heading.setTextColor(Color.BLACK);
                    }
                    btnStart.setText(buttonStateString.get(buttonState));
                    break;
                }

                case R.id.button2:
                    Intent intent = new Intent(firstPage.this, profile.class);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };

    private Runnable handlerCallback = new Runnable() {
        @Override
        public void run() {
            audioControllerObj.stopRecord(false);
            recorderHandler.removeCallbacks(handlerCallback);
        }
    };

    private class AzureVerificationResult {
        Verification mResult;
        UUID mProfileId;

        AzureVerificationResult(Verification result, UUID profileId) {
            this.mResult = result;
            this.mProfileId = profileId;
        }
    }

    private class AzureVerificationResponseController {
        /**
         * Constant variables.
         */
        private final int INITIAL = 0;
        private final int SUCCESS = 1;
        private final int MULTI_SUCCESS = 2;

        /**
         * Class level variables.
         */
        private int maxCount;
        private int azureRequestCount;
        private int azureRequestSuccess;
        private AzureVerificationResult validResult;
        private final ReentrantLock rLock = new ReentrantLock();

        AzureVerificationResponseController(int arrLen) {
            maxCount = arrLen;
        }

        private boolean reset() {
            boolean returnValue;
            rLock.lock();

            if (azureRequestCount == 0) {
                azureRequestCount = maxCount;
                azureRequestSuccess = INITIAL;
                validResult = null;
                returnValue = true;
            } else {
                returnValue = false;
            }

            rLock.unlock();
            return returnValue;
        }

        private int update(AzureVerificationResult response) {
            rLock.lock();
            azureRequestCount --;

            if (response != null) {
                if (response.mResult.result == Result.ACCEPT) {
                    switch (azureRequestSuccess) {
                        case INITIAL: {
                            azureRequestSuccess = SUCCESS;
                            validResult = response;
                            break;
                        }

                        case SUCCESS: {
                            azureRequestSuccess = MULTI_SUCCESS;
                            validResult = null;
                            break;
                        }

                        case MULTI_SUCCESS:
                            break;

                        default:
                            break;
                    }
                }
            }

            return azureRequestCount;
        }

        private AzureVerificationResult getResult() {
            return validResult;
        }
    }

    private class AzureVerificationRequestTask extends AsyncTask<String, Void, AzureVerificationResult> {

        protected AzureVerificationResult doInBackground(String... params) {
            if (params != null) {
                try {
                    UUID profileId = UUID.fromString(params[0]);
                    File audio = new File(params[1]);
                    FileInputStream fis = null;

                    try {
                        fis = new FileInputStream(audio);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Audio file not found.");
                    }

                    if (fis != null) {
                        Verification verificationResponse = null;

                        try {
                            verificationResponse = azureVerificationClientObj.verify(fis, profileId);
                        } catch (IOException | VerificationException e) {
                            Log.e(TAG, "Error encountered during verification process. - " + e.getMessage());
                        }

                        if (verificationResponse != null) {
                            return new AzureVerificationResult(verificationResponse, profileId);
                        }
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            return null;
        }

        protected void onPostExecute(AzureVerificationResult response) {
            if (azureVerificationResponseController.update(response) == 0) {
                AzureVerificationResult result = azureVerificationResponseController.getResult();

                if (result != null) {
                    String result_to_show = result.mResult.result.toString() + " : ";
                    result_to_show = result_to_show + result.mResult.confidence.toString() + " : ";
                    result_to_show = result_to_show + result.mResult.phrase;
                    Toast.makeText(firstPage.this, result_to_show, Toast.LENGTH_LONG).show();
                    Log.d(TAG, result_to_show);
                    Toast.makeText(firstPage.this, result_to_show, Toast.LENGTH_LONG).show();

                    if (result.mResult.result == Result.ACCEPT) {
                        Intent intent = new Intent(firstPage.this, profile.class);
                        intent.putExtra("authProfileId", response.mProfileId.toString());
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "Verification Fail.");
                    Toast.makeText(firstPage.this, ("Verification Fail."), Toast.LENGTH_LONG).show();
                }

                buttonState = BUTTON_STATE.IDLE;
                Button btnStart = ((Button) findViewById(R.id.btnStart));
                btnStart.setText(buttonStateString.get(buttonState));
                btnStart.setEnabled(true);
                TextView heading = ((TextView)findViewById(R.id.textView));
                heading.setText("SPEAK YOUR PASS PHRASE");
                heading.setTextColor(Color.BLACK);
            }
        }
    }
}
