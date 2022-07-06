package de.honoka.util.file;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Objects;

public class FileUtils {

    private static String CLASSPATH;

    /**
     * 检查当前运行的jar包外部是否含有指定的资源文件，若有则忽略此资源，若没有
     * 则从jar包中指定的相对路径处，提取此资源复制到jar包外部相同的相对路径处
     *
     * @param clazz 要提取资源的jar包中的某个类，用于基于此类进行相对路径的定位
     * @param paths 要提取的资源相对于clazz类所在路径的相对路径，以及要提取的
     *              资源所存放的位置相对于当前运行的jar包的相对路径
     * @return 指定的资源当中是否有某些资源原本不在jar包外部
     */
    @SneakyThrows
    public static boolean checkResources(Class<?> clazz, String... paths) {
        boolean result = false;
        String classpath = getClasspath();
        for(String path : paths) {
            URL url = Objects.requireNonNull(clazz.getResource(path));
            File file = new File(Path.of(classpath, path).toString());
            if(file.exists()) continue;
            //指定的资源不存在
            result = true;
            org.apache.commons.io.FileUtils.copyURLToFile(url, file);
        }
        return result;
    }

    public static Date getCreateTime(String filePath) {
        return getCreateTime(new File(filePath));
    }

    public static Date getCreateTime(File file) {
        try {
            Path path = Paths.get(file.getAbsolutePath());
            BasicFileAttributeView basicView = Files.getFileAttributeView(
                    path, BasicFileAttributeView.class,
                    LinkOption.NOFOLLOW_LINKS);
            BasicFileAttributes attr = basicView.readAttributes();
            return new Date(attr.creationTime().toMillis());
        } catch(Exception e) {
            e.printStackTrace();
            return new Date(file.lastModified());
        }
    }

    /**
     * 获取当前运行环境的classpath
     */
    public static String getClasspath() {
        if(CLASSPATH != null) return CLASSPATH;
        try {
            CLASSPATH = new File(Objects.requireNonNull(Thread.currentThread()
                    .getContextClassLoader().getResource("")).toURI())
                    .getAbsolutePath();
        } catch(Exception e) {
            CLASSPATH = new File("").getAbsolutePath();
        }
        return CLASSPATH;
    }

    @SneakyThrows
    public static void checkOrMkdirs(File... dirs) {
        for(File dir : dirs) {
            if(!dir.exists()) dir.mkdirs();
        }
    }

    /**
     * 检查必要的文件是否存在，不存在则创建
     */
    @SneakyThrows
    public static void checkOrTouch(File... files) {
        for(File f : files) {
            org.apache.commons.io.FileUtils.touch(f);
        }
    }
}
