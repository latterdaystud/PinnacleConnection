package com.example.jonathanashcraft.pinnacleconnection;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Joseph on 3/2/2018.
 */

public class UserClassTest {

    @Test
    public void isManagerTest() {
        User user = new User("jridgley", "girlygirl123", 1);

        assertFalse(user.isManager);

        user.validateManager("555");

        assertTrue(user.isManager());
    }
}
