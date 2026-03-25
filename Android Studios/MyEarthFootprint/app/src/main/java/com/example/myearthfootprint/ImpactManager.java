package com.example.myearthfootprint;

import java.util.ArrayList;
import java.util.List;

public class ImpactManager {
    private static final List<String> names   = new ArrayList<>();
    private static final List<Double> ghgs    = new ArrayList<>();
    private static final List<Double> waters  = new ArrayList<>();

    public static void addItem(String name, double ghg, double water) {
        names.add(name);
        ghgs.add(ghg);
        waters.add(water);
    }

    public static List<String> getNames() {
        return new ArrayList<>(names);
    }

    public static List<Double> getGhgs() {
        return new ArrayList<>(ghgs);
    }

    public static List<Double> getWaters() {
        return new ArrayList<>(waters);
    }

    public static double getTotalGhg() {
        double sum = 0;
        for (double v : ghgs) sum += v;
        return sum;
    }

    public static double getTotalWater() {
        double sum = 0;
        for (double v : waters) sum += v;
        return sum;
    }

    public static void clearItems() {
        names.clear();
        ghgs.clear();
        waters.clear();
    }
}
