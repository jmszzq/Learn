package agent;

import agent.jdk.CGlibProxyFactory;
import agent.jdk.Student;
import agent.jdk.User;

public class CGlibAgent {

    public static void main(String[] args) {
        User user = new Student();
        User cglibUser = (User) new CGlibProxyFactory().getProxyByCgLib(user.getClass());
        cglibUser.findName();
    }

}
