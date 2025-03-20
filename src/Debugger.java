public class Debugger {
    public static boolean debug = false;
    public static synchronized void debug (String s)
    {
        if (debug)
        {
            String threadName = Thread.currentThread().getName();
            System.err.println (threadName + ": " + s);
        }
    }
    public static synchronized void debug (Exception e)
    {
        if (debug)
        {
            String threadName = Thread.currentThread().getName();
            System.err.println (threadName + ": " + e);
        }
    }
}
