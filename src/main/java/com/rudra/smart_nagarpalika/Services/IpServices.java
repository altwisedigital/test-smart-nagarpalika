package com.rudra.smart_nagarpalika.Services;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpServices {

    public static String getCurrentIP() { // Change return type to String

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress(); // Return the IP address as a String
        } catch (UnknownHostException e) {
            System.err.println("Could not determine local host IP address: " + e.getMessage());
            return null; // Return null or throw an exception to indicate an error
        }
    }
}