package com.powerboot.common;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCommonUtils {

    private static final String MATCH_PATTERN = "\\{[^}]*\\}";

    /**
     * 替换参数
     * @param content
     * @param params
     * @return
     */
    public static String buildString(String content, Object... params){
        Pattern r = Pattern.compile(MATCH_PATTERN);
        Matcher m = r.matcher(content);
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while(m.find()){
            m.appendReplacement(sb, params[count].toString());
            count++;
        }
        m.appendTail(sb);
        int argsLength = params.length;
        if(count!=argsLength){
            throw new IllegalArgumentException("字符串占位符个数和传入的参数数量不相等");
        }
        return sb.toString();
    }
}
