package com.daexsys.grappl;

public class GrapplServerState {
    public static long timeStarted = System.currentTimeMillis();

    public static boolean testingMode = false;

    public static void setup() {
        timeStarted = System.currentTimeMillis();
    }
}