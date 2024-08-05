package org.lazy.plugin;

import org.lazy.common.CommonProperties;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackageArchiveGenerator {

    private static final String JAR = "jar";
    private static PackageArchiveGenerator INSTANCE;

    private Set<LocalZipEntry> zipEntries = new HashSet<>();
    private final ApplicationFilesGenerator applicationFilesGenerator = ApplicationFilesGenerator.getInstance();

    public PackageArchiveGenerator() {
    }

    public static PackageArchiveGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PackageArchiveGenerator();
        }
        return INSTANCE;
    }

    public void addZipEntry(LocalZipEntry localZipEntry) {
        this.zipEntries.add(localZipEntry);
    }

    public void addComponentDefinition(InputStream inputStream) {
        this.applicationFilesGenerator.addComponentDefinitions(inputStream);
    }

    // TODO chnage it to generatePackage to use it to generate jar and war 
    public void generateArchiveFile(String name, ArchiveType archiveType) throws IOException {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(name + "." + archiveType.getType()));
            String entryPath = ArchiveType.JAR == archiveType ? "" : "WEB-INF/classes/";
            addManifest(zipOutputStream, archiveType);
            writeStringToZipOutputStream(zipOutputStream,
                entryPath + CommonProperties.LAZY_APPLICATION_JSON.getName(),
                applicationFilesGenerator.generateLazyApplicationJson());
            writeStringToZipOutputStream(zipOutputStream,
                entryPath + CommonProperties.LAZY_APPLICATION_PROPERTIES.getName(),
                applicationFilesGenerator.generateLazyApplicationProperties());
            for (LocalZipEntry localZipEntry : zipEntries) {
                zipOutputStream.putNextEntry(new ZipEntry(localZipEntry.getName()));
                if (localZipEntry.getBytes().length > 0) {
                    zipOutputStream.write(localZipEntry.getBytes());
                }
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
    }

    private void addManifest(ZipOutputStream zipOutputStream, ArchiveType archiveType) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (ArchiveType.JAR == archiveType) {
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, applicationFilesGenerator.getApplicationMainClass());
        }
        zipOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));
        manifest.write(new BufferedOutputStream(zipOutputStream));
        zipOutputStream.closeEntry();
    }

    private void writeStringToZipOutputStream(ZipOutputStream zipOutputStream, String entryName, String source) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(entryName));
        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        zipOutputStream.write(bytes, 0, bytes.length);
        zipOutputStream.closeEntry();
    }
}
