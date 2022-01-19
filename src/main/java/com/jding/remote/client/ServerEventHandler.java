package com.jding.remote.client;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.function.Consumer;

import com.jding.remote.client.exceptions.UnexpectedRemoteException;
import com.jding.remote.client.exceptions.UnknownMessageTypeException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.client.rendering.Framebuffer;
import com.jding.remote.protocol.messages.Bell;
import com.jding.remote.protocol.messages.FramebufferUpdate;
import com.jding.remote.protocol.messages.ServerCutText;
import com.jding.remote.protocol.messages.SetColorMapEntries;

public class ServerEventHandler {

    private final VncSession session;
    private final Consumer<RemoteException> errorHandler;
    private final Framebuffer framebuffer;

    private volatile boolean running;
    private Thread eventLoop;

    ServerEventHandler(VncSession session, Consumer<RemoteException> errorHandler) {
        this.session = session;
        this.errorHandler = errorHandler;
        this.framebuffer = new Framebuffer(session);
    }

    void start() {
        PushbackInputStream in = new PushbackInputStream(session.getInputStream());

        running = true;

        eventLoop = new Thread(() -> {
            try {
                int messageType;
                while (running && ((messageType = in.read()) != -1)) {
                    in.unread(messageType);

                    switch (messageType) {
                        case 0x00:
                            FramebufferUpdate framebufferUpdate = FramebufferUpdate.decode(in);
                            framebuffer.processUpdate(framebufferUpdate);
                            break;
                        case 0x01:
                            SetColorMapEntries setColorMapEntries = SetColorMapEntries.decode(in);
                            framebuffer.updateColorMap(setColorMapEntries);
                            break;
                        case 0x02:
                            Bell.decode(in);
                            Consumer<Void> bellListener = session.getConfig().getBellListener();
                            if (bellListener != null) {
                                bellListener.accept(null);
                            }
                            break;
                        case 0x03:
                            ServerCutText cutText = ServerCutText.decode(in);
                            Consumer<String> cutTextListener = session.getConfig().getRemoteClipboardListener();
                            if (cutTextListener != null) {
                                cutTextListener.accept(cutText.getText());
                            }
                            break;
                        default:
                            throw new UnknownMessageTypeException(messageType);
                    }
                }
            } catch (IOException e) {
                if (running) {
                    errorHandler.accept(new UnexpectedRemoteException(e));
                }
            } catch (RemoteException e) {
                if (running) {
                    errorHandler.accept(e);
                }
            } finally {
                running = false;
            }
        });

        eventLoop.start();
    }

    void stop() {
        running = false;
        try {
            if (eventLoop != null) {
                eventLoop.join(1000);
            }
        } catch (InterruptedException ignored) {
        }
    }

}
