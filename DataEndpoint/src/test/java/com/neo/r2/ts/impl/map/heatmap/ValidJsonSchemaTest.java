package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.common.impl.ResourceUtil;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

class ValidJsonSchemaTest {

    @Test
    void schemaValidityTest() {
        loadSchema(ResourceUtil.getFolderContent("schema"), "schema", "");
    }

    protected void loadSchema(File[] files, String jsonSchemaFolder, String currentPath) {
        for (File file: files) {
            if (file.isDirectory()) {
                loadSchema(file.listFiles(), jsonSchemaFolder ,currentPath + file.getName() + "/");
            } else {
                JsonSchemaUtil.generateSchemaFromResource(jsonSchemaFolder+ "/"+ currentPath + file.getName());
            }
        }
    }
}
