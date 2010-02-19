package ntr.tt.example.midlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import ntr.ttme.DoubleRectangle;
import ntr.ttme.TrueTypeException;
import ntr.ttme.TrueTypeRegistry;
import ntr.ttme.TrueTypeText;
import ntr.ttme.View;

public class TestMidlet extends MIDlet implements CommandListener
{
    static final Integer COLOR_RED = new Integer( 0x00ff0000 );
    static final Integer COLOR_GREEN = new Integer( 0x0000ff00 );
    static final Integer COLOR_BLUE = new Integer( 0x000000ff );
    static final Integer COLOR_YELLOW = new Integer( 0x00f1d471 );
    static final Integer COLOR_GRAY = new Integer( 0x00777777 );
    
    public static int WIDTH;
    public static int HEIGHT;

    public static final int ANIMATION_DELAY = 40;

    private TrueTypeText text1;
    private TrueTypeText text2;

    private Image hiddenImage;
    private View view;

    private TimerThread timer;

    private long counter;

    private Command cmd_exit;
    private Display display;

    private DoubleRectangle b1;
    private DoubleRectangle b2;

    private boolean useFilling;

    public TestMidlet()
    {
        this( true );
    }

    public TestMidlet( boolean useFilling )
    {
        this.useFilling = useFilling;
    }

    protected void startApp() throws MIDletStateChangeException
    {
        final Display display = Display.getDisplay( this );

        final MyCanvas myCanvas = new MyCanvas();
        myCanvas.setCommandListener(this);

        cmd_exit = new Command( "Exit", Command.EXIT, 0 );
        myCanvas.addCommand( cmd_exit );

        display.setCurrent( myCanvas );

        Thread thread = new Thread()
        {
            public void run()
            {
                super.run();

                InputStream inputStream = null;

                try
                {
                    //inputStream = getClass().getResourceAsStream( "Pothana.ttf" );
                	inputStream = getClass().getResourceAsStream( "SCRIPTIN.ttf" );

                    TrueTypeRegistry.instance.registerTrueType( "FONT1", inputStream );

                    //inputStream = getClass().getResourceAsStream( "Tikkana.ttf" );
                    inputStream = getClass().getResourceAsStream( "UNCLEBOBMF.ttf" );

                    TrueTypeRegistry.instance.registerTrueType( "FONT2", inputStream );

                    text1 = new TrueTypeText( "FONT1",
                                              -0.3,
                                              0,
                                              //getUTF16FileData( "telugu1.txt" ),
                                              "True",
                                              24,
                                              COLOR_BLUE,
                                              (useFilling ? COLOR_YELLOW : null) );
                    //text1.translate( 0.07d, 0 );
                    //text1.rotate(-7d*Math.PI/30d, 0, 0 );

                    text2 = new TrueTypeText( "FONT2",
                                              0.4,
                                              0,
                                              //getTeluguUTF16FileDataAndTransposeForTikkanaFont( "telugu1.txt" ),
                                              "TYPE",
                                              24,
                                              COLOR_RED,
                                              (useFilling ? COLOR_GRAY : null) );
                    //text2.translate( 0.07d, 0 );
                }
                catch( TrueTypeException e )
                {
                    System.out.println( "Unable to load font!" );
                    e.printStackTrace();
                }
                catch( Throwable t )
                {
                    System.out.println( "Error!" );
                    t.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                        {
                            inputStream.close();
                        }
                    }
                    catch( Throwable ignore ) {}
                }

                // initialize graphics
                int attemptsLeft = 10;
                Displayable displayCurrent = null;
                while ((attemptsLeft-- > 0) && (displayCurrent = display.getCurrent()) == null)
                {
                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch ( InterruptedException ignore )
                    {
                    }
                }
                if (displayCurrent == null)
                {
                    throw new RuntimeException( "Display.getCurrent() returns null!" );
                }
                WIDTH = displayCurrent.getWidth();
                HEIGHT = displayCurrent.getHeight();

                view = new View( WIDTH, HEIGHT );

                timer = new TimerThread( ANIMATION_DELAY, myCanvas );
                timer.start();
            }
        };

