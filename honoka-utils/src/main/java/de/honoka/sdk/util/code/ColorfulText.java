package de.honoka.sdk.util.code;

@SuppressWarnings("unused")
public class ColorfulText {

    private static final String template = "\u001B[%sm%s\u001B[0m";

    private final StringBuilder text = new StringBuilder();

    private ColorfulText() {
    }

    @Override
    public String toString() {
        return text.toString();
    }

    public static ColorfulText of() {
        return new ColorfulText();
    }

    public void print() {
        System.out.print(text);
    }

    public void println() {
        System.out.println(text);
    }

    private ColorfulText add(int colorCode, Object text) {
        this.text.append(String.format(template, colorCode, text.toString()));
        return this;
    }

    public ColorfulText manual(int code, Object o) {
        return add(code, o);
    }

    public ColorfulText white(Object o) {
        return add(30, o);
    }

    public ColorfulText red(Object o) {
        return add(31, o);
    }

    public ColorfulText green(Object o) {
        return add(32, o);
    }

    public ColorfulText darkYellow(Object o) {
        return add(33, o);
    }

    public ColorfulText blue(Object o) {
        return add(34, o);
    }

    public ColorfulText purple(Object o) {
        return add(35, o);
    }

    public ColorfulText aqua(Object o) {
        return add(36, o);
    }

    public ColorfulText underline(Object o) {
        return add(21, o);
    }

    //-----------------------------------------------

    public ColorfulText pink(Object o) {
        return add(91, o);
    }

    //32号和92号颜色相同
    @Deprecated
    public ColorfulText lightGreen(Object o) {
        return add(92, o);
    }

    public ColorfulText yellow(Object o) {
        return add(93, o);
    }

    public ColorfulText lightBlue(Object o) {
        return add(94, o);
    }

    public ColorfulText lightPurple(Object o) {
        return add(95, o);
    }

    public ColorfulText lightAqua(Object o) {
        return add(96, o);
    }

    public ColorfulText black(Object o) {
        return add(97, o);
    }
}
