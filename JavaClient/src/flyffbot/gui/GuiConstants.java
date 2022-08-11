package flyffbot.gui;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GuiConstants {
    public final int padding = 4;

    public final int rowWidth = 360;
    public final int rowWidth35 = (int) (rowWidth*0.35);

    public final int rowWidth65 = (int) (rowWidth*0.65);

    public final int rowHeight = 30;
    public final int rowHeight75 = (int) (rowHeight * 0.75);
    public final int rowHeight50 = (int) (rowHeight * 0.5);
    public final int rowHeightLg = 40;
    public final int frameHeight = 376;
    public static int boxPadding = 5;
    public static int scrollbarWidth = 15;
    public static int customActionSlotColumnWidth = 80;
    public static int customActionSlotActionWidth = (int) (GuiConstants.customActionSlotColumnWidth*0.25);

    public static int textHeight = 16;
}
