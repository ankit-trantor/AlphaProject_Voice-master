package com.example.xjh786.myapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

class AudioController {
    /*
     * How to use AudioRecorder :-
     * http://audiorecordandroid.blogspot.sg/
     *
     * To convert PCM to WAV :-
     * https://stackoverflow.com/questions/37281430/how-to-convert-pcm-file-to-wav-or-mp3
     */

    /**
     * Constant variables
     */
    private final String TAG = "AudioController"; // For logs.
    static final String WAV_FILE = Environment.getExternalStorageDirectory() + "/VOVE.wav";
    private final String PCM_FILE = Environment.getExternalStorageDirectory() + "/VOVE.pcm";
    // static final String WAV_FILE = "/sdcard/VOVE.wav";
    // private final String PCM_FILE = "/sdcard/VOCE.pcm";

    private final int RECORDER_SAMPLERATE = 16000;
    private final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final int BUFFER_ELEMENTS_2_REC = 1024; // To play 2048 (2K) since 2 bytes we use only 1024
    private final int BYTES_PER_ELEMENT = 2; // 2 bytes in 16-bit format

    /**
     * Class level variables.
     */
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    int startRecord() {
        if (!isRecording) {
            int minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

            if (minBufferSize > (BUFFER_ELEMENTS_2_REC * BYTES_PER_ELEMENT)) {
                return ReturnCode.AUDIO_CONTROLLER_INVALID_BUFFER_SIZE;
            }

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING,
                    BUFFER_ELEMENTS_2_REC * BYTES_PER_ELEMENT);
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder_Thread");

            isRecording = true;
            recorder.startRecording();
            recordingThread.start();
            return ReturnCode.SUCCESS;
        } else {
            return ReturnCode.AUDIO_CONTROLLER_RECORDING_IS_RUNNING;
        }
    }

    int stopRecord(boolean furtherProcess) {
        int return_value = ReturnCode.FAILURE;

        if (null != recorder) {
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;

            if (furtherProcess) {
                File pcm_file = new File(PCM_FILE);
                File wave_file = new File(WAV_FILE);

                try {
                    rawToWave(pcm_file, wave_file);
                    return_value = ReturnCode.SUCCESS;
                } catch (IOException e) {
                    Log.e(TAG, "Conversion fail. - " + e.getMessage());
                    return_value = ReturnCode.AUDIO_CONTROLLER_CONVERSION_FAILURE;
                }
            } else {
                return_value = ReturnCode.SUCCESS;
            }
        }

        return return_value;

    }

    private void writeAudioDataToFile() {
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(PCM_FILE);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Fail file creation. - " + e.getMessage());
        }

        if (os != null) {
            short sData[] = new short[BUFFER_ELEMENTS_2_REC];

            while (isRecording) {
                recorder.read(sData, 0, BUFFER_ELEMENTS_2_REC);  // Gets the voice output from microphone to byte format.
                try {
                    byte bData[] = short2byte(sData);
                    os.write(bData, 0, BUFFER_ELEMENTS_2_REC * BYTES_PER_ELEMENT);  // Writes data to file from buffer stores the voice buffer.
                } catch (IOException e) {
                    Log.e(TAG, "Error encountered when writing to file. - " + e.getMessage());
                    break;
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                Log.e(TAG, "Fail closing output stream. - " + e.getMessage());
            }
        }
    }

    // Conversion of short to byte.
    private byte[] short2byte(short[] sData) {
        int shortArrSize = sData.length;
        byte[] bytes = new byte[shortArrSize * 2];

        for (int i = 0; i < shortArrSize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    // Conversion of PCM to WAV.
    private void rawToWave(final File rawFile, final File waveFile) throws IOException {
        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;

        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
            else
            {
                Log.d("Debug", "Input null 1");
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 16000); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            } else {
                Log.d("Debug", "Input null 1");
            }
        }
    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);

        try {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }
}