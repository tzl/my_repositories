
class Base
{
    public int _b;

    public Base(int v)
    {
        _b = v;
        System.out.println("Base constructor");
    }
}

public class Child extends Base
{
    public final String const_str = "hello world";
    public int _a;

    public Child(int a)
    {
        super(a+1);
        _a = a;
    }

    public static void main(String args[])
    {
        StringBuffer strBuf = new StringBuffer(args[0]);

        Child child = new Child(10);

        System.out.println(strBuf.toString());
    }
}
