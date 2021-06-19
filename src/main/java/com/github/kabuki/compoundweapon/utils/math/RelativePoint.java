package com.github.kabuki.compoundweapon.utils.math;

import net.minecraft.entity.Entity;

public class RelativePoint {

    protected double field_0;
    protected double field_1;
    private final Point.PointParser type;

    private RelativePoint(double v1, double v2, Point.PointParser type)
    {
        this.field_0 = v1;
        this.field_1 = v2;
        this.type = type;
    }

    /**
     * @param x Abscissa
     * @param y Ordinate
     * @return a Cartesian coordinate
     */
    public static RelativePoint createCartesianPoint(double x, double y) {
        return new RelativePoint(x, y, Point.PointParser.CARTESIAN);
    }

    /**
     * @param theta Angle θ
     * @param r Radius
     * @return a Polar coordinate
     */
    public static RelativePoint createPolarPoint(double theta, double r) {
        return new RelativePoint(theta, r, Point.PointParser.POLAR);
    }

    /**
     * @return the real position
     */
    public Point toPoint(Entity entity)
    {
        return type.toReal(this, entity);
    }
}

