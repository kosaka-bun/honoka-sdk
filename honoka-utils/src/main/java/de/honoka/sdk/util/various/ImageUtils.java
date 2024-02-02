package de.honoka.sdk.util.various;

import de.honoka.sdk.util.file.FileUtils;
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
        ImageRenderer render = Html2Image.fromHtml(html).getImageRenderer()
                .setWidth(width).setAutoHeight(true);
        BufferedImage img = render.getBufferedImage();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bytes);
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    @SneakyThrows
    private static String getTextImageHtml(String str) {
        URL textHtml = ImageUtils.class.getResource("/text.html");
        if(textHtml == null) return null;
        str = str.replace("\n", "<br>");
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
            if(lines[i].length() <= lineLength) {
                //行长度不超过限制
                textBuilder.append(lines[i]);
            } else {
				/*
				 * 超过限制，将此行拆分为多行，除最后一行外，每行都为固定长度，
				 * 加末尾换行符
				 */
                //计算此行拆分后的行数
                int lineNum = lines[i].length() / lineLength + 1;
                if(lines[i].length() % lineLength == 0) lineNum -= 1;
                //根据最大长度依次截取为多行，除最后一行外，均添加换行符
                for(int j = 0; j < lineNum; j++) {
                    if(j != lineNum - 1) {
                        textBuilder.append(lines[i],
                                j * lineLength,
                                (j + 1) * lineLength).append("\n");
                    } else {
                        textBuilder.append(lines[i].substring(j * lineLength));
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
     * 指定每行最大字符数，自动计算图片大小
     * 文本图片默认图片大小 imageSize = lineLength * 50
     */
    public static InputStream textToImageByLength(String text, int lineLength) {
        return htmlToImage(getTextImageHtml(forceWarp(text, lineLength)),
                lineLength * 53);
    }

    public static InputStream textToImage(String text) {
        return textToImageBySize(text, DEFAULT_IMAGE_WIDTH);
    }
}
