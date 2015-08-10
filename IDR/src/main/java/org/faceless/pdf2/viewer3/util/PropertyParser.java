// $Id: PropertyParser.java 10760 2009-08-27 17:14:51Z mike $

package org.faceless.pdf2.viewer3.util;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import org.faceless.pdf2.viewer3.feature.TextSelection;

public class PropertyParser {

    private PropertyParser() {
    }

    public static Paint getPaint(String text, Paint def) {
        if (text!=null) {
            try {
                int colorint = -1;
                if (text.startsWith("0x")) {
                    colorint = (int)Long.parseLong(text.substring(2), 16);
                    def = new Color(colorint, colorint > 0xFFFFFF || colorint < 0);
                } else if (text.startsWith("#")) {
                    colorint = Integer.parseInt(text.substring(1), 16);
                    def = new Color(colorint, colorint > 0xFFFFFF || colorint < 0);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return def;
    }

    public static Color getColor(String text, Color def) {
        return (Color)getPaint(text, def);
    }

    public static int getHighlightType(String text, int type) {
        if (text!=null) {
            if (text.equalsIgnoreCase("block")) {
                type = TextSelection.TYPE_BLOCK;
            } else if (text.equalsIgnoreCase("underline")) {
                type = TextSelection.TYPE_UNDERLINE;
            } else if (text.equalsIgnoreCase("doubleunderline")) {
                type = TextSelection.TYPE_DOUBLEUNDERLINE;
            } else if (text.equalsIgnoreCase("outline")) {
                type = TextSelection.TYPE_OUTLINE;
            } else if (text.equalsIgnoreCase("strikeout")) {
                type = TextSelection.TYPE_STRIKEOUT;
            } else if (text.equalsIgnoreCase("doublestrikeout")) {
                type = TextSelection.TYPE_DOUBLESTRIKEOUT;
            }
        }
        return type;
    }

    public static Stroke getStroke(String text, Stroke stroke) {
        return stroke;
    }

    public static float getMargin(String text, float margin) {
        if (text!=null) {
            try {
                margin = Float.parseFloat(text);
            } catch (NumberFormatException e) { }
        }
        return margin;
    }
}
