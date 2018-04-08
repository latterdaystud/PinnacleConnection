package com.example.jonathanashcraft.pinnacleconnection;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Jonathan Ashcraft on 4/7/2018.
 */

public class MessageTest {
    @Test
    public void testMessageClass() {
        // All of these should different.
        Message testMess = new Message();
        Message testMess2 = new Message("You", "Me", "Body", "Time");
        Message testMess3 = new Message();
        testMess3.setTo("You");
        testMess3.setFrom("Me");
        testMess3.setBody("Body");
        testMess3.setTime("Time");
        Message testMess4 = testMess3;

        assertNotEquals(testMess, testMess2);
        assertEquals(testMess3, testMess4);

        String body = testMess2.getBody();
        assertEquals(body, "Body");

        assertEquals(testMess2.getBody(), testMess3.getBody());
        assertEquals(testMess2.getFrom(), testMess3.getFrom());
        assertEquals(testMess2.getTime(), testMess3.getTime());
        assertEquals(testMess2.getTo(), testMess3.getTo());
    }
}
