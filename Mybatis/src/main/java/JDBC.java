import java.sql.*;

public class JDBC {

    public static void main(String[] args) {
        Connection conn = null;
        Statement statement = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://***:4000/auth_test?useSSL=false&serverTimezone=GMT%2B8","root","");

            statement = conn.createStatement();
            String sql = "SELECT * FROM user_all";
            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                String name = rs.getString("name");
                String phone = rs.getString("phone");

                System.out.print("名称: " + name);
                System.out.print(", phone: " + phone);
                System.out.print("\n");
            }
            rs.close();
            statement.close();
            conn.close();
        } catch(Exception se){
            se.printStackTrace();
        } finally{
            try{
                if(statement!=null){
                    statement.close();
                }
            }catch(SQLException se2){
                se2.printStackTrace();
            }
            try{
                if(conn!=null){
                    conn.close();
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}

/*
三种statement

(1)Statement 对象：用于执行不带参数的简单 SQL 语句；

    它提供了三种执行SQL语句的方法：

    executeQuery()：用于产生单个结果集的sql，如select语句

    executeUpdate：用于执行insert、delete、update、create table、drop table等

    execute()：用于执行返回多个结果集、多个更新计数或二者组合的语句，多数程序员不会需要该高级功能。


(2)PreparedStatement 对象：用于执行带或不带参数的预编译 SQL 语句

    PreparedStatement 实例包含已编译的 SQL 语句。包含于 PreparedStatement 对象中的 SQL 语句可具有一个或多个 IN 参数。IN 参数的值在 SQL 语句创建时未被指定。相反的，该语句为每个 IN 参数保留一个问号（“？”）作为占位符。每个问号的值必须在该语句执行之前通过适当的 prestmt.setXXX() 方法来提供。

    由于 PreparedStatement 对象已预编译过，所以其执行速度要快于 Statement 对象。因此多次执行的 SQL 语句经常创建为 PreparedStatement 对象，以提高效率。

    PreparedStatement接口也有自己的executeQuery、executeUpdate 和 execute 方法。Statement 对象本身不包含 SQL 语句，因而必须给 Statement.execute 方法提供 SQL 语句作为参数。PreparedStatement 对象并不将 SQL 语句作为参数提供给这些方法，因为它们已经包含预编译 SQL 语句。


(3)CallableStatement 对象：用于执行对数据库存储过程的调用。

    将常用的或很复杂的工作，预先用SQL语句写好并用一个指定的名称存储起来, 那么以后要叫数据库提供与已定义好的存储过程的功能相同的服务时,只需调用execute,即可自动完成命令。

    存储过程只在创造时进行编译，以后每次执行存储过程都不需再重新编译，而一般SQL语句每执行一次就编译一次,所以使用存储过程可提高数据库执行速度。

    当对数据库进行复杂操作时(如对多个表进行Update,Insert,Query,Delete时），可将此复杂操作用存储过程封装起来与数据库提供的事务处理结合一起使用。

 

 */
