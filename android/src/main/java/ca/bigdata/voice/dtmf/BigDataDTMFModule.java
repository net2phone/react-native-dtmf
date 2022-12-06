
package ca.bigdata.voice.dtmf;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.util.HashMap;

import android.media.ToneGenerator;
import android.media.AudioManager;

import android.content.Context;
import android.util.Log;

public class BigDataDTMFModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private final String TAG = "ca.bigdata.voice.dtmf.BigDataDTMFModule";

  public BigDataDTMFModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  private ToneGenerator mToneGenerator;

  @Override
  public String getName() {
    return "RNDtmf";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("DTMF_0", ToneGenerator.TONE_DTMF_0);
    constants.put("DTMF_1", ToneGenerator.TONE_DTMF_1);
    constants.put("DTMF_2", ToneGenerator.TONE_DTMF_2);
    constants.put("DTMF_3", ToneGenerator.TONE_DTMF_3);
    constants.put("DTMF_4", ToneGenerator.TONE_DTMF_4);
    constants.put("DTMF_5", ToneGenerator.TONE_DTMF_5);
    constants.put("DTMF_6", ToneGenerator.TONE_DTMF_6);
    constants.put("DTMF_7", ToneGenerator.TONE_DTMF_7);
    constants.put("DTMF_8", ToneGenerator.TONE_DTMF_8);
    constants.put("DTMF_9", ToneGenerator.TONE_DTMF_9);
    constants.put("DTMF_A", ToneGenerator.TONE_DTMF_A);
    constants.put("DTMF_B", ToneGenerator.TONE_DTMF_B);
    constants.put("DTMF_C", ToneGenerator.TONE_DTMF_C);
    constants.put("DTMF_D", ToneGenerator.TONE_DTMF_D);
    constants.put("DTMF_STAR", ToneGenerator.TONE_DTMF_S);
    constants.put("DTMF_POUND", ToneGenerator.TONE_DTMF_P);
    constants.put("DTMF_S", ToneGenerator.TONE_DTMF_S);
    constants.put("DTMF_P", ToneGenerator.TONE_DTMF_P);
    return constants;
  }

  private int resolveStreamType() {
    try {
      AudioManager audioManager = (AudioManager) reactContext.getSystemService(Context.AUDIO_SERVICE);
      int audioMode = audioManager.getMode();
      return audioMode == AudioManager.MODE_IN_CALL || audioMode == AudioManager.MODE_IN_COMMUNICATION ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
    } catch(Exception e) {
      Log.e(TAG, "Error resolving stream type", e);
    }
    return AudioManager.STREAM_MUSIC;
  }

  private boolean isMuted() {
    try {
      AudioManager audioManager = (AudioManager) reactContext.getSystemService(Context.AUDIO_SERVICE);
      if (audioManager.getRingerMode() ==  AudioManager.RINGER_MODE_NORMAL) {
        return false;
      }
      return true;
    } catch(Exception e) {
      Log.e(TAG, "Error resolving muted state", e);
    }
    return false;
  }

  @ReactMethod
  public void playTone(int tone, int duration) {
    int streamType = resolveStreamType();
    mToneGenerator = new ToneGenerator(streamType, isMuted() ? ToneGenerator.MIN_VOLUME : ToneGenerator.MAX_VOLUME);
    mToneGenerator.startTone(tone, duration);
  }

  @ReactMethod
  public void startTone(int tone) {
    playTone(tone, 5000);
  }

  @ReactMethod
  public void stopTone() {
    if (mToneGenerator == null) {
      return;
    }

    mToneGenerator.stopTone();
    mToneGenerator.release();
    mToneGenerator = null;
  }
}