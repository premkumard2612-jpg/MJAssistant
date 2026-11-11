
package com.mj.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private TextView status;
    private static final int SPEECH_CODE = 100;
    private static final int MIC_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(28, 28, 28, 28);
        layout.setBackgroundColor(Color.rgb(18, 18, 35));

        TextView title = new TextView(this);
        title.setText("MJ");
        title.setTextSize(42);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setText("Yes Boss! Main MJ hoon ❤️");
        status.setTextSize(19);
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);

        Button listen = new Button(this);
        listen.setText("🎙 Talk to MJ");

        Button youtube = new Button(this);
        youtube.setText("Open YouTube");

        Button whatsapp = new Button(this);
        whatsapp.setText("Open WhatsApp");

        Button chrome = new Button(this);
        chrome.setText("Open Chrome");

        layout.addView(title);
        layout.addView(status);
        layout.addView(listen);
        layout.addView(youtube);
        layout.addView(whatsapp);
        layout.addView(chrome);

        setContentView(layout);

        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
                speak("Namaste Boss! Main MJ hoon.");
            }
        });

        listen.setOnClickListener(v -> checkMicPermission());
        youtube.setOnClickListener(v -> openApp(
                "com.google.android.youtube",
                "https://www.youtube.com"
        ));
        whatsapp.setOnClickListener(v -> openApp(
                "com.whatsapp",
                "https://www.whatsapp.com"
        ));
        chrome.setOnClickListener(v -> openApp(
                "com.android.chrome",
                "https://www.google.com"
        ));
    }

    private void checkMicPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_CODE
            );
        } else {
            startListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        if (requestCode == MIC_CODE && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else if (requestCode == MIC_CODE) {
            status.setText("Boss, microphone permission chahiye.");
        }
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Boliye Boss");

        try {
            startActivityForResult(intent, SPEECH_CODE);
        } catch (Exception e) {
            status.setText("Speech recognition available nahi hai.");
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_CODE && resultCode == RESULT_OK
                && data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );

            if (results != null && !results.isEmpty()) {
                handleCommand(results.get(0).toLowerCase(Locale.ROOT));
            }
        }
    }

    private void handleCommand(String command) {
        status.setText("You said: " + command);

        if (command.contains("youtube")) {
            speak("Okay Boss, YouTube khol rahi hoon.");
            openApp("com.google.android.youtube",
                    "https://www.youtube.com");

        } else if (command.contains("whatsapp")) {
            speak("Okay Boss, WhatsApp khol rahi hoon.");
            openApp("com.whatsapp",
                    "https://www.whatsapp.com");

        } else if (command.contains("chrome")
                || command.contains("google")) {
            speak("Okay Boss, browser khol rahi hoon.");
            openApp("com.android.chrome",
                    "https://www.google.com");

        } else if (command.contains("hello")
                || command.contains("namaste")) {
            speak("Namaste Boss! Aap kaise hain?");

        } else {
            speak("Boss, abhi main ye command nahi samajh paayi.");
        }
    }

    private void openApp(String packageName, String fallbackUrl) {
        Intent launch = getPackageManager()
                .getLaunchIntentForPackage(packageName);

        try {
            if (launch != null) {
                startActivity(launch);
            } else {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(fallbackUrl)
                ));
            }
        } catch (Exception e) {
            status.setText("Boss, app open nahi ho paayi.");
        }
    }

    private void speak(String message) {
        status.setText(message);
        if (tts != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,
                    null, "MJ_REPLY");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
          }
