package de.honoka.sdk.util.file;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class AbstractEnvironmentPathUtils {

    public enum BuildTool {

        MAVEN, GRADLE
    }

    private final BuildTool buildTool;

    public AbstractEnvironmentPathUtils(BuildTool buildTool) {
        this.buildTool = buildTool;
    }

    public String getDataDirectoryOfApp() {
        String mainClasspath = FileUtils.getMainClasspath();
        if(FileUtils.isAppRunningInJar()) return mainClasspath;
        switch(buildTool) {
            //大括号用于防止在不同的case块当中，由于变量名相同而产生的冲突
            case MAVEN: {
                File classesDir = Paths.get(mainClasspath, "..").normalize().toFile();
                if(!Objects.equals(classesDir.getName(), "classes")) {
                    throw new RuntimeException("Not normal maven classes directory: " + classesDir.getAbsolutePath());
                }
                return Paths.get(classesDir.getAbsolutePath(), "../data").normalize().toString();
            }
            case GRADLE: {
                File classesDir = Paths.get(mainClasspath, "../..").normalize().toFile();
                if(!Objects.equals(classesDir.getName(), "classes")) {
                    throw new RuntimeException("Not normal gradle classes directory: " + classesDir.getAbsolutePath());
                }
                return Paths.get(classesDir.getAbsolutePath(), "../data").normalize().toString();
            }
            default:
                throw new RuntimeException("Unknown build tool: " + buildTool);
        }
    }
}
