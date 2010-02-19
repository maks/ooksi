package ntr.ttme;

public abstract class VectorGraphicsAdapter
    extends MatrixTransformationAdapter
    implements Drawable
{
    protected VectorGraphicsAdapter()
    {
        super();
    }

    // Drawable interface (partially implemented)

    public void drawToBackBuffer( View view )
    {
        draw( view, view.getBackBuffer() );
    }

    public void drawToFrontBuffer( View view )
    {
        draw( view, view.getFrontBuffer() );
    }

    public void draw( View view, int buffer )
    {
        draw( view, (buffer==BUFFER_BACK ? view.getBackBuffer() : view.getFrontBuffer()) );
    }

} // END VectorGraphicsAdapter
