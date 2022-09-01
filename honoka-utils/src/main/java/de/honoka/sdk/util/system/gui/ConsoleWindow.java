package de.honoka.sdk.util.system.gui;

import de.honoka.sdk.util.code.ActionUtils;
import de.honoka.sdk.util.code.ThrowsRunnable;
import de.honoka.sdk.util.text.TextUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 用于在可执行jar包运行时以窗口形式显示控制台，和可最小化到托盘
 */
public class ConsoleWindow {

    //全局初始化，先于所有类型的初始化执行
    static {
        //设置本机系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private final JFrame frame = new JFrame();

    final String windowName;

    private final JTextPane textPane = new WrapableJTextPane();

    private final int textPaneMaxLine = 200;

    private final Color defaultFontColor = Color.LIGHT_GRAY;

    private final AttributeSet defaultAttributeSet;

    private final JScrollPane scrollPane = new JScrollPane();

    private final JPanel inputFieldContainer = new JPanel();

    private final JTextField inputField = new JTextField();

    private final Dimension defaultFrameSize = new Dimension(1000, 600);

    /**
     * 在高分辨率的屏幕下，通常会使用屏幕比例缩放，在这样的模式下，直接
     * 获取鼠标点击的坐标往往不能获取到正确的坐标，需要指定当前缩放比例
     * 来换算成正确的坐标。
     */
    private double screenZoomScale = 1.0;

    private final Font menuItemFont = new Font("Microsoft YaHei UI",
            Font.PLAIN, 13);

    private volatile boolean autoScroll = true;

    private CheckboxMenuItem autoScrollItem;

    private PopupMenu trayIconMenu;

    /**
     * 设置textPane属性
     */
    private void initTextPane() {
        textPane.setBackground(new Color(43, 43, 43, 255));
        textPane.setForeground(defaultFontColor);
        textPane.setMargin(new Insets(8, 8, 8, 8));
        textPane.setFont(new Font("Microsoft YaHei Mono",
                Font.PLAIN, 18));
        //textArea.setLineWrap(true);
        textPane.setEditable(false);
        //监听ScrollLock
        textPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = KeyEvent.VK_SCROLL_LOCK;
                if(e.getKeyCode() != keyCode) return;
                setAutoScroll(!isAutoScroll());
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        //设置当文本框内容改变时要执行的操作
        Document doc = textPane.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            private volatile Thread runningScrollThread;

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(runningScrollThread != null) return;
                synchronized(this) {
                    if(runningScrollThread != null) return;
                    runningScrollThread = new Thread(() -> {
                        if(isAutoScroll()) doAutoScroll();
                        runningScrollThread = null;
                    });
                    runningScrollThread.start();
                }
            }

            //仅添加内容时滚动，其他修改不进行操作
            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        //添加textPane右键弹出菜单
        initTextPaneMenu(textPane);
    }

