package agent.jdk;

public class Student implements User {

    @Override
    public String findName() {
        return "学生张三";
    }

    @Override
    public String findNumber() {
        return "学号001";
    }
}
