package network.darkhelmet.prism.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class StorageConfiguration {
    @Comment("Set the datasource. This determines which storage system is used\n"
            + "Available options: mysql")
    private String datasource = "mysql";

    @Comment("Configure the database name.")
    private String database = "prism";

    @Comment("Configure the hostname.")
    private String host = "localhost";

    @Comment("Enter the password, if the selected datasource uses authentication")
    private String password = "";

    @Comment("Configure the port.")
    private String port = "3306";

    @Comment("Enter the prefix prism should use for database table names. i.e. prism_data")
    private String prefix = "prism_";

    @Comment("Enter the username, if the selected datasource uses authentication")
    private String username = "root";

    /**
     * Get the datasource setting.
     *
     * @return The datasource
     */
    public String datasource() {
        return datasource;
    }

    /**
     * Get the database name.
     *
     * @return The database name
     */
    public String database() {
        return database;
    }

    /**
     * Get the host.
     *
     * @return The host
     */
    public String host() {
        return host;
    }

    /**
     * Get the password.
     *
     * @return The password
     */
    public String password() {
        return password;
    }

    /**
     * Get the port.
     *
     * @return The port
     */
    public String port() {
        return port;
    }

    /**
     * Get the prefix.
     *
     * @return The prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * Get the username.
     *
     * @return The username
     */
    public String username() {
        return username;
    }
}
