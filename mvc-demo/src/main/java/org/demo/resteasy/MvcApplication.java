package org.demo.resteasy;

import jakarta.mvc.engine.ViewEngine;
import jakarta.mvc.form.FormMethodOverwriter;
import jakarta.mvc.form.FormMethodOverwriter.Options;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.krazo.Properties;

/**
 * @author jacksparrow414
 * @date 2023/6/17
 */
@ApplicationPath("mvc")
public class MvcApplication extends Application {
    
    /**
     * 配置文件 https://eclipse-ee4j.github.io/krazo/documentation/latest/index.html#_properties_default_view_file_extension_org_eclipse_krazo_defaultviewfileextension
     * @return
     */
    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = new HashMap<>();
        // 设置页面文件夹
        properties.put(ViewEngine.DEFAULT_VIEW_FOLDER, "/WEB-INF/views/");
        properties.put(Properties.DEFAULT_VIEW_FILE_EXTENSION, "jsp");
        // 设置form表单的method属性，允许除了get和post之外的其他请求
        properties.put(FormMethodOverwriter.FORM_METHOD_OVERWRITE, Options.ENABLED);
        properties.put(FormMethodOverwriter.HIDDEN_FIELD_NAME, FormMethodOverwriter.DEFAULT_HIDDEN_FIELD_NAME);
        return properties;
    }
}
