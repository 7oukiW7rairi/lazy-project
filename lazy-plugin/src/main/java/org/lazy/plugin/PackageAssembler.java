package org.lazy.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.lazy.common.CommonProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PackageAssembler {

    private static final Logger logger = LoggerFactory.getLogger(PackageAssembler.class);

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String ZIP_ENTRY_SEPARATOR = System.getProperty("file.separator");
    private static final String WEB_INF = "WEB-INF";
    private static final String WEB_XML = "web.xml";
    private static final String CLASSES = "classes";
    private static final String WEBAPP_FOLDER = "webapp";

    private static PackageAssembler INSTANCE;

    private PackageAssembler() {
    }

    public static PackageAssembler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PackageAssembler();
        }
        return INSTANCE;
    }


    public void assembleWar(File target, String outputName) throws MojoExecutionException {
        try {
            File outputFolder = getSubFile(target, outputName);
            String webInfPath = outputFolder.getAbsolutePath() + FILE_SEPARATOR + WEB_INF;
            String classPath = webInfPath + FILE_SEPARATOR + CLASSES + FILE_SEPARATOR;
            // TODO use Files.walk to move the contents of the folder
            Files.walk(new File(classPath + WEBAPP_FOLDER).toPath())
                    .sorted(Comparator.comparing(Path::toString))
                    .filter(path -> !path.toString().endsWith(WEBAPP_FOLDER))
                    .forEach(path -> {
                        try {
                            String relativePath = Paths.get(classPath + WEBAPP_FOLDER).relativize(path).toString();
                            Path outputPath = Paths.get(outputFolder.getAbsolutePath(), relativePath);
                            if (path.toFile().isDirectory()) {
                                Files.createDirectory(outputPath);
                            } else {
                                Files.move(path, outputPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            
            PackageArchiveGenerator packageArchiveGenerator = PackageArchiveGenerator.getInstance();
            scanFolderAndAddArchiveEntries(outputFolder.getAbsolutePath(), packageArchiveGenerator);

            Files.list(getSubFile(target, "libs").toPath())
                    .filter(path -> path.endsWith(".jar")).forEach(path -> {
                try (JarFile archive = new JarFile(path.toFile())) {
                    List<JarEntry> entries = archive.stream()
                            .sorted(Comparator.comparing(JarEntry::getName))
                            .collect(Collectors.toList());
                    for (JarEntry entry : entries) {
                        if (entry.getName().endsWith(CommonProperties.COMPONENT_DEFINITIONS.getName())) {
                            packageArchiveGenerator.addComponentDefinition(archive.getInputStream(entry));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            packageArchiveGenerator.addZipEntry(new LocalZipEntry(WEB_INF + ZIP_ENTRY_SEPARATOR + WEB_XML,
                    getClass().getClassLoader().getResource(WEB_XML).openStream().readAllBytes()));
            packageArchiveGenerator.generateArchiveFile(target.getAbsolutePath() + FILE_SEPARATOR + outputName + "-extended", ArchiveType.WAR);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    public void assembleJar(File target, String outputName) throws MojoExecutionException {
        try {
            String folderPath = target.getAbsolutePath() + FILE_SEPARATOR + "classes";
            PackageArchiveGenerator packageArchiveGenerator = PackageArchiveGenerator.getInstance();

            scanFolderAndAddArchiveEntries(folderPath, packageArchiveGenerator);

            Set<String> jarEntries = new HashSet<>();
            File libsFolder = getSubFile(target, "libs");
            List<Path> libs = Files.list(libsFolder.toPath())
                    //.filter(path -> path.endsWith(".jar"))
                    .collect(Collectors.toList());
            for (Path path : libs) {
                try (JarFile archive = new JarFile(path.toFile())) {
                    List<JarEntry> entries = archive.stream()
                            .sorted(Comparator.comparing(JarEntry::getName))
                            .collect(Collectors.toList());
                    for (JarEntry entry : entries) {
                        if (entry.getName().endsWith(CommonProperties.COMPONENT_DEFINITIONS.getName())) {
                            packageArchiveGenerator.addComponentDefinition(archive.getInputStream(entry));
                        } else if (JarFile.MANIFEST_NAME.equals(entry.getName())) {
                            //return;
                        } else if (jarEntries.add(entry.getName())) {
                            packageArchiveGenerator.addZipEntry(new LocalZipEntry(entry.getName(), archive.getInputStream(entry).readAllBytes()));
                        }
                    }
                }
            }

            packageArchiveGenerator.generateArchiveFile(target.getAbsolutePath() + FILE_SEPARATOR + outputName + "-extended", ArchiveType.JAR);
        } catch (IOException e) {
            e.printStackTrace();
            //throw new MojoExecutionException(e.getMessage());
        }
    }

    private File getSubFile(File folder, String subFileName) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> file.getName().equals(subFileName))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    private void scanFolderAndAddArchiveEntries(String folderPath, PackageArchiveGenerator packageArchiveGenerator) throws IOException {
        List<Path> paths = Files.walk(new File(folderPath).toPath())
                    .sorted(Comparator.comparing(Path::toString))
                    .filter(path -> !path.equals(folderPath)).collect(Collectors.toList());
            for (Path path : paths) {
                if (path.endsWith(CommonProperties.COMPONENT_DEFINITIONS.getName())) {
                    packageArchiveGenerator.addComponentDefinition(new FileInputStream(path.toFile()));
                } else {
                    // TODO convert file_separator to zip_separator if different (to confirm)
                    String relativePath = Paths.get(folderPath).relativize(path).toString();
                    if (path.toFile().isDirectory()) {
                        packageArchiveGenerator.addZipEntry(new LocalZipEntry(relativePath.endsWith(ZIP_ENTRY_SEPARATOR) ? relativePath : relativePath + ZIP_ENTRY_SEPARATOR));
                    } else {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            packageArchiveGenerator.addZipEntry(new LocalZipEntry(relativePath, inputStream.readAllBytes()));
                        }
                    }
                }
            }
    }
}
