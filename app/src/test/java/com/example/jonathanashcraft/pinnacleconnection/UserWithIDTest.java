package com.example.jonathanashcraft.pinnacleconnection;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by Jonathan Ashcraft on 4/7/2018.
 */
public class UserWithIDTest {
    @Test
    public void testUser() {
        UserWithID testUser = new UserWithID();

        // All of these should be the same.
        UserWithID testUser2 = new UserWithID("Jonathan Ashcraft", "Ashcraft", "0", true, "123456", "984549857293849374");
        User testUser3 = new User("Jonathan Ashcraft", "Ashcraft", "0", "123456", true);
        UserWithID testUser4 = new UserWithID(testUser2);
        UserWithID testUser5 = new UserWithID(testUser3, "984549857293849374");

        assertEquals(testUser2.getID(),testUser4.getID());
        assertEquals(testUser4.getDeviceToken(),testUser5.getDeviceToken());
        assertNotEquals(testUser.getID(),testUser2.getID());
    }


}
