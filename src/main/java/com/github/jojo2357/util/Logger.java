package com.github.jojo2357.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static FileWriter fw;

    static {
        try {
            fw = new FileWriter("log.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doPrint(String out) {
        System.out.print(out + "                            \r");
        try {
            fw.write("STDOUT [" + System.currentTimeMillis() + "]: " + out + '\n');
        }catch (Exception e){}
    }

    public static void doWarning(String out) {
        System.err.println(out);
        try {
            fw.write("STDERR [" + System.currentTimeMillis() + "]: " + out + '\n');
        }catch (Exception e){}
    }

    public static void doError(String out) {
        System.err.println(out);
        try {
            fw.write("ERROR [" + System.currentTimeMillis() + "]: " + out + '\n');
        }catch (Exception e){}
    }

    public static void doError(StackTraceElement[] out) {
        try {
            fw.write("ERROR [" + System.currentTimeMillis() + "]: " + out + '\n');
        }catch (Exception e){}
    }

    public static void closeLog(){
        try {
            fw.close();
        }catch (Exception e) {}
    }
}
