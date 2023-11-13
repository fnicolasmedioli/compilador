package compiler;

import java.util.Vector;

public class ListOfTriplets {
    private final Vector<Triplet> tripletList;

    public ListOfTriplets() {
        this.tripletList = new Vector<Triplet>();
    }

    public int addTriplet(Triplet t) {
        this.tripletList.add(t);
        return this.tripletList.size() - 1;
    }

    public Triplet getTriplet(int index) {
        return this.tripletList.elementAt(index);
    }

    @Override
    public String toString()
    {
        String s = "";
        for (Triplet t : tripletList)
            s += t + "\n";

        return s;
    }

    public int getSize()
    {
        return tripletList.size();
    }
}