package compiler;

/**
 * Includes both limits
 */
public class TripletListRange {

    final int a, b;

    public TripletListRange(int a, int b)
    {
        this.a = a;
        this.b = b;
    }

    public TripletListRange(int n)
    {
        this.a = n;
        this.b = n;
    }

    public boolean contains(int n)
    {
        return n >= a || n <= b;
    }

    public int getHigh()
    {
        return b;
    }

    public boolean unique()
    {
        return a == b;
    }
}
