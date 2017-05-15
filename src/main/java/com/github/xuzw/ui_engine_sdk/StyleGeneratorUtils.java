package com.github.xuzw.ui_engine_sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.github.xuzw.ui_engine_runtime.annotation.StyleAnnotation;
import com.github.xuzw.ui_engine_runtime.annotation.StyleBlockAnnotation;
import com.github.xuzw.ui_engine_runtime.annotation.StyleDeclarationAnnotation;
import com.github.xuzw.ui_engine_runtime.div.Div;
import com.github.xuzw.ui_engine_runtime.div.location.ClassName;
import com.github.xuzw.ui_engine_runtime.style.Block;
import com.github.xuzw.ui_engine_runtime.style.Declaration;
import com.github.xuzw.ui_engine_runtime.style.Style;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.SubclassMatchProcessor;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年5月11日 上午11:37:34
 */
public class StyleGeneratorUtils {
    private static final Charset encoding = Charset.forName("utf8");
    private static final Map<String, Style> map = new HashMap<>();

    public static void updateCssFiles(String folder, String... scanSpec) throws IOException {
        // ----
        new FastClasspathScanner(scanSpec).matchSubclassesOf(Div.class, new SubclassMatchProcessor<Div>() {
            @Override
            public void processMatch(Class<? extends Div> divClass) {
                StyleAnnotation styleAnnotation = divClass.getDeclaredAnnotation(StyleAnnotation.class);
                if (styleAnnotation == null) {
                    return;
                }
                if (styleAnnotation.value().length == 0) {
                    styleAnnotation = getStyleAnnotationFromSuperClass(divClass);
                }
                String name = styleAnnotation.name();
                if (!map.containsKey(name)) {
                    Style style = new Style();
                    style.setBlocks(new ArrayList<>());
                    map.put(name, style);
                }
                for (StyleBlockAnnotation styleBlockAnnotation : styleAnnotation.value()) {
                    Block block = new Block();
                    block.setDeclarations(new ArrayList<>());
                    block.setSelector(ClassName.selector(divClass, styleBlockAnnotation.selector()));
                    for (StyleDeclarationAnnotation styleDeclarationAnnotation : styleBlockAnnotation.value()) {
                        block.getDeclarations().add(new Declaration(styleDeclarationAnnotation.property(), styleDeclarationAnnotation.value()));
                    }
                    map.get(name).getBlocks().add(block);
                }
            }
        }).scan();
        // ----
        for (String name : map.keySet()) {
            Style style = map.get(name);
            updateCssFile(folder, name, style);
        }
    }

    private static void updateCssFile(String folder, String name, Style style) throws IOException {
        File file = new File(folder, name + ".css");
        file.getParentFile().mkdirs();
        OutputStream output = new FileOutputStream(file);
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("/* GenerateBy UiEngineSdk %s */", _buildTime())).append("\n");
        sb.append(style.toString());
        IOUtils.write(sb.toString(), output, encoding);
        IOUtils.closeQuietly(output);
    }

    private static String _buildTime() {
        return new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS").format(System.currentTimeMillis());
    }

    private static StyleAnnotation getStyleAnnotationFromSuperClass(Class<?> divClass) {
        Class<?> superClass = divClass.getSuperclass();
        StyleAnnotation styleAnnotation = superClass.getDeclaredAnnotation(StyleAnnotation.class);
        if (styleAnnotation == null || styleAnnotation.value().length == 0) {
            return getStyleAnnotationFromSuperClass(superClass);
        }
        return styleAnnotation;
    }
}
