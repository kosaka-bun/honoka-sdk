package de.honoka.sdk.util.text;

import org.apache.commons.text.StringEscapeUtils;

//@SuppressWarnings("deprecation")
public class HtmlUtils {

    /**
     * 由于html文档中的“<>”符号都被转义，文本内容中不可能出现这个符号，所以可以根据
     * 这两个符号检查字符串中的html标签，并加以清除。同时将html中的转义符号还原。
     *
     * @param html html文档字符串
     * @return 清除后的文本
     */
    public static String clearHtmlTags(String html) {
        //由于文档字符串可能存在缩进空格，为了不让其影响文本，需要予以清除
        String[] htmlRows = html.split("\n");    //将文档字符串分为每一行
        StringBuilder htmlBuilder = new StringBuilder();
        for(String htmlRow : htmlRows) {
            //去除每一行左右两边的缩进空格后拼接为一个字符串
            htmlBuilder.append(htmlRow.trim()).append("\n");
        }
        html = htmlBuilder.toString();
        //println(html);
        while(html.contains("<") && html.contains(">")) {
            //检索html标签
            int leftSymbolIndex = html.indexOf("<"), rightSymbolIndex = html.indexOf(">");
            //要清除的html标签
            String tag = html.substring(leftSymbolIndex, rightSymbolIndex + 1);
            //println(tag);
            //将文档中与此标签匹配的标签清除
            html = html.replace(tag, "");
        }
        //解码最终结果中的html转义字符
        html = StringEscapeUtils.unescapeHtml4(html);
        return html;
    }
}
