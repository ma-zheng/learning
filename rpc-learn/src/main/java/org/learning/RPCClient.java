package org.learning;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings("unchecked")
public class RPCClient {

    public static <T> T getRemoteProxy(Class clazz, InetSocketAddress address) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                // an invocation handler instance
                (proxy, method, args) -> {
                    Socket socket = null;
                    ObjectInputStream ois = null;
                    ObjectOutputStream oos = null;
                    try {
                        socket = new Socket();
                        socket.connect(address);

                        OutputStream os = socket.getOutputStream();
                        oos = new ObjectOutputStream(os);

                        // write method and arg types and arg values.
                        oos.writeUTF(method.getName());
                        oos.writeObject(method.getParameterTypes());
                        oos.writeObject(args);

                        InputStream is = socket.getInputStream();
                        ois = new ObjectInputStream(is);

                        // read result
                        Object result = ois.readObject();

                        return result;
                    } finally {
                        if (ois != null) {
                            ois.close();
                        }
                        if (oos != null) {
                            oos.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    }

                }
        );
    }
}
