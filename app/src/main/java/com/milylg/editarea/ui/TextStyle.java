package com.milylg.editarea.ui;
public class TextStyle {

    enum TextColor {
        PLAIN("#90303030"), BROWN("#F0E57373"),
        GREEN("#F0AED581"), PURPLE("#D0BB86FC"),
        ORANGE("#F0FF8A65"), BLACK_LIGHT("#90807060");

        private final String hex;

        TextColor(String color) {
            this.hex = color;
        }

        public String color() {
            return hex;
        }
    }

    private String textColor;

    public TextStyle(TextColor textColor){
        this.textColor = textColor.color();
    }

    public String getTextColor() {
        return textColor;
    }

    public void restore(String color) {
        textColor = color;
    }

    @Deprecated
    public void modifiedTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String rotation() {
        TextColor[] colors = TextColor.values();
        for (TextColor color:colors) {
            if (textColor.equals(color.hex)) {
                int len = colors.length;
                int next = (color.ordinal() + 1) % len;
                textColor = colors[next].color();
                break;
            }
        }
        return textColor;
    }

    public String htmlValue() {
        return " style=\"+ textColor +\";";
    }
}
