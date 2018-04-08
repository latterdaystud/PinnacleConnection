package com.example.jonathanashcraft.pinnacleconnection;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Jonathan Ashcraft on 4/7/2018.
 */

public class AnnouncementTest {
    @Test
    public void testAnnouncementClass() {
        // All of these should different.
        Announcement testAnn = new Announcement();
        Announcement testAnn2 = new Announcement("Title", "Body", "Date", "Time", "Author", "Path to Image");
        Announcement testAnn3 = new Announcement("", "", "", "Time", "", "Path to Image");

        assertNotEquals(testAnn2, testAnn3);
        assertNotEquals(testAnn,testAnn2);

        String title = testAnn2.getTitle();
        assertEquals(title, "Title");
    }
}
