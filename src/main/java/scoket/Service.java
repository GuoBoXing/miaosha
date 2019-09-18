package scoket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {
    public static void main(String[] args) {
        Service myServerBin=new Service();
        myServerBin.startAction();
    }

    public void startAction(){
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(7878);
            while(true){
                Socket socket=serverSocket.accept();
                new Thread(new MyRuns(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket!=null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    class MyRuns implements Runnable{

        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;

        public MyRuns(Socket socket) {
            super();
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String lineString="";
                while( !(lineString=reader.readLine()).equals("bye") ){
                    System.out.println("客户端："+socket.hashCode()+"==="+lineString);
                    writer.write("服务器返回："+lineString+"\n");
                    writer.flush();
                }
            } catch (Exception e) {
                System.out.println("连接关闭");
            } finally {
                try {
                    if (reader!=null) {
                        reader.close();
                    }
                    if (writer!=null) {
                        writer.close();
                    }
                    if (socket!=null) {
                        socket.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

}
