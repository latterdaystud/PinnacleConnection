package com.example.jonathanashcraft.pinnacleconnection;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by Jonathan Ashcraft on 3/2/2018.
 */
public class MessagingActivityTest {
    @Test
    public void testMessage() throws Exception {
        /*
        MessagingActivity mess1 = Mockito.mock(MessagingActivity.class);
        View view = new View(mess1.getApplicationContext());
        //view.setId(R.layout.content_messaging);
        EditText editText = mess1.getMessage();
        //editText.setText("Hello dude");
        mess1.setEditText("Hello dude");
        assertEquals(mess1.getMessage().getText().toString(), "Hello dude");
        String message = editText.getText().toString();
        mess1.onSend(view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mess1.getApplicationContext());
        assertEquals(message, prefs.getString("Message", "myStringToSave"));
        assertEquals(mess1.getAdapter().getItem(mess1.getAdapter().getPosition(message)), message);
        */
    }

}