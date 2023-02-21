package com.alfredobejarano.texttospeechdemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
* Constant that represent an arbitrary value for a TTS data download request.
**/
const val TTS_ENGINE_RESULT_CODE = 26

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * As not all devices provide all the TTS data (languages or engines),
         * is recommended to check if this data is available before using the engine.
         */
        val checkTTSEngineIntent = Intent() // Create an Intent
        checkTTSEngineIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA // Set the intent action to check the TTS engine data.
        startActivityForResult(checkTTSEngineIntent, TTS_ENGINE_RESULT_CODE) // Start the Intent expecting a result, sending a custom status code.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TTS_ENGINE_RESULT_CODE) { // Check if the result code is the same as the one sent by the TTS check intent.
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) { // If the device has all the necessary TTS data.
                textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
                    // Initialize the TTS engine.
                    textToSpeech?.language = Locale.getDefault() // Set the language to the device's locale.
                })
                button.setOnClickListener {
                    /**
                     * The speak() method signature changed in API 21.
                     */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech?.speak(edit_text.text
                                ?: edit_text.hint, TextToSpeech.QUEUE_FLUSH, null, null) // Make the device read the text in the EditText.
                    } else {
                        textToSpeech?.speak((edit_text.text
                                ?: edit_text.hint) as String, TextToSpeech.QUEUE_FLUSH, null) // Make the device read the text in the EditText.
                    }
                }
            } else { // If the data is not available.
                val installTTSDataIntent = Intent() // Create a new intent.
                installTTSDataIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA // Define the intent action as one for installing this missing data, the TTS API provides this.
                startActivity(installTTSDataIntent) // Start the intent for downloading the missing data.
            }
        }
    }
}
