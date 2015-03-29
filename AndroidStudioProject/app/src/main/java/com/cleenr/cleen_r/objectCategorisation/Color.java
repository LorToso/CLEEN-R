package com.cleenr.cleen_r.objectCategorisation;

import org.opencv.core.Scalar;

/**
 * Created by lorenzo on 23.03.15.
 */
public enum Color {
    NONE,

    RED,
    BROWN,
    YELLOW,
    OLIVE,
    LIME,
    GREEN,
    AQUA,
    TEAL,
    BLUE,
    NAYY,
    FUCHISA,
    PURPLE;

    /**
     * Returns a color-category based on an HSV-Color
     * https://en.wikipedia.org/wiki/Web_colors#HTML_color_names
     * @param colorHSV The HSV-Color as Scalar
     * @return The color-category of the given color
     */
    public static Color getObjectColor(Scalar colorHSV)
    {
        if(colorHSV.val[0] < 60)
        {
            if(colorHSV.val[2] > 128)
                return RED;
            return BROWN;
        }
        if(colorHSV.val[0] < 120)
        {
            if(colorHSV.val[2] > 128)
                return YELLOW;
            return OLIVE;
        }
        if(colorHSV.val[0] < 180)
        {
            if(colorHSV.val[2] > 128)
                return LIME;
            return GREEN;
        }
        if(colorHSV.val[0] < 240)
        {
            if(colorHSV.val[2] > 128)
                return AQUA;
            return TEAL;
        }
        if(colorHSV.val[0] < 300)
        {
            if(colorHSV.val[2] > 128)
                return BLUE;
            return NAYY;
        }
        if(colorHSV.val[2] > 128)
            return FUCHISA;
        return PURPLE;
    }
}
