package com.cleenr.cleen_r.objectCategorisation;

/**
 * Created by lorenzo toso on 23.03.15.
 */
public class Category {
    Shape shape = Shape.NONE;
    Color color = Color.NONE;
    public Category(Shape shape, Color color)
    {
        this.shape = shape;
        this.color = color;
    }
    public String toString()
    {
        return shape.toString() + " " + color.toString();
    }
}
