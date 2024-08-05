package org.lazy.plugin;

import java.util.*;

public class LocalZipEntry {

    private String name;
    private byte[] bytes;

    public LocalZipEntry(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public LocalZipEntry(String name) {
        this(name, new byte[0]);
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalZipEntry that = (LocalZipEntry) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
