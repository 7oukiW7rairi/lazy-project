package org.lazy.plugin;

import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentType;
import org.objectweb.asm.ClassReader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ApplicationFilesGeneratorTest {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    @Test
    public void testGenerateComponentDefinitionFile() throws URISyntaxException, IOException {
        ApplicationFilesGenerator applicationFilesGenerator = ApplicationFilesGenerator.getInstance();
        Path path = Paths.get(getClass().getResource("/TestEntityRepository.class").toURI());
        ClassReader classReader = new ClassReader(path.toUri().toURL().openStream());
        String targetFolder = path.getParent().getParent().toString();
        /*applicationFilesGenerator.generateComponentDefinitionFile(Collections.singletonList(classReader), targetFolder, FILE_SEPARATOR);

        Map<String, BaseComponentDefinition> definitionMap = applicationFilesGenerator.readComponentDefinitions(targetFolder + FILE_SEPARATOR + "classes" + FILE_SEPARATOR);
        BaseComponentDefinition definition = definitionMap.get("org.lazy.TestEntityRepository");
        assertNotNull(definition);
        assertEquals(definition.getComponentClassName(), "org.lazy.TestEntityRepositoryImpl");
        assertEquals(definition.getComponentType(), ComponentType.SINGLETON);
        assertEquals(definition.getDependencies(), Arrays.asList("javax.persistence.EntityManager", "org.lazy.jpa.LocalTransactionManager"));*/
    }
}