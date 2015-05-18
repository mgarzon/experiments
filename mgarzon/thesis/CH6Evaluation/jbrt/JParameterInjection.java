package org.jbrt.client.annotations;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cipov Peter
 */
public class JParameterInjection {

    private Object object;
    private Map<Type, Object[]> map;

    public JParameterInjection(Object instance) {
        this.object = instance;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    

    public void add(Type type, Object... parameters) {
        if (map == null) {
            map = new HashMap<Type, Object[]>();
        }
        map.put(type, parameters);
    }

    public void clear() {
        map.clear();
    }

    public void inject(Object object) throws Exception{
        if (map == null) {
            return;
        }

        Method[] methods = object.getClass().getDeclaredMethods();
        if (methods == null) {
            return;
        }
        Parameter annotation;
        Object[] params;
        for (Method method : methods) {
            annotation = method.getAnnotation(Parameter.class);
            if (annotation != null) {
                params = map.get(annotation.type());
                if(params != null) {
                    method.invoke(object,params);
                }
            }

        }
    }

    public void inject() throws Exception {
        inject(this.object);
    }

}
