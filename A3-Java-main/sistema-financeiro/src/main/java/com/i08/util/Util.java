package com.i08.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class Util {
    public static void centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (screenSize.width - w) / 2;
        int y = (screenSize.height - h) / 2;
        frame.setLocation(x, y);
    }
}
