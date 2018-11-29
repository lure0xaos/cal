package gargoyle.util.load;

import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;


@SuppressWarnings("deprecation")
public final class DefaultLoaders extends Loaders {
    public static final DefaultLoaders INSTANCE = new DefaultLoaders();

    private DefaultLoaders() {
        addLoader(Font.class, DefaultLoaders::loadFont);
        addLoader(BufferedImage.class, DefaultLoaders::loadImage);
        addLoader(Image.class, DefaultLoaders::loadImage);
        addLoader(AudioClip.class, DefaultLoaders::loadAudioClip);
    }

    @NotNull
    private static AudioClip loadAudioClip(@NotNull Resource resource) {
        URL url = resource.getUrl();
        return Applet.newAudioClip(url);
    }

    @NotNull
    private static Font loadFont(@NotNull Resource resource) throws IOException {
        try (InputStream stream = resource.getInputStream()) {
            return Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (FontFormatException e) {
            throw new IOException(MessageFormat.format("cannot load font from {0}", resource), e);
        }
    }

    @NotNull
    private static BufferedImage loadImage(@NotNull Resource resource) throws IOException {
        try (InputStream stream = resource.getInputStream()) {
            return ImageIO.read(stream);
        }
    }
}
