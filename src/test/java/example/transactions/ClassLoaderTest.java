package example.transactions;

import org.junit.jupiter.api.Test;
import example.transactions.examples.java.core.ClassLoaderExample;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ClassLoaderTest {
    @Test
    void printClassLoaders() throws ClassNotFoundException {
        ClassLoaderExample.printClassLoaders();
        // Platform Classloader:jdk.internal.loader.ClassLoaders$PlatformClassLoader@38234a38

        // System Classloader:jdk.internal.loader.ClassLoaders$AppClassLoader@2c854dc5

        // Classloader of this class:jdk.internal.loader.ClassLoaders$AppClassLoader@2c854dc5

        // Classloader of
        // DriverManager:jdk.internal.loader.ClassLoaders$PlatformClassLoader@38234a38

        // Classloader of ArrayList:null
    }
}
