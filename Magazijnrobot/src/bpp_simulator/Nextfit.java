package bpp_simulator;

import java.util.ArrayList;

public class Nextfit extends Algoritme {

    public int start(ArrayList<Pakket> pk, int grootte) {
        int binCount = 0;
        int s = grootte;
        for (Pakket pakket : pk) {

            if (s - pakket.getLength() > 0) {
                s -= pakket.getLength();
                if (s - pakket.getLength() < 0) {
                    binCount++;
                    s = grootte;
                }
                continue;
            } else {
                binCount++;
                s = grootte;
            }
        }
        return binCount;
    }
}
