package org.lazy.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.lazy.common.CommonProperties;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PackageArchiveGeneratorTest {

    private static final String ZIP_ENTRY = "properties-test.properties";
    private static final String ARCHIVE_PATH = "package-archive-generator-test";

    private PackageArchiveGenerator packageArchiveGenerator;

    @BeforeTest
    public void setUp() throws IOException {
        packageArchiveGenerator = PackageArchiveGenerator.getInstance();
        packageArchiveGenerator.addComponentDefinition(
                getClass().getClassLoader().getResourceAsStream(CommonProperties.COMPONENT_DEFINITIONS.getName()));
        packageArchiveGenerator.addZipEntry(new LocalZipEntry(ZIP_ENTRY, getClass().getClassLoader()
                .getResourceAsStream(CommonProperties.COMPONENT_DEFINITIONS.getName()).readAllBytes()));
    }

    @Test
    void testGenerateArchiveFile() throws IOException {
        String targetAbsolutePath = getClass().getResource("/").getPath();
        packageArchiveGenerator.generateArchiveFile(targetAbsolutePath + ARCHIVE_PATH, ArchiveType.JAR);

        JarFile archive = new JarFile(new File(targetAbsolutePath + ARCHIVE_PATH + "." + ArchiveType.JAR.getType()));

        assertNotNull(archive);
        assertNotNull(archive.getEntry(ZIP_ENTRY));
        assertNotNull(archive.getEntry(CommonProperties.LAZY_APPLICATION_JSON.getName()));
        assertNotNull(archive.getEntry(CommonProperties.LAZY_APPLICATION_PROPERTIES.getName()));
        assertEquals(archive.getManifest().getMainAttributes().get(Attributes.Name.MAIN_CLASS),
                "org.lazy.app.MainApplication");
    }
}
