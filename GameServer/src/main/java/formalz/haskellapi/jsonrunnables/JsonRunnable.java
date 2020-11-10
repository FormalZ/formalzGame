/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import java.util.Map.Entry;

import com.google.gson.*;

import formalz.haskellapi.Response;

/**
 * An interface with which to run through an entry of a JsonObject.
 * @author Ludiscite
 * @version 1.0
 */
public interface JsonRunnable
{
    /**
     * Runs through an entry of a JsonObject.
     * @param e The entry of the JsonObject through which to run.
     */
    public void run(Entry<String, JsonElement> e);

    /**
     * Runs through all entries of a JsonObject with a JsonRunnable.
     * @param o The JsonObject through which to run.
     * @param runnable The JsonRunnable with which to run through the entries of the JsonObject.
     */
    public static void iterateJsonObject(JsonObject o, JsonRunnable runnable)
    {
        iterateEntries(o.entrySet(), runnable);
    }

    /**
     * Runs through all entries of a Response object with a JsonRunnable.
     * @param r The Response object through which to run.
     * @param runnable The JsonRunnable with which to run through the entries of the Response object.
     */
    public static void iterateResponse(Response r, JsonRunnable runnable)
    {
        iterateEntries(r.getModel(), runnable);
    }

    /**
     * Runs through a collection of entries with a JsonRunnable.
     * @param entries The entries through which to run.
     * @param runnable The JsonRunnable with which to run through the collection of entries.
     */
    public static void iterateEntries(Iterable<Entry<String, JsonElement>> entries, JsonRunnable runnable)
    {
        if (entries == null)
            return;
        for (Entry<String, JsonElement> e : entries)
        {
            runnable.run(e);
        }
    }
}