        thread.start();
    }

    public String getTeluguUTF16FileDataAndTransposeForTikkanaFont( String resourceFileName )
    {
        InputStream is = null;
        InputStreamReader isr = null;

        try
        {
            is = getClass().getResourceAsStream( "telugu1.txt" );
            isr = new InputStreamReader( is, "UTF16" );

            StringBuffer strbuf = new StringBuffer();

            int ch;
            while ( ( ch = isr.read() ) > -1 )
            {
                char transposedAsTikkanaFontChar;
                if (ch >= 0x0c02 && ch <= 0xc0c)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc02 - 0x3d));
                }
                else if (ch >= 0x0c0e && ch <= 0xc10)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc0e - 0x48));
                }
                else if (ch >= 0x0c12 && ch <= 0xc28)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc12 - 0x4b));
                }
                else if (ch >= 0x0c2a && ch <= 0xc2f)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc2a - 0x65));
                }
                else if (ch == 0xc32)
                {
                    transposedAsTikkanaFontChar = (char)0x6e;
                }
                else if (ch >= 0x0c35 && ch <= 0xc37)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc35 - 0x70));
                }
                else if (ch >= 0x0c38 && ch <= 0xc39)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc38 - 0x75));
                }
                else if (ch >= 0x0c3e && ch <= 0xc3f)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc3e - 0x7d));
                }
                else if (ch >= 0x0c41 && ch <= 0xc42)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc41 - 0xa8));
                }
                else if (ch >= 0x0c46 && ch <= 0xc47)
                {
                    transposedAsTikkanaFontChar = (char)(ch - (0xc46 - 0xb3));
                }
                else
                {
                    // no transposition defined for this character, please use CharacterMapper and the Telugu Unicode table to add more translations
                    transposedAsTikkanaFontChar = (char)0x003f;
                    System.out.println( "TRANSLATE: "+ch );
                }
                strbuf.append( transposedAsTikkanaFontChar );

            }

            String fileData = strbuf.toString();
            System.out.println( "file data ====" + fileData );

            return fileData;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( "Couldn't load the file" );
        }
        finally
        {
            if ( isr != null )
            {
                try
                {
                    isr.close();
                }
                catch ( IOException ignore )
                {
                }
            }
            if ( is != null )
            {
                try
                {
                    is.close();
                }
                catch ( IOException ignore )
                {
                }
            }
        }
    }

    public String getUTF16FileData( String resourceFileName )
    {
        InputStream is = null;
        InputStreamReader isr = null;

        try
        {
            is = getClass().getResourceAsStream( resourceFileName );
            isr = new InputStreamReader( is, "UTF16" );

            StringBuffer strbuf = new StringBuffer();

            int ch;
            while ( ( ch = isr.read() ) > -1 )
            {
                strbuf.append( (char)ch );

            }

            String fileData = strbuf.toString();
            System.out.println( "file data ====" + fileData );

            return fileData;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( "Couldn't load the file "+resourceFileName );
        }
        finally
        {
            if ( isr != null )
            {
                try
                {
                    isr.close();
                }
                catch ( IOException ignore )
                {
                }
            }
            if ( is != null )
            {
                try
                {
                    is.close();
                }
                catch ( IOException ignore )
                {
                }
            }
        }
    }
    
    protected void pauseApp()
    {
        text1 = null;
        text2 = null;

        TrueTypeRegistry.instance.deregisterAll();

        hiddenImage = null;
        view = null;
    }

    protected void destroyApp( boolean arg0 ) throws MIDletStateChangeException
    {
        timer.requestStop();

        try
        {
            System.out.println( "Stop requested" );
            timer.join();
            System.out.println( "Timer joined" );
        }
        catch( InterruptedException ignore )
        {}
        notifyDestroyed();
    }

    public void commandAction( Command c, Displayable d )
    {
        if ( c == cmd_exit )
        {
            try
            {
                destroyApp( true );
            }
            catch ( MIDletStateChangeException e )
            {
                showException( e );
            }
        }
    }

    public void showException( Exception e )
    {
        Alert alert = new Alert( "Error" );
        alert.setString( e.getMessage() );
        alert.setType( AlertType.ERROR );
        alert.setTimeout( Alert.FOREVER );

        display.setCurrent( alert );
    }

    private class MyCanvas extends Canvas implements TimerDriven
    {
        public MyCanvas()
        {
            super();
        }

        public void paint( Graphics g )
        {
            update( g );
        }

        public void update( Graphics g )
        {
            if (view == null)
            {
                return;
            }

            if (! view.hasBuffers())
            {
                hiddenImage = Image.createImage( WIDTH, HEIGHT );

                view.setBuffers( g, hiddenImage.getGraphics() );

                zoom();
            }

            synchronized( view )
            {
                view.clearBackBuffer();

                text1.drawToBackBuffer( view );
                text2.drawToBackBuffer( view );
            }

            g.drawImage( hiddenImage, 0, 0, Graphics.TOP|Graphics.LEFT );
        }

        // TimerDriven interface

        public void timerTicked()
        {
            if (hiddenImage != null) // painted or updated already
            {
                synchronized( view )
                {
                    b1 = text1.getBounds();
                    if (b1 != null)
                    {
                        text1.rotate( -Math.PI/30,
                                      0,
                                      0 );
                    }

                    b2 = text2.getBounds();
                    if (b2 != null)
                    {
                        text2.rotate( Math.PI/30,
                                      0,
                                      0 );
                    }

                    counter+=20;
                    zoom();
                }
            }
            repaint();
            //System.out.println( "Tick!" );
        }

        private void zoom()
        {
            double middle = (View.MAX_ZOOM_IN-View.MAX_ZOOM_OUT)/2.0d;

            double zoomFactor = View.MAX_ZOOM_OUT +
                                middle +
                                Math.cos( (double)counter*(Math.PI/180.0d) )*middle;

            zoomFactor = (zoomFactor < View.MAX_ZOOM_OUT ? View.MAX_ZOOM_OUT : zoomFactor );
            zoomFactor = (zoomFactor > View.MAX_ZOOM_IN ? View.MAX_ZOOM_IN : zoomFactor );

            view.setZoomFactor( zoomFactor );
            view.centerViewPoint();
        }

    }

} // END MyCanvas

interface TimerDriven
{
    public void timerTicked();

} // END TimerDriven

class TimerThread extends Thread
{
    private long tickDuration;
    private TimerDriven timerDriven;

    private volatile boolean stopRequested;

    public TimerThread( long tickDuration, TimerDriven timerDriven )
    {
        this.tickDuration = tickDuration;
        this.timerDriven = timerDriven;
    }

    public void run()
    {
        while (! stopRequested)
        {
            timerDriven.timerTicked();

            try
            {
                Thread.sleep( tickDuration );
            }
            catch( InterruptedException ignore )
            {}
        }
    }

    public void requestStop()
    {
        this.stopRequested = true;
    }

} // END TimerThread
