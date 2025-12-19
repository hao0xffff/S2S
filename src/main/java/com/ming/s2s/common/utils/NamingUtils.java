package com.ming.s2s.common.utils;

import org.apache.commons.lang3.text.WordUtils;

public class NamingUtils {

    /**
     * 下划线转大驼峰 (用于类名: sys_user -> SysUser)
     */
    public static String toClassName(String text) {
        if (text == null || text.isEmpty()) return "";
        // 移除反引号并处理下划线
        String cleanText = text.replace("`", "");
        return WordUtils.capitalizeFully(cleanText, '_').replace("_", "");
    }

    /**
     * 下划线转小驼峰 (用于属性名: user_name -> userName)
     */
    public static String toPropertyName(String text) {
        String className = toClassName(text);
        if (className.isEmpty()) return "";
        // 首字母小写
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }
}