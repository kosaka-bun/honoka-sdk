package de.honoka.sdk.util.test.gui;

import de.honoka.sdk.util.code.CodeUtils;
import de.honoka.sdk.util.code.ColorfulText;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;
import java.util.UUID;

public class GuiTest {

    static {
        //设置本机系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //consoleWindowTest();
        //inputFieldTest();
        //popupMenuTest();
        popupMenuTest2();
    }

    private static void consoleWindowTest() {
        ConsoleWindow window = new ConsoleWindow(
                "hello", null, () -> {
            System.out.println("系统退出");
        });
        //ConsoleWindow window = new ConsoleWindow("hello");
        window.setAutoScroll(true);
        window.setScreenZoomScale(1.25);
        window.show();
        System.out.println(1);
        System.out.println(2);
        System.err.println(3);
        System.out.println("显示一段中文");
        System.out.println("\u001B[32m :: Spring Boot :: \u001B[39m      \u001B[2m (v2.3.5.RELEASE)\u001B[0;39m");
        for(int i = 1; i <= 200; i++) {
            System.out.println(i + "\t" + UUID.randomUUID());
            CodeUtils.threadSleep(10);
        }
        new Exception().printStackTrace();
        String str = window.getText();
        System.out.println(str);
    }

    @SneakyThrows
    private static void inputFieldTest() {
        ConsoleWindow window = new ConsoleWindow("hello");
        window.setScreenZoomScale(1.25);
        window.show();
        Thread.sleep(1000);
        System.out.print("请输入：");
        String str = new Scanner(System.in).nextLine();
        ColorfulText.of().green(str).println();
        Thread.sleep(3000);
        System.out.print("请输入：");
        str = new Scanner(System.in).nextLine();
        ColorfulText.of().green(str).println();
        Thread.sleep(3000);
        System.exit(0);
    }

    @SneakyThrows
    private static void popupMenuTest() {
        //应用主窗口
        JFrame frame = new JFrame();
        //使用JDialog 作为JPopupMenu载体
        JDialog popWindow = new JDialog();
        popWindow.setUndecorated(true);
        //popWindow作为JPopupMenu载体不需要多大的size
        popWindow.setSize(1, 1);

        //创建JPopupMenu
        //重写firePopupMenuWillBecomeInvisible
        //消失后将绑定的组件一起消失
        JPopupMenu pop = new JPopupMenu() {
            @Override
            public void firePopupMenuWillBecomeInvisible() {
                popWindow.setVisible(false);
                System.out.println("JPopupMenu不可见时绑定载体组件popWindow也不可见");
            }
        };
        pop.setSize(100, 30);
        //添加菜单选项
        JMenuItem exit = new JMenuItem("退出aaaaaaaa");
        pop.add(exit);
        exit.addActionListener(e -> {
            System.out.println("点击了退出选项");
            System.exit(0);
        });
        //创建托盘图标
        Image image = Toolkit.getDefaultToolkit().createImage(
                GuiTest.class.getResource("/defaultSystemTrayIcon.jpg")
        );
        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        //给托盘图标添加鼠标监听
        trayIcon.addMouseListener(new MouseAdapter() {

            Dimension dimension;

            @Override
            public void mouseReleased(MouseEvent e) {
                //左键点击
                if (e.getButton() == 1) {
                    //显示窗口
                    frame.setVisible(true);
                } else if (e.getButton() == 3 && e.isPopupTrigger()) {
                    double width, height;
                    if(dimension == null) {
                        System.out.println(pop.getSize());
                        width = pop.getWidth();
                        height = pop.getHeight();
                    } else {
                        System.out.println(dimension);
                        width = dimension.getWidth();
                        height = dimension.getHeight();
                    }
                    //右键点击弹出JPopupMenu绑定的载体以及JPopupMenu
                    popWindow.setLocation(
                            (int) (e.getX() / 1.25 - width / 1.25 - 5),
                            (int) (e.getY() / 1.25 - height / 1.25 - 5)
                    );
                    popWindow.setVisible(true);
                    pop.show(popWindow, 0, 0);
                    if(dimension == null) {
                        dimension = pop.getSize();
                    }
                }
            }
        });
        //取消默认关闭事件，自定义使其放在右下角的系统托盘
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @SneakyThrows
            @Override
            public void windowClosing(WindowEvent e) {
                // 应用主窗口不可见 != 应用退出
                frame.setVisible(false);
                // 将托盘图标添加到系统的托盘实例中
                SystemTray tray = SystemTray.getSystemTray();
                tray.add(trayIcon);
            }
        });
        frame.setBounds(500, 500, 200, 200);
        frame.setVisible(true);
    }

    private static void popupMenuTest2() {
        ConsoleWindow window = new ConsoleWindow(
                "hello中文", null, () -> {
            System.out.println("系统退出");
        });
        //ConsoleWindow window = new ConsoleWindow("hello");
        window.setAutoScroll(true);
        window.setScreenZoomScale(1.25);
        window.addTrayIconMenuItem("中文", false, () -> {});
        window.show();
        System.out.println(123);
        System.err.println(567);
    }
}
