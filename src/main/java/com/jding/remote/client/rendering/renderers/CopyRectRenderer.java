package com.jding.remote.client.rendering.renderers;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.Rectangle;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class CopyRectRenderer implements Renderer {

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws RemoteException {
        try {
            DataInput dataInput = new DataInputStream(in);
            int srcX = dataInput.readUnsignedShort();
            int srcY = dataInput.readUnsignedShort();
            BufferedImage src = new BufferedImage(rectangle.getWidth(), rectangle.getHeight(), TYPE_INT_RGB);
            destination.getSubimage(srcX, srcY, rectangle.getWidth(), rectangle.getHeight()).copyData(src.getRaster());
            destination.getGraphics().drawImage(src, rectangle.getX(), rectangle.getY(), null);
        } catch (IOException e) {
            throw new UnexpectedRemoteException(e);
        }
    }
}
