package org.lazy.web;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * For test only
  */
public class ResponseOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream byteArrayOutputStream;

    public ResponseOutputStream() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int b) throws IOException {
        byteArrayOutputStream.write(b);

    }

    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }
}
