package com.example.canscan;

import java.util.HashMap;

public class ZipcodeLab {

    private static ZipcodeLab sZupcodeLab;

    private HashMap<Integer, String> mZipcodePickUpDays;

    public static ZipcodeLab get() {
        if (sZupcodeLab == null) {
            sZupcodeLab = new ZipcodeLab();
        }
        return sZupcodeLab;
    }

    private ZipcodeLab() {
        fillZipcodePickUpDaysMap();
    }

    private void fillZipcodePickUpDaysMap() {
        mZipcodePickUpDays = new HashMap<>();

        mZipcodePickUpDays.put(14201, "Wednesday");
        mZipcodePickUpDays.put(14202, "Wednesday");
        mZipcodePickUpDays.put(14203, "Wednesday");
        mZipcodePickUpDays.put(14204, "Monday");
        mZipcodePickUpDays.put(14206, "Monday");
        mZipcodePickUpDays.put(14207, "Friday");
        mZipcodePickUpDays.put(14208, "Tuesday");
        mZipcodePickUpDays.put(14209, "Wednesday");
        mZipcodePickUpDays.put(14210, "Monday");
        mZipcodePickUpDays.put(14211, "Tuesday");
        mZipcodePickUpDays.put(14212, "Tuesday");
        mZipcodePickUpDays.put(14213, "Wednesday");
        mZipcodePickUpDays.put(14214, "Thursday");
        mZipcodePickUpDays.put(14215, "Thursday");
        mZipcodePickUpDays.put(14216, "Friday");
        mZipcodePickUpDays.put(14220, "Monday");
        mZipcodePickUpDays.put(14222, "Wednesday");
    }

    public HashMap<Integer, String> getZipcodePickUpDaysMap() {
        return mZipcodePickUpDays;
    }
}
