package org.learning;

import java.net.InetSocketAddress;

public class Example {
    public static void main(String[] args) {
        System.out.println("Local invocation");
        CalculatorImpl calculator = new CalculatorImpl();
        int result = calculator.add(1, 2);
        System.out.println(result);

        System.out.println("Remote invocation");
        Calculator remoteCalculator = RPCClient.getRemoteProxy(
                Calculator.class,
                new InetSocketAddress("127.0.0.1", 8080)
        );
        System.out.println(remoteCalculator.add(1, 2));
    }
}
