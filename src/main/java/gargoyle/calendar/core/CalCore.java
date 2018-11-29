package gargoyle.calendar.core;

import gargoyle.util.Interpolation;
import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public final class CalCore {

    private static final String PARAM_YEAR = "year";
    private final CalConfig config;

    public CalCore(CalConfig config) {
        this.config = config;
    }

    public void createWrite(@NotNull Resource write, @NotNull Year year, @NotNull Locale locale, @NotNull String formatName) throws IOException {
        BufferedImage image = createImage(year, locale);
        CalUtil.writeImage(image, write, formatName);
    }

    @NotNull
    private BufferedImage createImage(@NotNull Year year, @NotNull Locale locale) throws IOException {
        int canvasWidth = config.getCanvasWidth();
        int canvasHeight = config.getCanvasHeight();
        @NotNull BufferedImage canvas = CalUtil.createImage(canvasWidth, canvasHeight);
        printImage(canvas);
        printYearTitle(canvas, year);
        printYear(canvas, canvasHeight > canvasWidth, year, locale);
        return canvas;
    }

    private void printImage(@NotNull BufferedImage canvas) throws IOException {
        int canvasWidth = config.getCanvasWidth();
        int canvasHeight = config.getCanvasHeight();
        @NotNull BufferedImage original = CalUtil.readImage(config.getImageLocation());
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        @NotNull Rectangle2D inner = CalUtil.createRectangle(0, 0, originalWidth, originalHeight);
        @NotNull Rectangle2D area = CalUtil.fit(CalUtil.createRectangle(0, 0, canvasWidth, canvasHeight), inner);
        CalUtil.fill(canvas, config.getBackgroundColor(), area);
        double areaWidth = area.getWidth();
        double areaHeight = area.getHeight();
        //noinspection NumericCastThatLosesPrecision
        CalUtil.drawImage(canvas, area, CalUtil.scale(original, (int) areaWidth, (int) areaHeight));
    }

    private void printYearTitle(@NotNull BufferedImage canvas, @NotNull Year year) {
        int lineHeight = CalUtil.lineHeight(canvas, config.getDaysFont());
        int canvasWidth = canvas.getWidth();
        @NotNull Rectangle2D area = CalUtil.createRectangle(0, 0, canvasWidth, lineHeight);
        @NotNull String text = Interpolation.interpolate(config.getYearText(), Map.of(PARAM_YEAR, year));
        write(canvas, area, text, config.getYearFont(), config.getYearForeColor(), config.getYearBackColor(), true);
    }

    private void printYear(BufferedImage canvas, boolean portrait, @NotNull Year year, @NotNull Locale locale) {
        long yearLength = getLength(ChronoField.MONTH_OF_YEAR);
        double sqrt = Math.sqrt(yearLength);
        int floor = (int) Math.floor(sqrt);
        int ceil = (int) Math.ceil(sqrt);
        int monthsPerRow = portrait ? floor : ceil;
        int monthsPerCol = portrait ? ceil : floor;
        double monthWidth = (double) canvas.getWidth() / monthsPerRow;
        double yearLineHeight = CalUtil.lineHeight(canvas, config.getYearFont());
        double monthHeight = (canvas.getHeight() - yearLineHeight) / monthsPerCol;
        double dayHeight = CalUtil.lineHeight(canvas, config.getDaysFont());
        for (@NotNull Month month : Month.values()) {
            int monthIndex = month.ordinal();
            int col = monthIndex % monthsPerRow;
            int row = monthIndex / monthsPerRow;
            double x = col * monthWidth;
            double y = yearLineHeight + row * monthHeight;
            //noinspection ObjectAllocationInLoop
            @NotNull Rectangle2D area = CalUtil.createRectangle(x, y, monthWidth, monthHeight);
            //noinspection ObjectAllocationInLoop
            @NotNull YearMonth yearMonth = year.atMonth(month);
            printMonth(canvas, dayHeight, yearMonth, area, locale);
        }
    }

    private static void write(@NotNull BufferedImage canvas, @NotNull Rectangle2D area, @Nullable String text, @Nullable Font font, @Nullable Color foreColor, @Nullable Color backColor, boolean fill) {
        if (backColor != null) {
            @NotNull Rectangle2D fillArea = fill || text == null ? area : CalUtil.metricText(canvas, Objects.requireNonNull(font), area, text);
            CalUtil.fill(canvas, backColor, fillArea);
        }
        if (text != null) {
            CalUtil.drawText(canvas, text, Objects.requireNonNull(foreColor), Objects.requireNonNull(font), area);
        }
    }

    private static long getLength(TemporalField field) {
        ValueRange range = field.range();
        return range.getMaximum() - range.getMinimum() + 1;
    }

    private void printMonth(@NotNull BufferedImage canvas, double dayHeight, YearMonth yearMonth, Rectangle2D monthArea, @NotNull Locale locale) {
        int monthHeight = CalUtil.lineHeight(canvas, config.getMonthFont());
        double monthAreaX = monthArea.getX();
        double monthAreaY = monthArea.getY();
        double monthAreaWidth = monthArea.getWidth();
        @NotNull Rectangle2D area = CalUtil.createRectangle(monthAreaX, monthAreaY, monthAreaWidth, monthHeight);
        Month month = yearMonth.getMonth();
        printMonthTitle(canvas, monthArea, month, locale);
        double dayWidth = area.getWidth() / getLength(ChronoField.DAY_OF_WEEK);
        double areaX = area.getX();
        double weekdaysY = area.getY() + monthHeight;
        printWeekdays(canvas, areaX, weekdaysY, dayWidth, dayHeight, locale);
        double daysY = area.getY() + monthHeight + dayHeight;
        printBackDays(canvas, areaX, daysY, dayWidth, dayHeight);
        printDays(canvas, yearMonth, areaX, daysY, dayWidth, dayHeight);
    }

    private void printMonthTitle(@NotNull BufferedImage canvas, Rectangle2D monthArea, Month month, @NotNull Locale locale) {
        @NotNull String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, locale);
        int lineHeight = CalUtil.lineHeight(canvas, config.getDaysFont());
        double monthAreaX = monthArea.getX();
        double monthAreaY = monthArea.getY();
        double monthAreaWidth = monthArea.getWidth();
        @NotNull Rectangle2D area = CalUtil.createRectangle(monthAreaX, monthAreaY, monthAreaWidth, lineHeight);
        write(canvas, area, monthName, config.getMonthFont(), config.getMonthForeColor(), config.getMonthBackColor(), true);
    }

    private void printWeekdays(@NotNull BufferedImage canvas, double areaX, double areaY, double dayWidth, double dayHeight, @NotNull Locale locale) {
        Color backColor = config.getWeekdaysBackColor();
        for (@NotNull DayOfWeek dayOfWeek : DayOfWeek.values()) {
            int day = dayOfWeek.ordinal();
            double x = areaX + day * dayWidth;
            //noinspection ObjectAllocationInLoop
            @NotNull Rectangle2D dayArea = CalUtil.createRectangle(x, areaY, dayWidth, dayHeight);
            Color foreColor = isWeekend(dayOfWeek) ? config.getWeekdaysRedColor() : config.getWeekdaysForeColor();
            @NotNull String weekdayName = dayOfWeek.getDisplayName(TextStyle.SHORT, locale);
            write(canvas, dayArea, weekdayName, config.getWeekdaysFont(), foreColor, backColor, true);
        }
    }

    @SuppressWarnings("MethodWithMultipleLoops")
    private void printBackDays(@NotNull BufferedImage canvas, double areaX, double areaY, double dayWidth, double dayHeight) {
        Color backColor = config.getDaysBackColor();
        long weekLength = getLength(ChronoField.DAY_OF_WEEK);
        long l = getLength(ChronoField.ALIGNED_WEEK_OF_MONTH) / weekLength;
        for (int row = 0; row < l; row++) {
            for (int col = 0; col < weekLength; col++) {
                double x = areaX + col * dayWidth;
                double y = areaY + row * dayHeight;
                //noinspection ObjectAllocationInLoop
                @NotNull Rectangle2D.Double dayArea = CalUtil.createRectangle(x, y, dayWidth, dayHeight);
                write(canvas, dayArea, null, null, null, backColor, false);
            }
        }
    }

    private void printDays(@NotNull BufferedImage canvas, YearMonth yearMonth, double areaX, double areaY, double dayWidth, double dayHeight) {
        LocalDate firstDay = yearMonth.atEndOfMonth().with(TemporalAdjusters.firstDayOfMonth());
        DayOfWeek weekdayIndex = firstDay.getDayOfWeek();
        ValueRange range = firstDay.range(ChronoField.DAY_OF_MONTH);
        //noinspection NumericCastThatLosesPrecision
        int weekLength = (int) getLength(ChronoField.DAY_OF_WEEK);
        //noinspection NumericCastThatLosesPrecision
        int maximum = (int) range.getMaximum();
        //noinspection NumericCastThatLosesPrecision
        int minimum = (int) range.getMinimum();
        for (int day = minimum; day <= maximum; day++) {
            long idx = weekdayIndex.ordinal() + day - 1;
            long col = idx % weekLength;
            long row = idx / weekLength;
            double x = areaX + col * dayWidth;
            double y = areaY + row * dayHeight;
            //noinspection NestedMethodCall,ChainedMethodCall
            Color color = isWeekend(yearMonth.atDay(day).getDayOfWeek()) ? config.getDaysRedColor() : config.getDaysForeColor();
            //noinspection ObjectAllocationInLoop
            @NotNull Rectangle2D dayArea = CalUtil.createRectangle(x, y, dayWidth, dayHeight);
            write(canvas, dayArea, String.valueOf(day), config.getDaysFont(), color, null, true);
        }
    }

    private static boolean isWeekend(DayOfWeek day) {
        return day == DayOfWeek.SUNDAY;
    }

    @Override
    public String toString() {
        return String.format("CalCore{config=%s}", config);
    }
}
