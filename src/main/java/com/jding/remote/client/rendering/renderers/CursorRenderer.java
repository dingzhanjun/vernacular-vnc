package com.jding.remote.client.rendering.renderers;

import static com.jding.remote.utils.ByteUtils.bitAt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.Rectangle;

public class CursorRenderer implements Renderer {

    private static final int TRANSPARENT = new Color(0.0f, 0.0f, 0.0f, 0.0f).getRGB();

    private final RawRenderer rawRenderer;

    public CursorRenderer(RawRenderer rawRenderer) {
        this.rawRenderer = rawRenderer;
    }

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws RemoteException {
        try {
            rawRenderer.render(in, destination, 0, 0, rectangle.getWidth(), rectangle.getHeight());

            byte[] bitmask = new byte[((rectangle.getWidth() + 7) / 8) * rectangle.getHeight()];
            new DataInputStream(in).readFully(bitmask);

            int x = 0;
            int y = 0;

            for (byte b : bitmask) {
                for (int i = 7; i >= 0; i--) {
                    boolean visible = bitAt(b, i);
                    if (!visible) {
                        destination.setRGB(x, y, TRANSPARENT);
                    }
                    if (++x == rectangle.getWidth()) {
                        x = 0;
                        y++;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new UnexpectedRemoteException(e);
        }
    }
}
