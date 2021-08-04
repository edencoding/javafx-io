package com.edencoding.models.openVision;

public class BoundingBox {

    private final Integer x1, y1, x2, y2;

    public BoundingBox(Integer x1, Integer y1, Integer x2, Integer y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Integer getWidth() {
        return x2 - x1;
    }

    public Integer getHeight() {
        return y2 - y1;
    }

    public Integer getX1() {
        return x1;
    }

    public Integer getY1() {
        return y1;
    }

    public Integer getX2() {
        return x2;
    }

    public Integer getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return "bounding box: [" + x1 + ", " + y1 + "], " + "[" + x2 + ", " + y2 + "]";
    }
}
