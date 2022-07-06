package de.honoka.util.web;

public interface WebBrowser extends AutoCloseable {

    /**
     * 获取当前URL
     */
    String getNowUrl();

    /**
     * 加载一个URL并等待加载完成
     */
    void loadUrl(String url);

    /**
     * 获取当前网页的HTML文本
     */
    String getHtml();

    /**
     * 执行JS代码，不需要返回值
     */
    void executeJSCode(String jsCode);

    /**
     * 执行JS代码，需要返回值
     */
    String executeJSCodeWithReturn(String jsCode);
}
