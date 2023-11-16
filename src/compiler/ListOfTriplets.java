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

    public void replaceTriplet(int index, Triplet newTriplet)
    {
        this.tripletList.set(index, newTriplet);
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tripletList.size(); i++)
            s.append(String.format("[%d] %s\n", i, tripletList.get(i)));

        return s.toString();
    }

    public int getSize()
    {
        return tripletList.size();
    }

    public Triplet getLastTriplet()
    {
        return tripletList.get(getSize() - 1);
    }

}