    private void initTextPaneMenu(JTextPane textPane) {
        PopupMenu textAreaMenu = new PopupMenu();
        JWindow screen = new JWindow();   //定义屏幕左上角位置，让弹出菜单相对于它定位
        screen.setLocation(0, 0);
        screen.setSize(0, 0);
        screen.add(textAreaMenu);
        //复制项
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setFont(menuItemFont);
        copyItem.addActionListener(e -> {
            try {
                //获取系统剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                //封装文本内容
                Transferable trans = new StringSelection(textPane.getSelectedText());
                //把文本内容设置到系统剪贴板
                clipboard.setContents(trans, null);
            } catch(Exception ex) {
                //none
            }
        });
        textAreaMenu.add(copyItem);
        //恢复窗口大小项
        MenuItem resizeItem = new MenuItem("Reset Window Size");
        resizeItem.setFont(menuItemFont);
        resizeItem.addActionListener(e -> frame.setSize(defaultFrameSize));
        textAreaMenu.add(resizeItem);
        //自动滚屏项
        CheckboxMenuItem autoScrollItem =
                new CheckboxMenuItem("Auto Scroll / Auto Clear");
        autoScrollItem.setFont(menuItemFont);
        autoScrollItem.addItemListener(e -> {
            boolean state = e.getStateChange() == ItemEvent.SELECTED;
            setAutoScroll(state);
        });
        textAreaMenu.add(autoScrollItem);
        this.autoScrollItem = autoScrollItem;
        updateAutoScrollLockItem(isAutoScroll());
        //region 监听弹出右键菜单
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON3) return;
                new Thread(() -> {
                    screen.setVisible(true);
                    textAreaMenu.show(screen,
                            (int) (e.getXOnScreen() * screenZoomScale),
                            (int) (e.getYOnScreen() * screenZoomScale));
                    screen.setVisible(false);
                }).start();
            }
        });
        //endregion
    }

    /**
     * 转移系统输出流
     */
    @SneakyThrows
    private void changeSystemOut() {
        class ConsoleOutputStreamImpl extends ConsoleOutputStream {

            public ConsoleOutputStreamImpl(PrintStream systemPrintStream,
                                           Color printColor) {
                super(systemPrintStream, printColor);
            }

            @Override
            public void print(List<Byte> bytes) {
                writeToTextPane(this, bytes);
            }
        }
        ConsoleOutputStream newOut = new ConsoleOutputStreamImpl(System.out,
                defaultFontColor);
        ConsoleOutputStream newErr = new ConsoleOutputStreamImpl(System.err,
                new Color(255, 107, 103, 255));
        PrintStream newOutPrintStream =
                new PrintStream(newOut, false, "UTF-8");
        PrintStream newErrPrintStream =
                new PrintStream(newErr, false, "UTF-8");
        System.setOut(newOutPrintStream);
        System.setErr(newErrPrintStream);
    }

    private void changeSystemIn() {
        System.setIn(new ConsoleInputStream(this));
    }

    /**
     * 加载滚动框，分别设置水平和垂直滚动条自动出现
     */
    private void initScrollPane() {
        scrollPane.setViewportView(textPane);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);
    }

    /**
     * 加载控制台输入框
     */
    private void initInputField() {
        inputFieldContainer.setLayout(new BorderLayout(10, 10));
        inputFieldContainer.add(new JLabel(), BorderLayout.NORTH);
        inputFieldContainer.add(new JLabel(), BorderLayout.SOUTH);
        inputFieldContainer.add(new JLabel(), BorderLayout.EAST);
        //label
        //空格是为了模拟左边距
        JLabel label = new JLabel("  输入到控制台：");
        label.setFont(menuItemFont);
        inputFieldContainer.add(label, BorderLayout.WEST);
        //inputField
        inputField.setFont(menuItemFont);
        ActionListener actionListener = e -> {
            String input = inputField.getText() + "\n";
            //将自己输入的内容用蓝色字回显
            writeToTextPane(input, ColorAttributeSets.getAttributeSet(34));
            ConsoleInputStream systemIn = (ConsoleInputStream) System.in;
            synchronized(System.in) {
                byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
                for(byte aByte : bytes) {
                    systemIn.buffer.offer(aByte);
                }
                systemIn.buffer.offer((byte) -1);
                systemIn.notifyAll();
            }
        };
        inputField.registerKeyboardAction(
                actionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
                        false),
                JComponent.WHEN_FOCUSED
        );
        inputFieldContainer.add(inputField);
        //frame
        frame.add(inputFieldContainer, BorderLayout.SOUTH);
        hideInputField();
    }

    /**
     * 加载窗口
     */
    private void initFrame() {
        frame.setTitle(windowName);
        frame.setMinimumSize(defaultFrameSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    /**
     * 加载系统托盘图标
     */
    @SneakyThrows
    private void initSystemTrayIcon(Image systemTrayIcon,
                                    ThrowsRunnable onExit) {
        if(!SystemTray.isSupported())
            throw new RuntimeException("不支持系统托盘");
        //获取当前平台的系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        //创建点击图标时的弹出菜单
        PopupMenu popupMenu = new PopupMenu();
        initTrayIconMenu(popupMenu, onExit);
        trayIconMenu = popupMenu;
        //创建一个托盘图标
        TrayIcon trayIcon = new TrayIcon(systemTrayIcon, windowName, trayIconMenu);
        //托盘图标自适应尺寸
        trayIcon.setImageAutoSize(true);
        //region 设置鼠标点击监听
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 不需要添加右键事件，只要托盘图标在初始化时指定了弹出菜单，
                 * 右键点击托盘图标就能弹出这个菜单 */
                //监听左键点击托盘图标事件
                if(e.getButton() != MouseEvent.BUTTON1) return;
                if(!frame.isVisible()) show();
            }
        });
        //endregion
        //添加托盘图标到系统托盘
        tray.add(trayIcon);
    }

    private void initTrayIconMenu(PopupMenu popupMenu,
                                  ThrowsRunnable onExit) {
        //应用名项
        MenuItem applicationNameItem = new MenuItem(windowName);
        applicationNameItem.setFont(menuItemFont);
        applicationNameItem.setEnabled(false);
        popupMenu.add(applicationNameItem);
        popupMenu.addSeparator();
        //退出项
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setFont(menuItemFont);
        exitItem.addActionListener(e -> {
            //点击菜单的退出按钮时，执行退出时方法，然后退出程序
            int option = JOptionPane.showConfirmDialog(frame,
                    "确定退出吗？", windowName,
                    JOptionPane.OK_CANCEL_OPTION);
            //判断是否选择了“是”选项
            if(option == JOptionPane.OK_OPTION) {
                //通过新线程执行退出方法，避免卡住界面
                new Thread(() -> exitApplication(exitItem, onExit)).start();
            }
        });
        popupMenu.add(exitItem);
    }

    public ConsoleWindow(String windowName) {
        this.windowName = windowName;
        //初始化默认样式集
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, defaultFontColor);
        defaultAttributeSet = attributeSet;
        //设置textPane属性
        initTextPane();
        //转移系统输入输出流
        changeSystemOut();
        changeSystemIn();
        //分别设置水平和垂直滚动条自动出现
        initScrollPane();
        //加载控制台输入框
        initInputField();
        //加载窗口
        initFrame();
    }

    public ConsoleWindow(String windowName, URL iconPath,
                         ThrowsRunnable onExit) {
        this(windowName);
        //有系统托盘图标时，最小化则隐藏窗口
        //windowListener.hideWindowOnIconified = true;
        //未提供图标则加载默认图标
        if(iconPath == null)
            iconPath = this.getClass().getResource("/img/java.png");
        //创建图片对象
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(iconPath));
        //加载系统托盘图标
        initSystemTrayIcon(icon.getImage(), onExit);
        //加载窗口时默认指定的是关闭窗口时退出程序
        //有了托盘图标后需要对窗口关闭时的动作进行修改
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void show() {
        if(frame.isVisible()) return;
        //窗口可视
        frame.setVisible(true);
        //将窗口显示出来（如果是最小化到任务栏的状态）
        switch(frame.getExtendedState()) {
            case JFrame.ICONIFIED:    //最小化
                frame.setExtendedState(JFrame.NORMAL);
                break;
            case 7:  //7表示最大化的窗口被最小化到任务栏
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                break;
        }
    }

    public void hide() {
        frame.setVisible(false);
    }

    /**
     * 获取有限行数的控制台内容的HTML表示文本
     * 控制台每一行对应一个HTML的pre标签，该标签将记录该行的颜色
     */
    //查找、添加和删除不可同时进行，因此需要添加synchronized
    @SneakyThrows
    public synchronized String getText(int limit) {
        //将document中的内容转换为html
        StyledDocument doc = textPane.getStyledDocument();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        htmlEditorKit.write(bao, doc, 0, doc.getLength());
        String html = bao.toString();
        //解析每个span中包含的文本内容
        List<String> contents = new ArrayList<>();
        for(Iterator<String> lines = TextUtils.getLines(html).iterator();
            lines.hasNext(); ) {
            String line = lines.next();
            //查找起始p标签
            if(!line.trim().startsWith("<p")) continue;
            while(lines.hasNext()) {
                line = lines.next();
                //查找起始span标签
                String trimedLine = line.trim();
                if(trimedLine.startsWith("<span")) {
                    //计算缩进
                    int indent = line.indexOf("<") + 2;
                    StringBuilder content = new StringBuilder();
                    //获取span标签之间的每一行
                    while(lines.hasNext()) {
                        line = lines.next();
                        //遇到结束标签即跳出
                        if(!line.trim().startsWith("</span")) {
                            try {
                                content.append(line.substring(indent));
                            } catch(Exception e) {
                                //字符不足缩进，去除左侧空格，保留右侧空格
                                content.append(StringUtils.stripStart(
                                        line, null));
                            }
                        } else break;
                    }
                    //转义<>符号，然后添加到列表中，表示一个span标签的文本内容
                    String contentStr = content.toString()
                            .replaceAll("<", "&lt;")
                            .replaceAll(">", "&gt;");
                    contents.add(contentStr);
                } else if(trimedLine.startsWith("</p")) {
                    //遇到结束p标签，为文本内容列表中的最后一个元素添加html换行符
                    int lastIndex = contents.size() - 1;
                    String last = contents.get(lastIndex) + "<br>";
                    contents.set(lastIndex, last);
                    //跳出，查找下一个起始p标签
                    break;
                }
            }
        }
        //提取每个span的颜色
        Elements spans = Jsoup.parse(html).select("span");
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < spans.size(); i++) {
            org.jsoup.nodes.Element span = spans.get(i);
            String style = span.attr("style");
            int colorPos = style.indexOf("color:");
            style = style.substring(colorPos, style.indexOf(";", colorPos) + 1);
            result.append("<pre style=\"").append(style).append("\">")
                    .append(contents.get(i)).append("</pre>");
        }
        //判断是否需要截取
        String resultStr = result.toString();
        int lineCount = StringUtils.countMatches(resultStr, "<br>") + 1;
        //最后一行为空行，故实际限制数量应为limit + 1
        limit += 1;
        if(lineCount > limit) {
            int removeLineCount = lineCount - limit;
            resultStr = resultStr.substring(resultStr.indexOf(
                    "<pre", StringUtils.ordinalIndexOf(resultStr,
                            "<br>", removeLineCount)
            ));
        }
        return resultStr;
    }

    //不论自动滚动是否打开，始终返回固定限制数量的文本
    public synchronized String getText() {
        return getText(textPaneMaxLine);
    }

    private synchronized void writeToTextPane(
            ConsoleOutputStream out, List<Byte> buffer) {
        byte[] bytes = new byte[buffer.size()];
        for(int i = 0; i < buffer.size(); i++) {
            bytes[i] = buffer.get(i);
        }
        String str = new String(bytes, StandardCharsets.UTF_8);
        //按颜色输出
        String[] parts = str.split("\\u001B");
        for(int i = 0; i < parts.length; i++) {
            if(i == 0) {
                //第一部分直接按原色输出
                writeToTextPane(parts[i], out.getPrintAttributeSet());
            } else {
                //其他部分先进行处理，改变输出流颜色，并去除控制序列，再输出
                String part = out.changePrintColorByAnsiString(parts[i]);
                writeToTextPane(part, out.getPrintAttributeSet());
            }
        }
    }

    @SneakyThrows
    synchronized void writeToTextPane(String str, AttributeSet attributeSet) {
        StyledDocument doc = textPane.getStyledDocument();
        if(attributeSet == null) attributeSet = defaultAttributeSet;
        doc.insertString(doc.getLength(), str, attributeSet);
    }

    /**
     * 通过textPane使滚动组件滚动到最底部
     */
    private synchronized void textPaneScrollToEnd() {
        int docLength = textPane.getDocument().getLength();
        //防止当光标位置等于要移动到的位置时，移动光标位置不触发滚动的问题
        if(textPane.getCaretPosition() == docLength && docLength > 0)
            textPane.setCaretPosition(docLength - 1);
        textPane.setCaretPosition(docLength);
    }

    /**
     * 判断文本框内容是否达到最大长度，若达到则移除多余部分
     */
    @SneakyThrows
    private synchronized void removeSurplusTextInTextPane() {
        Document doc = textPane.getDocument();
        int lineCount = TextUtils.getLines(
                doc.getText(0, doc.getLength())
        ).size();
        if(lineCount <= textPaneMaxLine) return;
        //清除多余的行
        int offset = StringUtils.ordinalIndexOf(
                doc.getText(0, doc.getLength()), "\n",
                lineCount - textPaneMaxLine) + 1;
        textPane.getDocument().remove(0, offset);
    }

    private void doAutoScroll() {
        //判断文本框内容是否达到最大长度，若达到则移除多余部分
        removeSurplusTextInTextPane();
        //滚屏到页尾
        textPaneScrollToEnd();
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
        updateAutoScrollLockItem(isAutoScroll());
    }

    private void updateAutoScrollLockItem(boolean autoScroll) {
        autoScrollItem.setState(autoScroll);
        if(autoScroll) doAutoScroll();
    }

    private void exitApplication(MenuItem exitItem, ThrowsRunnable onExit) {
        //在执行前先禁用此退出选项
        exitItem.setEnabled(false);
        System.out.println("正在退出……");
        //执行退出方法
        try {
            if(onExit != null) onExit.throwsRun();
            System.exit(0);
        } catch(Throwable t) {
            t.printStackTrace();
            //退出失败，询问是否强行退出
            int option = JOptionPane.showConfirmDialog(frame,
                    "退出时出现了异常，是否强制退出应用？",
                    windowName, JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) System.exit(-1);
            //不选择强制退出，则恢复退出选项
            exitItem.setEnabled(true);
        }
    }

    public double getScreenZoomScale() {
        return screenZoomScale;
    }

    public void setScreenZoomScale(double screenZoomScale) {
        this.screenZoomScale = screenZoomScale;
    }

    public void setTextPaneFont(Font font) {
        textPane.setFont(font);
    }

    public void setTextPaneFont(String fontFamily) {
        setTextPaneFont(new Font(fontFamily, Font.PLAIN, 18));
    }

    public void addTrayIconMenuItem(String name, boolean needConfirm,
                                    ThrowsRunnable action) {
        if(trayIconMenu == null) return;
        MenuItem item = new MenuItem(name);
        item.setFont(menuItemFont);
        item.addActionListener(e -> {
            //进行操作确认
            if(needConfirm) {
                int option = JOptionPane.showConfirmDialog(frame,
                        "确定执行" + name + "吗？", windowName,
                        JOptionPane.OK_CANCEL_OPTION);
                //判断是否选择了“是”选项
                if(option != JOptionPane.OK_OPTION) return;
            }
            //执行指定的方法
            //通过新线程执行方法，避免卡住界面
            new Thread(() -> {
                item.setEnabled(false);
                ActionUtils.doAction(name, action);
                item.setEnabled(true);
            }).start();
        });
        trayIconMenu.insert(item, trayIconMenu.getItemCount() - 1);
    }

    public void showInputField() {
        inputFieldContainer.setVisible(true);
    }

    public void hideInputField() {
        inputFieldContainer.setVisible(false);
    }
}
