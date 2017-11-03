class Parent
{
    int pm1;
    int pm2 = 10;
    {
        System.out.println("Parent's instance initialize block");
    }
    int pm3 = pmethod();
    public static int spm1 = 10;
    static {
        System.out.println("Parent's static initialize block");
    }

    Parent()
    {
        System.out.println("Parent's default constructor");
    }

    Parent(int a)
    {
        System.out.println("Parent's self-define constructor");
    }

    static void staticmethod()
    {
        System.out.println("Parent's staticmethod");
    }

    int pmethod()
    {
        System.out.println("Parent's method");
        return 3;
    }
}

class Child extends Parent
{
    int cm1;
    int cm2 = 10;
    int cm3 = cmethod();
    Other co;
    public static int scm1 = 10;
    {
        System.out.println("Child's instance initialize block");
    }
    static {
        System.out.println("Child's static initialize block");
    }

    Child()
    {
        co = new Other();
        System.out.println("Child's default constructor");
    }

    Child(int m)
    {
        System.out.println("Child's self-define constructor");
        cm1 = m;
    }

    static void staticmethod()
    {
        System.out.println("Child's staticmethod");
    }

    int cmethod()
    {
        System.out.println("Child's method");
        return 3;
    }

}

class Other
{
    int om1;

    Other()
    {
        System.out.println("Other's default constructor");
    }

}

public class InitializationTest
{
    public static void main(String args[])
    {
        Child c1;
        System.out.println("program start");
        //System.out.println(Child.scm1);
        c1 = new Child(10);
        System.out.println("program end");
    }
}

//编译此文件: javac InitializationTest.java
//运行此文件: java  InitializationTest

/**
 * program start
 * Parent's static initialize block
 * Child's static initialize block
 * Parent's instance initialize block
 * Parent's method
 * Parent's default constructor
 * Child's method
 * Child's instance initialize block
 * Child's self-define constructor
 * program end
 */
