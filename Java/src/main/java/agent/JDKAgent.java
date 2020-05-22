package agent;

import agent.jdk.Student;
import agent.jdk.User;
import agent.jdk.JDKProxyFactory;

public class JDKAgent {

    public static void main(String[] args) {
        User user = new Student();
        JDKProxyFactory userProxy = new JDKProxyFactory(user);
        Object object = userProxy.createProxyObj();

        User student = (User)object;
        System.out.println(student.findName());
        System.out.println(student.findNumber());
    }


}
