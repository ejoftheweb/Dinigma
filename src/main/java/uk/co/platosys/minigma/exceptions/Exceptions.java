package uk.co.platosys.minigma.exceptions;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Exceptions {
    private static Logger log = LogManager.getRootLogger();
    private static Marker mark = MarkerManager.getMarker("Digester");

    public static void dump (Throwable e) {
        log.error(mark, "error", e);
        System.out.println(e.getClass().getName() + ":" + e.getMessage());
        if (e.getCause() != null) {
            dump(e.getCause());
        } else {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                System.out.println(stackTraceElement.toString());
            }
        }
    }
}