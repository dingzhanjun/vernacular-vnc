package com.jding.remote.client.rendering.renderers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.PixelFormat;
import com.jding.remote.protocol.messages.Rectangle;

public class RawRenderer implements Renderer {

    private final PixelDecoder pixelDecoder;
    private final PixelFormat pixelFormat;

    public RawRenderer(PixelDecoder pixelDecoder, PixelFormat pixelFormat) {
        this.pixelDecoder = pixelDecoder;
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws RemoteException {
        render(in, destination, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    void render(InputStream in, BufferedImage destination, int x, int y, int width, int height) throws RemoteException {
        try {
            int sx = x;
            int sy = y;
            for (int i = 0; i < width * height; i++) {
                Pixel pixel = pixelDecoder.decode(in, pixelFormat);
                destination.setRGB(sx, sy, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()).getRGB());
                sx++;
                if (sx == x + width) {
                    sx = x;
                    sy++;
                }
            }
        } catch (IOException e) {
            throw new UnexpectedRemoteException(e);
        }
    }

}
