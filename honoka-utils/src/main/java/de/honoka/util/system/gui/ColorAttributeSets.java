package de.honoka.util.system.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class ColorAttributeSets {

    private static AttributeSet color(int r, int g, int b) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, new Color(r, g, b, 255));
        return attributeSet;
    }

    public static final int MIN_CODE = 30, MAX_CODE = 39;

    private static final AttributeSet[] SETS = {
            color(255, 255, 255),   //30
            color(255, 107, 105),   //31
            color(168, 192, 35),    //32
            color(213, 191, 86),    //33
            color(83, 147, 236),    //34
            color(173, 138, 190),    //35
            color(40, 153, 153),    //36
            color(153, 153, 153),    //37
            color(187, 187, 187),    //38
            color(187, 187, 187)    //39
    };

    public static AttributeSet getAttributeSet(int ansiCode) {
        if(ansiCode < MIN_CODE || ansiCode > MAX_CODE) return null;
        return SETS[ansiCode - 30];
    }
}