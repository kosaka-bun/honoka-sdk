package de.honoka.sdk.util.system.gui;

import javax.swing.*;
import javax.swing.text.*;

/**
 * 该类是真正实现超长单词都能自动换行的JTextPane的子类。
 *
 * Java 7以下版本的JTextPane本身都能实现自动换行，对超长单词都能有效，
 * 但从Java 7开始，超长单词就不能自动换行，导致JTextPane的实际宽度变大，
 * 使得滚动条出现。下面的方法是对这个bug的较好修复。
 */
public class WrapableJTextPane extends JTextPane {

    //以下内部类全都用于实现自动强制折行

    private static class WarpEditorKit extends StyledEditorKit {

        private final ViewFactory defaultFactory = new WarpColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    private static class WarpColumnFactory implements ViewFactory {

        public View create(Element elem) {
            String kind = elem.getName();
            if(kind != null) {
                switch(kind) {
                    case AbstractDocument.ContentElementName:
                        return new WarpLabelView(elem);
                    case AbstractDocument.ParagraphElementName:
                        return new ParagraphView(elem);
                    case AbstractDocument.SectionElementName:
                        return new BoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName:
                        return new ComponentView(elem);
                    case StyleConstants.IconElementName:
                        return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    private static class WarpLabelView extends LabelView {

        public WarpLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch(axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }

    public WrapableJTextPane() {
        super();
        this.setEditorKit(new WarpEditorKit());
    }
}
