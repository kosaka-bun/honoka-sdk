package de.honoka.sdk.util.various;

import de.honoka.sdk.util.file.FileUtils;
import de.honoka.sdk.util.text.TextUtils;
import gui.ava.html.Html2Image;
import gui.ava.html.renderer.ImageRenderer;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class ImageUtils {

    public static final int DEFAULT_IMAGE_WIDTH = 1000;

    @SneakyThrows
    public static InputStream htmlToImage(String html, int width) {
        ImageRenderer render = Html2Image.fromHtml(html).getImageRenderer().setWidth(width).setAutoHeight(true);
        BufferedImage img = render.getBufferedImage();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bytes);
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    @SneakyThrows
    private static String getTextImageHtml(String str) {
        URL textHtml = ImageUtils.class.getResource("/text.html");
        if(textHtml == null) return null;
        str = "<pre>" + str + "</pre>";
        str = str.replace("\t", "    ");
        return String.format(FileUtils.fetchUrlResourceAndToString(textHtml), str);
    }

    /**
     * 文本强制换行，每行字数限制在一定范围内，超过则强制拆分为多行
     */
    private static String forceWarp(String text, int lineLength) {
        //提取行
        String[] lines = text.split("\n");
        StringBuilder textBuilder = new StringBuilder();
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(TextUtils.getHalfWidthLength(line) <= lineLength) {
                //行长度不超过限制
                textBuilder.append(line);
            } else {
                int halfWidthIndex = 0;
                for(int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    textBuilder.append(c);
                    halfWidthIndex += c < 0x800 ? 1 : 2;
                    if(halfWidthIndex >= lineLength) {
                        halfWidthIndex = 0;
                        textBuilder.append("\n");
                    }
                }
            }
            //当前添加的行（不论是否被拆分）是否为最后一行
            if(i != lines.length - 1) textBuilder.append("\n");
        }
        return textBuilder.toString();
    }

    /**
     * 指定图片大小，不限制每行字符数
     */
    public static InputStream textToImageBySize(String text, int imageSize) {
        return htmlToImage(getTextImageHtml(text), imageSize);
    }

    /**
     * 指定每行最大字符数（半角），自动计算图片大小。
     * <p>
     * 文本图片默认图片大小 <code>imageSize</code> = <code>lineLength</code> * 26
     */
    public static InputStream textToImageByLength(String text, int lineLength) {
        return htmlToImage(getTextImageHtml(forceWarp(text, lineLength)), lineLength * 28);
    }

    public static InputStream textToImage(String text) {
        return textToImageBySize(text, DEFAULT_IMAGE_WIDTH);
    }
}
