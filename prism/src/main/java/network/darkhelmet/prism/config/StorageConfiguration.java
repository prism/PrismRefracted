package network.darkhelmet.prism.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class StorageConfiguration {
    @Comment("Set the datasource. This determines which storage system is used\n" +
            "Available options: mysql")
    private String datasource = "mysql";

    @Comment("Configure the jdbc url, if applicable to the selected datasource.")
    private String jdbcUrl = "jdbc:mysql://localhost:3306/prism?useUnicode=true&characterEncoding=UTF-8";

    @Comment("Enter the username, if the selected datasource uses authentication")
    private String username = "root";

    @Comment("Enter the password, if the selected datasource uses authentication")
    private String password = "";

    /**
     * Get the datasource setting.
     *
     * @return The datasource
     */
    public String datasource() {
        return datasource;
    }

    /**
     * Get the jdbc url.
     *
     * @return The jdbc url
     */
    public String jdbcUrl() {
        return jdbcUrl;
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
     * Get the username.
     *
     * @return The username
     */
    public String username() {
        return username;
    }
}
