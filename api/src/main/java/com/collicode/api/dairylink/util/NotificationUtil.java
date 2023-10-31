package com.collicode.api.dairylink.util;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationUtil {
    private static final String TEMPLATE_REGEX = "\\$\\w+";

    public static String generateContentFromTemplate(String template, Map<String, Object> replacementValues) {
        if (template == null) {
            return null;
        }

        Set<String> templateParams = getTemplateParams(template);

        System.out.println("replacement: " + templateParams);

        return replaceTemplateParams(template, templateParams, replacementValues);
    }

    public static Set<String> getTemplateParams(String template) {

        Pattern pattern = Pattern.compile(TEMPLATE_REGEX);

        Matcher matcher = pattern.matcher(template);

        // matched texts
        Set<String> wordsToReplace = new HashSet<>();
        while (matcher.find()) {
            wordsToReplace.add(matcher.group());
        }

        return wordsToReplace;
    }

    public static String replaceTemplateParams(String template, Set<String> templateParams, Map<String, Object> replacementValues) {

        for (String templateParam : templateParams) {
            String realTemplateParam = templateParam.replaceFirst("\\$", "");

            String replacementValue = String.valueOf(replacementValues.get(realTemplateParam));

            template = template.replaceAll("\\$" + realTemplateParam, replacementValue);
        }

        return template;
    }



}