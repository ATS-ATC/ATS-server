package com.alucn.weblab.utils;

public class jsonUtil {

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
    public static String formatJson(String s) {
        //json 字符串
        /*
         String s = "{\"code\":10000,\"msg\":null,\"data\":{\"id\":\"7aa0eb56-1026-4497-a42e-4c39f5e3dcf1\",\"topicId\":\"0876ab84-a478-417b-91bc-849843c191a5\",\"title\":null,\"commentId\":null,\"content\":\"" +
                "开发者平台自动化测试：针对帖子发表评论" +
                "\",\"images\":\"\",\"time\":\"2017-10-15 18:09:56\",\"userId\":\"61028f94-de92-4c65-aad3-2fc8614e1d34\",\"userName\":\"devautotest\",\"commentNum\":0,\"status\":0}}";
        */
    	int level = 0;
        //存放格式化的json字符串
        StringBuffer jsonForMatStr = new StringBuffer();
        for(int index=0;index<s.length();index++)//将字符串中的字符逐个按行输出
        {
            //获取s中的每个字符
            char c = s.charAt(index);
            //System.out.println(s.charAt(index));
             
            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
                //System.out.println("123"+jsonForMatStr);
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            switch (c) {
            case '{':
            case '[':
                jsonForMatStr.append(c + "\n");
                level++;
                break;
            case ',':
                jsonForMatStr.append(c + "\n");            
                break;
            case '}':
            case ']':
                jsonForMatStr.append("\n");
                level--;
                jsonForMatStr.append(getLevelStr(level));
                jsonForMatStr.append(c);
                break;
            default:
                jsonForMatStr.append(c);
                break;
            }
        }
        //System.out.println(jsonForMatStr);
        return jsonForMatStr.toString();
    }
}
