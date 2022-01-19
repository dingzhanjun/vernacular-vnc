package com.jding.remote.client.rendering.renderers;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.Rectangle;

public interface Renderer {
    void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws RemoteException;
}
