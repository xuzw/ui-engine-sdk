package com.github.xuzw.ui_engine_sdk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import com.github.xuzw.ui_engine_runtime.UiEngine;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年5月15日 下午12:08:23
 */
public class ScriptGeneratorUtils {
    private static final Charset encoding = Charset.forName("utf8");

    public static void updateScriptFile(String path) throws IOException {
        OutputStream output = new FileOutputStream(path);
        IOUtils.write(String.format("/* GenerateBy UiEngineSdk %s */\n", _buildTime()), output, encoding);
        InputStream input = UiEngine.class.getResourceAsStream("/com/github/xuzw/ui_engine_runtime/script/ui-engine.js");
        IOUtils.copy(input, output);
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
    }

    private static String _buildTime() {
        return new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS").format(System.currentTimeMillis());
    }
}
