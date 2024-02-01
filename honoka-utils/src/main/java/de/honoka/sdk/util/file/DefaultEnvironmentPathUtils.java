package de.honoka.sdk.util.file;

public class DefaultEnvironmentPathUtils extends AbstractEnvironmentPathUtils {

    public static final DefaultEnvironmentPathUtils instance = new DefaultEnvironmentPathUtils();

    public DefaultEnvironmentPathUtils() {
        super(BuildTool.GRADLE);
    }
}
