package org.lazy.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * For test purpose
 */
public class RequestInputStream extends ServletInputStream {

    private ByteArrayInputStream byteArrayInputStream;

    public RequestInputStream(byte[] bytes) {
        super();
        this.byteArrayInputStream = new ByteArrayInputStream(bytes);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}
