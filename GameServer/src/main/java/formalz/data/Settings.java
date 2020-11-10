/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.*;

import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that keeps track of all the settings.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);

    private String haskellTargetURL;
    private int haskellPort;
    private String haskellMethod;
    private int haskellTimeout;

    private String databaseDriver;
    private String databaseUsername;
    private String databasePassword;
    private String databaseURLPrefix;
    private String databaseHostURL;
    private String databaseName;
    private boolean databaseUseSSL;

    private String databaseURL;

    private int databaseMinPoolSize;

    private int databaseMaxPoolSize;

    private int connectionPort;

    private String serverStoreType;
    private String serverKeyStore;
    private String serverStorePassword;
    private String serverKeyPassword;

    private long maxSessionCreatedDifference;

    private int sessionWaitTime;

    private boolean tlsEnabled;

    private boolean analyticsEnabled;

    private String analyticsServerHost;

    private int analyticsServerPort;

    private boolean analyticsServerSecureConnection;

    private int connectionLostTimeout = 40;

    private List<String> trustedProxyIps = Collections.emptyList();

    private int workerThreadNumber = 2;

    private static String settingsFileName = "settings.json";

    private static Settings settings = initializeSettings();

    /**
     * Set the maximum amount of time between the creation and authentication of a
     * session. For testing purposes.
     * 
     * @param time Time in milliseconds allowed between creation and authentication
     *             of session.
     */
    public static void setMaxSessionCreatedDifference(long time) {
        settings.maxSessionCreatedDifference = time;
    }

    /**
     * Initializes the settings constant.
     * 
     * @return The initial Settings object.
     */
    private static Settings initializeSettings() {
        String fileData;
        try {
            Scanner scan = new Scanner(new File(settingsFileName));
            fileData = scan.useDelimiter("\\Z").next(); // Delimit to end of file
            scan.close();
        } catch (FileNotFoundException e) {
            LOGGER.warn(String.format("Couldn't load settings file '%s', using defaults.", settingsFileName), e);
            fileData = "{}";
        }
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(fileData, Settings.class);
    }

    /**
     * Set a new settings file.
     * 
     * @param fileName File name of the new settings file.
     */
    public static void setSettingsFile(String fileName) {
        Settings.settingsFileName = fileName;
        Settings.settings = initializeSettings();
    }

    /**
     * Gets the URL of the Haskell back-end.
     * 
     * @return The URL of the Haskell back-end.
     */
    public static String getHaskellTargetURL() {
        return settings.haskellTargetURL;
    }

    public static void setHaskellTargetURL(String haskellTargetURL) {
        settings.haskellTargetURL = haskellTargetURL;
    }

    /**
     * Gets the port of the Haskell back-end.
     * 
     * @return The port of the Haskell back-end.
     */
    public static int getHaskellPort() {
        return settings.haskellPort;
    }

    public static void setHaskellPort(int haskellPort) {
        settings.haskellPort = haskellPort;
    }

    /**
     * Gets the method to use on the Haskell back-end.
     * 
     * @return The method to use on the Haskell back-end.
     */
    public static String getHaskellMethod() {
        return settings.haskellMethod;
    }

    /**
     * Gets the timeout on connection with the Haskell back-end in milliseconds.
     * 
     * @return The timeout on connection with the Haskell back-end in milliseconds.
     */
    public static int getHaskellTimeout() {
        return settings.haskellTimeout;
    }

    /**
     * Gets the class name of the Driver class of the database.
     * 
     * @return The class name of the Driver class of the database.
     */
    public static String getDatabaseDriver() {
        return settings.databaseDriver;
    }

    /**
     * Gets the username in the database.
     * 
     * @return The username in the database.
     */
    public static String getDatabaseUsername() {
        return settings.databaseUsername;
    }

    public static void setDatabaseUsername(String databaseUsername) {
        settings.databaseUsername=databaseUsername;
    }

    /**
     * Gets the password in the database.
     * 
     * @return The password in the database.
     */
    public static String getDatabasePassword() {
        return settings.databasePassword;
    }

    public static void setDatabasePassword(String databasePassword) {
        settings.databasePassword = databasePassword;
    }

    /**
     * Gets the connection URL to the database.
     * 
     * @return The connection URL to the database.
     */
    public static String getDatabaseURL() {
        String result = settings.databaseURL;
        if (result == null || result.isEmpty()) {
            result = Settings.getDatabaseURLPrefix() + Settings.getDatabaseHostURL() + "/" + Settings.getDatabaseName()  + "?useSSL=" + Settings.getDatabaseUseSSL();
        }
        return result;
    }

    public static void setDatabaseURL(String databaseURL) {
        settings.databaseURL = databaseURL;
    }

    public static int getDatabaseMinPoolSize() {
        return settings.databaseMinPoolSize;
    }

    public static void setDatabaseMinPoolSize(int databaseMinPoolSize) {
        settings.databaseMinPoolSize = databaseMinPoolSize;
    }

    public static int getDatabaseMaxPoolSize() {
        return settings.databaseMaxPoolSize;
    }

    public static void setDatabaseMaxPoolSize(int databaseMaxPoolSize) {
        settings.databaseMaxPoolSize = databaseMaxPoolSize;
    }

    /**
     * Gets the prefix of the URL of the database.
     * 
     * @return The prefix of the URL of the database.
     */
    public static String getDatabaseURLPrefix() {
        return settings.databaseURLPrefix;
    }

    /**
     * Gets the URL of the database.
     * 
     * @return The URL of the database.
     */
    public static String getDatabaseHostURL() {
        return settings.databaseHostURL;
    }

    /**
     * Gets the name of the database.
     * 
     * @return The name of the database.
     */
    public static String getDatabaseName() {
        return settings.databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        settings.databaseName = databaseName;
    }

    /**
     * Gets a boolean value determining if we use SSL for the database.
     * 
     * @return A boolean value determining if we use SSL for the database.
     */
    public static boolean getDatabaseUseSSL() {
        return settings.databaseUseSSL;
    }

    /**
     * Gets the port on which to run the server.
     * 
     * @return The port on which to run the server.
     */
    public static int getConnectionPort() {
        return settings.connectionPort;
    }

    public static boolean isTLSEnabled() {
        return settings.tlsEnabled;
    }

    /**
     * Gets the type of the keystore for the server.
     * 
     * @return Store type.
     */
    public static String getServerStoreType() {
        return settings.serverStoreType;
    }

    /**
     * Gets the filename of the keystore.
     * 
     * @return Filename of the keystore.
     */
    public static String getServerKeyStore() {
        return settings.serverKeyStore;
    }

    /**
     * Gets the password of the keystore.
     * 
     * @return Password of the keystore.
     */
    public static String getServerStorePassword() {
        return settings.serverStorePassword;
    }

    /**
     * Gets the password of the key.
     * 
     * @return Password of the key.
     */
    public static String getServerKeyPassword() {
        return settings.serverKeyPassword;
    }

    /**
     * Gets the maximal difference between current time and creation of token to
     * accept.
     * 
     * @return The maximal time difference to accept sessions startup.
     */
    public static long getMaxSessionCreatedDifference() {
        return settings.maxSessionCreatedDifference;
    }

    /**
     * Gets the time to wait for a user to authenticate himself.
     * 
     * @return The time to wait till timeout.
     */
    public static int getSessionWaitTime() {
        return settings.sessionWaitTime;
    }

    public static boolean isAnalyticsEnabled() {
        return settings.analyticsEnabled;
    }

    public static String getAnalyticsServerHost() {
        return settings.analyticsServerHost;
    }

    public static int getAnalyticsServerPort() {
        return settings.analyticsServerPort;
    }

    public static boolean isAnalyticsServerSecureConnection() {
        return settings.analyticsServerSecureConnection;
    }

    public static int getConnectionLostTimeout() {
        return settings.connectionLostTimeout;
    }

    public static Collection<SubnetUtils.SubnetInfo> getTrustedProxyCidrs() {
        return parseTrustedProxiesCidrs(settings.trustedProxyIps);
    }

    private static List<SubnetUtils.SubnetInfo> parseTrustedProxiesCidrs(Collection<String> proxyEntries) {
        List<SubnetUtils.SubnetInfo> cidrs = new LinkedList<SubnetUtils.SubnetInfo>();

        proxyEntries.forEach(proxyEntry -> {
            // conver simple ip to ip range
            if (proxyEntry.indexOf("/") == -1) {
                proxyEntry += "/32";
            }
            try {
                SubnetUtils subnet = new SubnetUtils(proxyEntry);
                cidrs.add(subnet.getInfo());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid CIDR syntax : " + proxyEntry);
                throw e;
            }
        });
        return cidrs;
    }

    public static int getWorkerThreadNumber() {
        return settings.workerThreadNumber;
    }

    public static void setWorkerThreadNumber(int workerThreadNumber) {
        settings.workerThreadNumber = workerThreadNumber;
    }
}
