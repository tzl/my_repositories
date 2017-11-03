/**
 * @file SocketTest.java
 */

package main;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import util.ByteParse;

public class SocketTest
{
    public static void main(String args[])
    {
        if (1 > args.length) {
            System.out.println("argument error: arglen=" + args.length);
            return;
        }

        int pos;
        ByteParse bp = new ByteParse(args[0].getBytes());
        Thread thread = null;

        pos = bp.getIndex("client", 0);
        if (-1 != pos) {
            thread = new SocketThread("127.0.0.1", 1234);
        }

        pos =  bp.getIndex("server", 0);
        if (-1 != pos) {
            thread = new SocketThread(null, 1234);
        }

        if (null != thread) {
            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("argument error 1.");
        }
    }
}

class SocketThread extends Thread
{
    private boolean _isClient;
    Socket socket = null;

    public SocketThread(String host, int port)
    {
        _isClient = (null == host)? false: true;

        try {
            if (null == host) {
                ServerSocket ss = new ServerSocket(port);
                socket = ss.accept();
            }
            else {
                socket = new Socket("127.0.0.1", port);
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        int data;
        OutputStream os = null;
        InputStream is = null;

        try {
            os = socket.getOutputStream();
            is = socket.getInputStream();

            if (true == _isClient) {
                os.write(-1);
                System.out.println("client sent -1");
            }
            else {
                /*data = is.read();*/
                DataInputStream dis = new DataInputStream(is);
                do {
                    data = dis.readByte();
                    System.out.println("server received " + data);
                }
                while (true);
            }

            os.close();
            is.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
