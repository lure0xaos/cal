package gargoyle.calendar.core;

import gargoyle.util.load.DefaultLoaders;
import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Year;


@SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "HardCodedStringLiteral"})
public final class CalUtil {
    private CalUtil() {
    }

    @NotNull
    @Contract("_, _ -> new")
    public static BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static void drawImage(@NotNull BufferedImage canvas, @NotNull Rectangle2D area, @NotNull BufferedImage image) {
        double areaX = area.getX();
        double dx2 = areaX + area.getWidth();
        double areaY = area.getY();
        double dy2 = areaY + area.getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Graphics g = canvas.getGraphics();
        //noinspection NumericCastThatLosesPrecision
        g.drawImage(image, (int) areaX, (int) areaY, (int) dx2, (int) dy2, 0, 0, imageWidth, imageHeight, null);
    }

    public static void drawText(@NotNull BufferedImage canvas, @NotNull String text, @NotNull Color color, @NotNull Font font, @NotNull Rectangle2D area) {
        Graphics g = canvas.getGraphics();
        Color savedColor = g.getColor();
        Font gFont = g.getFont();
        Shape clip = g.getClip();
        g.setColor(color);
        g.setFont(font);
        g.setClip(area);
        FontMetrics fm = g.getFontMetrics(font);
        double x = area.getX() + (area.getWidth() - fm.stringWidth(text)) / 2;
        double lineHeight = lineHeight(canvas, font);

        double y = area.getY() + (area.getHeight() == 0 ? lineHeight : (area.getHeight() - lineHeight) / 2) + fm.getAscent();
        //noinspection NumericCastThatLosesPrecision
        g.drawString(text, (int) x, (int) y);
        g.setFont(gFont);
        g.setColor(savedColor);
        g.setClip(clip);
    }

    public static int lineHeight(@NotNull BufferedImage canvas, @NotNull Font font) {
        Graphics g = canvas.getGraphics();
        FontMetrics fm = g.getFontMetrics(font);
        return fm.getHeight();
    }

    public static void fill(@NotNull BufferedImage canvas, @NotNull Color color, @NotNull Rectangle2D area) {
        Graphics g = canvas.getGraphics();
        Color gColor = g.getColor();
        g.setColor(color);
        double areaX = area.getX();
        double areaY = area.getY();
        double areaWidth = area.getWidth();
        double areaHeight = area.getHeight();
        //noinspection NumericCastThatLosesPrecision
        g.fillRect((int) areaX, (int) areaY, (int) areaWidth, (int) areaHeight);
        g.setColor(gColor);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Rectangle2D fit(@NotNull Rectangle2D outer, @NotNull Rectangle2D inner) {
        double width;
        double height;
        if (inner.getWidth() > inner.getHeight()) {
            width = outer.getWidth();
            height = outer.getHeight() / outer.getWidth() * width;
        } else {
            height = outer.getHeight();
            width = outer.getWidth() / outer.getHeight() * height;
        }
        double x = outer.getX() + (outer.getWidth() - width) / 2;
        double y = outer.getY() + (outer.getHeight() - height) / 2;
        return createRectangle(x, y, width, height);
    }

    @NotNull
    public static Year getCurrentYear() {
        return Year.now();
    }

    @NotNull
    public static Rectangle2D metricText(@NotNull BufferedImage canvas, @NotNull Font font, @NotNull Rectangle2D area, @NotNull String text) {
        Graphics g = canvas.getGraphics();
        FontMetrics fm = g.getFontMetrics(font);
        Rectangle2D bounds = fm.getStringBounds(text, g);
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double x = area.getX() + (area.getWidth() - width) / 2;
        double y = area.getY() + (area.getHeight() == 0 ? height : (area.getHeight() - height) / 2);
        return createRectangle(x, y, width, height);
    }

    @NotNull
    public static BufferedImage readImage(@NotNull Resource image) throws IOException {
        return DefaultLoaders.INSTANCE.load(BufferedImage.class, image);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static BufferedImage scale(@NotNull BufferedImage src, int width, int height) {
        int type = src.getType();
        @NotNull BufferedImage image = new BufferedImage(width, height, type);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Graphics g = image.getGraphics();
        g.drawImage(src, 0, 0, imageWidth, imageHeight, 0, 0, srcWidth, srcHeight, null);
        return image;
    }

    public static void writeImage(@NotNull RenderedImage image, @NotNull Resource resource, @NotNull String formatName) throws IOException {
        try (OutputStream stream = resource.getOutputStream()) {
            ImageIO.write(image, formatName, stream);
        }
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Rectangle2D.Double createRectangle(double x, double y, double width, double height) {
        return new Rectangle2D.Double(x, y, width, height);
    }
}
