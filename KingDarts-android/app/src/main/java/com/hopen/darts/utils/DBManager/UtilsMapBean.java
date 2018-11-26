package com.hopen.darts.utils.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class UtilsMapBean {

    /**
     * 将map转为bean对象
     * @author lukai
     * @param paramap  需要转换的map
     * @param beanclass  bean的class类型
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */


    /**
     * 将简单（只包含存在基本类型及其封装类和string）bean类型转换为map，此方法只做第一层的处理，即：如果param中有符合类型的对象，只取出对象
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //判断是否为空bean，如果是直接返回
        if (bean == null) {
            return resultMap;
        }
        //获取类的class
        Class cls = bean.getClass();

        Field[] fields = cls.getDeclaredFields();//获取所有字段
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();//获取所有字段名称
            Object filedValue = null;
            try {
                int typeInt = fields[i].getModifiers();//获取字段的类型
                //获取字段的类型申明表，8静态，2私有，16final  =26，类型26为静态常量，不做处理如最终serialVersionUID
                if (typeInt != 26) {
                    fields[i].setAccessible(true);//设置访问权限
                    filedValue = fields[i].get(bean);//获取所有字段的值
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            resultMap.put(fieldName, filedValue);
        }

        return resultMap;

    }

    /**
     * 此方法目前未完成，可以将简单的bean转换成{name:张三,sex:男}这种格式
     *
     * @param bean
     * @return
     */
    public static <T> String beanToJsonStr(T bean) {
        StringBuffer sb = new StringBuffer("{");
        //判断是否为空bean，如果是直接返回
        if (bean == null) {
            sb.append("}");
            return sb.toString();
        }
        //获取类的class
        Class cls = bean.getClass();

        Field[] fields = cls.getDeclaredFields();//获取所有字段
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();//获取所有字段名称
            Object filedValue = null;
            try {
                int typeInt = fields[i].getModifiers();//获取字段的类型
                //获取字段的类型申明表，8静态，2私有，16final  =26，类型26为静态常量，不做处理如最终serialVersionUID
                if (typeInt != 26) {
                    fields[i].setAccessible(true);//设置访问权限
                    filedValue = fields[i].get(bean);//获取所有字段的值
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (i != 0) {
                sb.append("," + fieldName + ":" + fieldType(filedValue));
            } else {
                sb.append(fieldName + ":" + fieldType(filedValue));
            }
            System.out.println(isWrapClass(filedValue.getClass()));
        }
        sb.append("}");
        return sb.toString();

    }

    /**
     * 判断队形类型是否为java的基本类型或者基本类型的封装类
     *
     * @param clz
     * @return
     * @author lukai
     */
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 特殊类型处理，目前只有日期类型 ，将日期类型转会成 int字符串,其他类型直接返回toString()方法的值，null返回null
     *
     * @param field
     * @return
     * @author lukai
     */
    public static <T> String fieldType(T field) {
        if (field == null) {
            return null;
        }
        //判断date的子类
        if (Date.class.isAssignableFrom(field.getClass())) {
            Date dt = (Date) field;
            return String.valueOf(dt.getTime());

        }
        //判断Calendar的子类
        if (Calendar.class.isAssignableFrom(field.getClass())) {
            Calendar calendar = (Calendar) field;
            return String.valueOf(calendar.getTime());
        }

        return field.toString();
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) throws IllegalAccessException, InstantiationException {
        T bean = beanClass.newInstance();
        Field[] fields = beanClass.getDeclaredFields();//获取所有字段
        for (Field field : fields) {
            String fieldName =field.getName();//获取所有字段名称
            int typeInt= field.getModifiers();//获取字段的类型
            //获取字段的类型申明表，8静态，2私有，16final  =26，类型26为静态常量不做处理 如:serialVersionUID
            if(typeInt!=26){
                field.setAccessible(true);//设置访问权限
                if(map.get(fieldName)!=null){
                    field.set(bean,map.get(fieldName));//设置bean对象的值
                }
            }
        }

        return bean;
    }


    /**
     * jsonObjec转Map
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static Map toMap(JSONObject jsonObject) throws JSONException {

        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;

        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);

        }
        return result;
    }


    /**
     * 方法名称:transStringToMap
     * 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map transStringToMap(String mapString){
        Map map = new HashMap();
        StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, ","); entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), ":");
        return map;
    }



}
