package ntr.ttme;

import java.io.InputStream;
import java.util.Hashtable;

public class TrueTypeRegistry
{
    public static TrueTypeRegistry instance = new TrueTypeRegistry();

    private Hashtable registered;

    private TrueTypeRegistry()
    {
        registered = new Hashtable();
    }

    public TrueTypeDefinition registerTrueType( String token,
                                                InputStream inputStream )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        TrueTypeDefinition ttf =
            new TrueTypeDefinition( inputStream, true );

        registered.put( token, ttf );

        return ttf;
    }

    public TrueTypeDefinition registerTrueType( String token,
                                                InputStream inputStream,
                                                short platformID,
                                                short specificID,
                                                short languageID )
        throws TrueTypeBusinessException, TrueTypeTechnicalException
    {
        TrueTypeDefinition ttf =
            new TrueTypeDefinition( inputStream, platformID, specificID, languageID );

        registered.put( token, ttf );

        return ttf;
    }

    public void registerTrueType( String token,
                                  TrueTypeDefinition ttf )
    {
        registered.put( token, ttf );
    }

    public TrueTypeDefinition get( String token )
    {
        TrueTypeDefinition ttf =
            (TrueTypeDefinition)registered.get( token );

        return ttf;
    }

    public void deregisterTrueType( String token )
    {
        registered.remove( token );
    }

    public void deregisterAll()
    {
        registered.clear();
    }

} // END TrueTypeRegistry
