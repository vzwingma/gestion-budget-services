package android.finances.terrier.com.budget.utils;

import android.util.Log;

/**
 * Classe de log dans l'application
 *
 * @author vzwingma
 */
public class Logger {

    /**
     * Classe associée au log
     */
    private final Class<?> classe;

    /**
     * Constructeur
     *
     * @param clazz classe du logger
     */
    public Logger(Class<?> clazz) {
        this.classe = clazz;
    }

    /**
     * Message de niveau Trace
     *
     * @param message message à tracer
     */
    public void trace(String message) {
        Log.v(classe.getName(), message);
    }

    /**
     * Message de niveau Debug
     *
     * @param message message à tracer
     */
    public void debug(String message) {
        Log.d(classe.getName(), message);
    }

    /**
     * Message de niveau Info
     *
     * @param message message à tracer
     */
    public void info(String message) {
        Log.i(classe.getName(), message);
    }

    /**
     * Message de niveau Error
     *
     * @param message message à tracer
     */
    public void error(String message) {
        Log.e(classe.getName(), message);
    }

    /**
     * Message de niveau Error
     *
     * @param message message à tracer
     */
    public void error(String message, Exception e) {
        Log.e(classe.getName(), message + " || " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Message de niveau Warn
     *
     * @param message message à tracer
     */
    public void warn(String message) {
        Log.w(classe.getName(), message);
    }
}
