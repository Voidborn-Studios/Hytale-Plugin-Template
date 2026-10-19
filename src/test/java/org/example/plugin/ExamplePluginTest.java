package org.example.plugin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExamplePluginTest {

    @Test
    @DisplayName("Verify template plugin class is present")
    void sanityCheck() {
        assertNotNull(ExamplePlugin.class);
    }
}
