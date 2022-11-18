//package com.ulalalab.snipe.infra.util;
//
//import com.ulalalab.snipe.infra.handler.ApplicationContextProvider;
//import org.springframework.context.ApplicationContext;
//
//import javax.script.*;
//
//public final class ScriptUtils {
//
//    public static Invocable getInvocable(String javascriptSource) {
//        ScriptEngineManager sem = new ScriptEngineManager();
//        ScriptEngine se = null;
//        Invocable invocable = null;
//
//        try {
//            se = sem.getEngineByName("JavaScript");
//            se.eval(javascriptSource);
//
//            invocable = (Invocable) se;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return invocable;
//    }
//
//    public static Object getValue(Invocable invocable, String fuctionName, Double... params) {
//        Object resultObj = null;
//
//        try {
//            resultObj = invocable.invokeFunction(fuctionName, params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return resultObj;
//    }
//
////    public static Double getCalculate(String javascriptSource, Double d) {
////        Double resultValue = 1.0;
////
////        try {
////            ScriptEngineManager sem = new ScriptEngineManager();
////
////            ScriptEngine se = sem.getEngineByName("JavaScript");
////
////            Compilable compilable = (Compilable) se;
////            CompiledScript cs = compilable.compile("print('JavaScript is compiled')");
////            cs.eval();
////
////            //se.eval("function caculate(a) {return print(a+b);}");
////            se.eval(javascriptSource);
////            resultValue = (Double) ((Invocable) se).invokeFunction("add", d);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return resultValue;
////    }
//}