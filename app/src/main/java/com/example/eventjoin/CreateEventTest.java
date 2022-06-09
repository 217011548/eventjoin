package com.example.eventjoin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class CreateEventTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void onCreate() {
        FirebaseAuth mockFirebaseUser = null;
        FirebaseUser currentFirebaseUser= mockFirebaseUser.getCurrentUser();
        when(currentFirebaseUser.getUid()).thenReturn("456");
        String cf = currentFirebaseUser.getUid();
        assertEquals("currentFirebaseUser","456" ,cf);
        if (currentFirebaseUser.getUid() != null) {assertTrue(true);}
        
        
    }

    @org.junit.Test
    public void onOptionsItemSelected() {
    }

    @org.junit.Test
    public void onDateSet() {
    }

    @org.junit.Test
    public void onTimeSet() {
    }

    @org.junit.Test
    public void onActivityResult() {
    }
}