package com.jding.remote.client.rendering.renderers;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.Rectangle;

public class ZLibRenderer implements Renderer {

    private final RawRenderer rawRenderer;
    private final Inflater inflater;

    public ZLibRenderer(RawRenderer rawRenderer) {
        this.rawRenderer = rawRenderer;
        this.inflater = new Inflater();
    }

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws RemoteException {
        try {
            DataInput dataInput = new DataInputStream(in);
            int compressedLength = dataInput.readInt();
            byte[] compressedData = new byte[compressedLength];
            dataInput.readFully(compressedData);
            inflater.setInput(compressedData);

            int read;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((read = inflater.inflate(buffer)) != 0) {
                baos.write(buffer, 0, read);
            }

            byte[] decompressedData = baos.toByteArray();

            rawRenderer.render(
                    new ByteArrayInputStream(decompressedData),
                    destination,
                    rectangle.getX(),
                    rectangle.getY(),
                    rectangle.getWidth(),
                    rectangle.getHeight());

        } catch (IOException | DataFormatException e) {
            throw new UnexpectedRemoteException(e);
        }
    }
}
