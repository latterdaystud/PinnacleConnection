package com.example.jonathanashcraft.pinnacleconnection;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void trueStatement() throws Exception {
        assertEquals(16, 8 * 2);
    }

    @Test
    public void testMessage() throws Exception {
        MessagingActivity mess1 = new MessagingActivity();
        View view;
        view.setContentView(R.layout.activity_messaging);
        EditText editText = view.findViewById(R.id.editText);
        editText.setText("Hello dude");
        assertEquals(editText.getText().toString(), "Hello dude");
        String message = editText.getText().toString();
        mess1.onSend(view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mess1.getApplicationContext());
        assertEquals(message, prefs.getString("Message", "myStringToSave"));
        assertEquals(mess1.getAdapater().getItem(mess1.getAdapater().getPosition(message)), message);

    }
}