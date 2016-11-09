package dev2dev.textclient;

public class MyAddress {
    public String ip;
    public int port;

    public MyAddress(String address){
        this(Helper.getIpFromAddress(address), Helper.getPortFromAddress(address));
    }

    public MyAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public MyAddress(String ip, String port) {
        this(ip, Integer.parseInt(port));
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return ip + ":" + String.valueOf(port);
    }

    boolean equals(String ip, int port) {
        return this.ip.equals(ip) && this.port == port;
    }
}
