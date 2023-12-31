package org.learning;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCServer {

    private Calculator calculator;

    private String address;

    private int port;

    private ExecutorService executor;

    private boolean isRunning;


    public RPCServer(Calculator calculator, String address, int port) {
        this.calculator = calculator;
        this.address = address;
        this.port = port;
        this.executor = Executors.newSingleThreadExecutor();
        this.isRunning = false;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        isRunning = true;
        while (isRunning) {
            Socket socket = serverSocket.accept();
            executor.execute(() -> {
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);
                    ObjectOutputStream oos = new ObjectOutputStream(os);

                    // read method and arg types and arg values.
                    String method = ois.readUTF();
                    Class<?>[] argTypes = (Class<?>[]) ois.readObject();
                    Object[] args = (Object[]) ois.readObject();

                    // real call the method
                    Method calculateMethod = calculator.getClass().getMethod(method, argTypes);
                    Object result = calculateMethod.invoke(calculator, args);

                    // return result
                    oos.writeObject(result);
                    System.out.println("rpc server return result: " + result);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
        serverSocket.close();
    }

    public void stop() {
        isRunning = false;
        executor.shutdownNow();
    }


    public static void main(String[] args) throws IOException {
        RPCServer rpcServer = new RPCServer(new CalculatorImpl(), "127.0.0.1", 8080);
        rpcServer.start();
    }
}
