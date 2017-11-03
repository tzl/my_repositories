package misc;

import java.util.Properties;

public class SystemProperties {
    public static void main(String[] args) {
        Properties pps = System.getProperties();
        pps.list(System.out);
    }
}
