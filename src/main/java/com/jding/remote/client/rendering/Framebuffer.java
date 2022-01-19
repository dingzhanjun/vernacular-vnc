package com.jding.remote.client.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.jding.remote.client.VncSession;
import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.client.rendering.renderers.*;
import com.jding.remote.protocol.messages.*;
import com.jding.remote.protocol.messages.Rectangle;

import static com.jding.remote.protocol.messages.Encoding.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Framebuffer {

    private final VncSession session;
    private final Map<BigInteger, ColorMapEntry> colorMap = new ConcurrentHashMap<>();
    private final Map<Encoding, Renderer> renderers = new ConcurrentHashMap<>();
    private final CursorRenderer cursorRenderer;

    private BufferedImage frame;

    public Framebuffer(VncSession session) {
        PixelDecoder pixelDecoder = new PixelDecoder(colorMap);
        RawRenderer rawRenderer = new RawRenderer(pixelDecoder, session.getPixelFormat());
        renderers.put(RAW, rawRenderer);
        renderers.put(COPYRECT, new CopyRectRenderer());
        renderers.put(RRE, new RRERenderer(pixelDecoder, session.getPixelFormat()));
        renderers.put(HEXTILE, new HextileRenderer(rawRenderer, pixelDecoder, session.getPixelFormat()));
        renderers.put(ZLIB, new ZLibRenderer(rawRenderer));
        cursorRenderer = new CursorRenderer(rawRenderer);

        frame = new BufferedImage(session.getFramebufferWidth(), session.getFramebufferHeight(), TYPE_INT_RGB);
        this.session = session;
    }

    public void processUpdate(FramebufferUpdate update) throws RemoteException {
        InputStream in = session.getInputStream();
        try {
            for (int i = 0; i < update.getNumberOfRectangles(); i++) {
                Rectangle rectangle = Rectangle.decode(in);
                if (rectangle.getEncoding() == DESKTOP_SIZE) {
                    resizeFramebuffer(rectangle);
                } else if (rectangle.getEncoding() == CURSOR) {
                    updateCursor(rectangle, in);
                } else {
                    renderers.get(rectangle.getEncoding()).render(in, frame, rectangle);
                }
            }
            paint();
            session.framebufferUpdated();
        } catch (IOException e) {
            throw new UnexpectedRemoteException(e);
        }
    }

    private void paint() {
        Consumer<Image> listener = session.getConfig().getScreenUpdateListener();
        if (listener != null) {
            ColorModel colorModel = frame.getColorModel();
            WritableRaster raster = frame.copyData(null);
            BufferedImage copy = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
            listener.accept(copy);
        }
    }

    public void updateColorMap(SetColorMapEntries update) {
        for (int i = 0; i < update.getColors().size(); i++) {
            colorMap.put(BigInteger.valueOf(i + update.getFirstColor()), update.getColors().get(i));
        }
    }

    private void resizeFramebuffer(Rectangle newSize) {
        int width = newSize.getWidth();
        int height = newSize.getHeight();
        session.setFramebufferWidth(width);
        session.setFramebufferHeight(height);
        BufferedImage resized = new BufferedImage(width, height, TYPE_INT_RGB);
        resized.getGraphics().drawImage(frame, 0, 0, null);
        frame = resized;
    }

    private void updateCursor(Rectangle cursor, InputStream in) throws RemoteException {
        if (cursor.getWidth() > 0 && cursor.getHeight() > 0) {
            BufferedImage cursorImage = new BufferedImage(cursor.getWidth(), cursor.getHeight(), TYPE_INT_ARGB);
            cursorRenderer.render(in, cursorImage, cursor);
            BiConsumer<Image, Point> listener = session.getConfig().getMousePointerUpdateListener();
            if (listener != null) {
                listener.accept(cursorImage, new Point(cursor.getX(), cursor.getY()));
            }
        }
    }

}
