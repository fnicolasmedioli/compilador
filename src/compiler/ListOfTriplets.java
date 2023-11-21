package compiler;

import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;

public class ListOfTriplets implements Iterable<Triplet> {
    private final Vector<Triplet> tripletList;
    private final HashMap<Integer, LinkedList<String>> tags;

    private int incrementalCounter = 0;

    public ListOfTriplets() {
        this.tripletList = new Vector<Triplet>();
        tags = new HashMap<>();
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

    @Override
    public Iterator<Triplet> iterator() {
        return new Iterator<Triplet>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < tripletList.size();
            }

            @Override
            public Triplet next() {
                return tripletList.get(currentIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void addTag(int index, String tag)
    {
        if (!tags.containsKey(index))
            tags.put(index, new LinkedList<>());
        tags.get(index).add(tag);
    }

    public LinkedList<String> getTags(int index)
    {
        return ((tags.containsKey(index)) ? tags.get(index) : new LinkedList<>());
    }

    public void printTags()
    {
        System.out.println(tags);
    }

    public String getNewIfTag()
    {
        return String.format("@@@if_end_%d", incrementalCounter++);
    }

    public String getIncrementalNum()
    {
        return String.format("%d", incrementalCounter++);
    }
}