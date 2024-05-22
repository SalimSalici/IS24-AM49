package it.polimi.ingsw.am49.view.gui;

public enum PointsCoordinates {
    POINT_0(90, 637),
    POINT_1(171, 637),
    POINT_2(253, 637),

    POINT_3(292, 563),
    POINT_4(212, 563),
    POINT_5(131, 563),
    POINT_6(51, 563),

    POINT_7(51, 489),
    POINT_8(131, 489),
    POINT_9(212, 489),
    POINT_10(292, 489),

    POINT_11(292, 415),
    POINT_12(212, 415),
    POINT_13(131, 415),
    POINT_14(51, 415),

    POINT_15(51, 341),
    POINT_16(131, 341),
    POINT_17(212, 341),
    POINT_18(292, 341),

    POINT_19(292, 268),
    POINT_20(171, 232),
    POINT_21(51, 268),
    POINT_22(51, 193),
    POINT_23(51, 119),
    POINT_24(96, 60),
    POINT_25(171, 45),
    POINT_26(246, 60),
    POINT_27(292, 119),
    POINT_28(292, 193),
    POINT_29(171, 138);

    private final double x;
    private final double y;

    PointsCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static PointsCoordinates fromNumber(int number) {
        return PointsCoordinates.values()[number];
    }
}
