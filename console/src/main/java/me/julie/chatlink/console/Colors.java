package me.julie.chatlink.console;

import java.awt.*;

public enum Colors {
    BLACK("30"),
    RED("31"),
    GREEN("32"),
    YELLOW("33"),
    BLUE("34"),
    PURPLE("35"),
    CYAN("36"),
    WHITE("37"),

    BRIGHT_BLACK("90"),
    BRIGHT_RED("91"),
    BRIGHT_GREEN("92"),
    BRIGHT_YELLOW("93"),
    BRIGHT_BLUE("94"),
    BRIGHT_PURPLE("95"),
    BRIGHT_CYAN("96"),
    BRIGHT_WHITE("97")
    ;
    private static final String prefix = "\u001B[";
    public static final String RESET = prefix + "0m";
    public static final String BOLD = prefix + "1m";
    public static final String UNDERLINE = prefix + "4m";
    public static final String ITALICS = prefix + "3m";
    public static final String STRIKETHROUGH = prefix + "9m";
    private final String value;

    Colors(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return prefix + "0;" + value + "m";
    }

    public static String hex(String hex) {
        Color color = Color.decode(hex);
        return prefix + "38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }

    public static void printReset(String message) {
        System.out.print(RESET + message + RESET);
    }

    public static void printlnReset(String message) {
        System.out.println(RESET + message + RESET);
    }
}
