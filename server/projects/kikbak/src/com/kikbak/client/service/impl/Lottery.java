package com.kikbak.client.service.impl;

import java.util.ArrayList;

class Lottery {
    private static class Chance {
        public Chance(double probability, double value) {
            this.probability = probability;
            this.value = value;
        }

        double probability;
        double value;
    }

    private ArrayList<Lottery.Chance> mChances;

    /**
     * A lottery is defined by values and assigned to them probabilities. Probabilities MUST sum up to 1. <br>
     * Example: a definition string: "1 0.6 2 0.3 3 0.1" constructs lottery where chances to draw "1" are 60%, "2" are
     * 30% and "3" are 10%.<br>
     * Both probabilities and values are of type double.
     */
    public Lottery(String definition) {
        parse(definition);
        checkProbablitySum();
    }

    /**
     * Get a value from the pool according to defined probability
     */
    public double draw() {
        return getValue(Math.random());
    }

    private void parse(String definition) {
        String[] parts = definition.split(" ");
        if (parts.length % 2 != 0 || parts.length == 0)
            throw new IllegalArgumentException("Lottery definition wrong:" + definition);

        mChances = new ArrayList<Lottery.Chance>(parts.length / 2);

        for (int i = 0; i < parts.length; i += 2) {
            double v = Double.valueOf(parts[i]);
            double p = Double.valueOf(parts[i + 1]);
            mChances.add(new Chance(p, v));
        }
    }

    private double getValue(double p) {
        Chance last = mChances.get(0);
        double sum = last.probability;
        for (int i = 1; i < mChances.size(); i++) {
            if (sum > p)
                return last.value;
            Chance c = mChances.get(i);
            sum += c.probability;
            last = c;
        }
        return last.value;
    }

    private void checkProbablitySum() {
        double sum = 0.0;
        for (Chance e : mChances) {
            sum += e.probability;
        }
        if (sum != 1.0)
            throw new IllegalArgumentException("Lottery probability does not sum-up:" + sum);

    }
}
