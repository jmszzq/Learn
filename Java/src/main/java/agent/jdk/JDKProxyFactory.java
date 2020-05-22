package agent.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyFactory implements InvocationHandler {

    private Object target;

    public JDKProxyFactory(Object object){
        this.target = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("这是jdk的代理方法");

        Method method2 = target.getClass().getMethod("findName", null);
        Method method3 = Class.forName("agent.jdk.Student").getMethod("findNumber", null);
        System.out.println("目标对象的方法:" + method2.toString());
        System.out.println("目标接口的方法:" + method.toString());
        System.out.println("代理对象的方法:" + method3.toString());

        return method.invoke(target, args);
    }

    public Object createProxyObj(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }
}
