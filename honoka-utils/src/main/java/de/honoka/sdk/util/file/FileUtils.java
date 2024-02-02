package de.honoka.sdk.util.file;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {

    private static volatile String MAIN_CLASSPATH;

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
    public static boolean copyResourceIfNotExists(Class<?> clazz, String... paths) {
        if(!isAppRunningInJar()) return false;
        boolean result = false;
        String mainClasspath = getMainClasspath();
        for(String path : paths) {
            URL url = clazz.getResource(path);
            if(url == null) continue;
            File file = new File(Paths.get(mainClasspath, path).toString());
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

    public static boolean isAppRunningInJar() {
        URL rootResourceUrl = Thread.currentThread().getContextClassLoader().getResource("");
        if(rootResourceUrl == null) {
            throw new RuntimeException("Failed to get root resource");
        }
        return isAppRunningInJar(rootResourceUrl);
    }

    private static boolean isAppRunningInJar(URL rootResourceUrl) {
        if(rootResourceUrl == null) {
            throw new NullPointerException("rootResourceUrl is null");
        }
        return Objects.equals(rootResourceUrl.getProtocol(), "jar");
    }

    /**
     * 获取当前运行环境的主classpath的绝对路径
     * <p>
     * 当Java应用程序在jar包中被运行时，此路径为jar包所在目录的路径。在IDE中直接运行时，此路径为
     * 项目构建目录中的java源代码编译输出路径（如Maven中为“[项目目录]/target/classes”）。
     */
    @SneakyThrows
    public static String getMainClasspath() {
        if(MAIN_CLASSPATH != null) return MAIN_CLASSPATH;
        URL rootResourceUrl = Thread.currentThread().getContextClassLoader().getResource("");
        if(isAppRunningInJar(rootResourceUrl)) {
            String path = rootResourceUrl.getPath();
            String pathEndSymbol = ".jar!/";
            int lowercaseSymbolIndex = path.indexOf(pathEndSymbol);
            int uppercaseSymbolIndex = path.indexOf(pathEndSymbol.toUpperCase());
            List<Integer> symbolIndexes = new HashSet<>(Arrays.asList(
                lowercaseSymbolIndex, uppercaseSymbolIndex
            )).stream().filter(it -> it != -1).sorted().collect(Collectors.toList());
            if(!path.startsWith("file:/") || symbolIndexes.isEmpty()) {
                throw new RuntimeException("Root resource path is invalid: " + path);
            }
            path = path.substring(6, symbolIndexes.get(0) + 4);
            path = path.substring(0, path.lastIndexOf("/"));
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
            MAIN_CLASSPATH = Paths.get(path).normalize().toString();
        } else {
            MAIN_CLASSPATH = new File(Objects.requireNonNull(rootResourceUrl).toURI()).getAbsolutePath();
        }
        return MAIN_CLASSPATH;
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

    @SneakyThrows
    public static String fetchUrlResourceAndToString(URL url) {
        try(InputStream is = url.openStream()) {
            return new String(IOUtils.toByteArray(is));
        }
    }
}
