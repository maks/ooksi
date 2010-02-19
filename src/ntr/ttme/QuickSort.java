package ntr.ttme;

import java.util.Vector;

public class QuickSort
{
    protected Vector integers;

    public QuickSort( Vector integers )
    {
        this.integers = integers;
    }

    public void run()
    {
        quicksort( 0, integers.size()-1 );
    }

    protected void quicksort( int start, int end )
    {
        if (start < end)
        {
            int pivot = partition( start, end );
            quicksort( start, pivot );
            quicksort( pivot+1, end );
        }
    }

    private int partition( int start, int end )
    {
        Integer pivot = (Integer)integers.elementAt( start );
        Integer temp;

        start--;
        end++;
        while(true)
        {
            do
            {
                end--;
            }
            while( !less( (Integer)integers.elementAt(end), pivot ) );
            
            do
            {
                start++;
            }
            while( !less( pivot,(Integer)integers.elementAt( start ) ) );
            
            if (start < end)
            {
                temp = (Integer)integers.elementAt( start );
                integers.setElementAt( integers.elementAt( end ), start );
                integers.setElementAt( temp, end );
            }
            else
            {
                break;
            }
        }

        return end;
    }

    private boolean less( Integer il, Integer ir )
    {
        return (il.intValue() <= ir.intValue());
    }

} // END QuickSort
