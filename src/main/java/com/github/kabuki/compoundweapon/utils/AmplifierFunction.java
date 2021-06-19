package com.github.kabuki.compoundweapon.utils;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.weapon.skill.tag.SharedSkillTag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmplifierFunction {
    private static final Pattern TAG = Pattern.compile("[^.()\\d*/+-]+");
    private static final Pattern TOP_PRIORITY_FORMULA = Pattern.compile("\\(([^()]+)\\)");
    private static final Pattern HIGH_PRIORITY_FORMULA = Pattern.compile("(-?[\\d.]+)\\s*([*/])\\s*(-?[\\d.]+)");
    private static final Pattern LOW_PRIORITY_FORMULA = Pattern.compile("(-?[\\d.]+)\\s*([+-])\\s*(-?[\\d.]+)");

    private final String prf;

    private AmplifierFunction(String prf) {
        this.prf = prf;
    }

    public static AmplifierFunction create(String function){
        return new AmplifierFunction(checkExpression(function));
    }

    private static String checkExpression(String expression) {
        int num = 0;
        int c = 0;
        Pattern p = Pattern.compile("-?[\\d.]+|[^()\\d*/+-]+");
        Matcher m = p.matcher(expression);
        while(m.find())
        {
            num++;
        }
        p = Pattern.compile("[*/+-]");
        m = p.matcher(expression);
        while(m.find())
        {
            c++;
        }

        int a = num + c;
        int b = num - c;
        if(a == 2)
        {
            throw new IllegalArgumentException("Expression error, wrong format");
        }
        else if(a == 1 && c == 1)
        {
            throw new IllegalArgumentException("Expression error, Illegal parameters");
        }

        if(b != 1)
        {
            throw new IllegalArgumentException("Expression error, It contains illegal parameters");
        }
        else
        {
            return expression;
        }
    }

    public double calc(ISkillContext context) {
        String s = toPRF(prf, context);
        return ConvertHelper.pasteDouble(s);
    }

    private static String assignmentTag(String s, ISkillContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append(s);
        Matcher m = TAG.matcher(builder.toString());
        while(m.find())
        {
            int start = m.start();
            int end = m.end();
            String tag = builder.substring(m.start(), m.end());
            builder.replace(start, end, SharedSkillTag.getTagNumber(tag, context).toString());
            m.reset(builder.toString());
        }
        return builder.toString();
    }

    private static String toPRF(String s, ISkillContext context) {
        if(!hasTag(s))
        {
            return calNum(s);
        }
        else
        {
            String s1 = assignmentTag(s, context);
            if(hasTag(s1))
            {
                throw new RuntimeException("Oh my god, It cannot happen, AmplifierFunction After assignment tag is error. Please contact the developer");
            }
            else
            {
                return toPRF(s1, context);
            }
        }
    }

    private static String calNum(String src) {
        StringBuilder builder = new StringBuilder();
        if (src.contains("(")) {
            Matcher matcher = TOP_PRIORITY_FORMULA.matcher(src);
            int lastEnd = 0;
            while (matcher.find()) {
                builder.append(src, lastEnd, matcher.start());
                builder.append(calNum(matcher.group(1)));
                lastEnd = matcher.end();
            }
            builder.append(src.substring(lastEnd));
        } else {
            return lCalc(builder, hCalc(builder, src));
        }
        return calNum(builder.toString());
    }

    private static String hCalc(StringBuilder builder, String s) {
        builder.append(s);
        Matcher matcher = HIGH_PRIORITY_FORMULA.matcher(builder.toString());
        while (matcher.find()){
            double f1 = Double.parseDouble(matcher.group(1));
            double f2 = Double.parseDouble(matcher.group(3));
            double result = 0;
            switch (matcher.group(2)){
                case "*":
                    result = f1 * f2;
                    break;
                case "/":
                    if(f1 == 0.0D)
                    {
                        result = 0.0D;
                    }
                    else
                    {
                        result = f1 / f2;
                    }
                    break;
            }
            builder.replace(matcher.start(), matcher.end(), String.valueOf(result));
            matcher.reset(builder.toString());
        }
        return builder.toString();
    }

    private static String lCalc(StringBuilder builder, String s) {
        Matcher matcher = LOW_PRIORITY_FORMULA.matcher(builder.toString());
        while (matcher.find()){
            double f1 = Double.parseDouble(matcher.group(1));
            double f2 = Double.parseDouble(matcher.group(3));
            double result = 0;
            switch (matcher.group(2)){
                case "+":
                    result = f1 + f2;
                    break;
                case "-":
                    result = f1 - f2;
                    break;
            }
            builder.replace(matcher.start(), matcher.end(),
                    String.valueOf(result));
            matcher.reset(builder.toString());
        }
        return builder.toString();
    }

    private static boolean hasTag(String expression) {
        Matcher m = TAG.matcher(expression);
        return m.find();
    }
}
