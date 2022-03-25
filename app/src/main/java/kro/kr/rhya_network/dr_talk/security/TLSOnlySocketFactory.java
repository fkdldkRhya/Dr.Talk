package kro.kr.rhya_network.dr_talk.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Albert-IM on 18/01/2017.
 *
 *
 * 원인 :
 * HTTPsUrlConnection은 TLS Intolerance Support를 지원하기 때문에 TLS를 이용해 통신에 실패한 경우 ‘SSLv3’를 유일한 protocol로 설정하여 다시 서버에 접속을 시도한다.
 * TLS를 이용한 요청이 어떤 이유로 실패했을 때, HTTPsUrlConnection은 SSLv3를 이용하여 재시도하는데 이 때 서버에서 더이상 SSLv3를 지원하지 않는다면 SSLHandshakeException이 발생하게 된다.
 *
 * 해결 :
 * 서버의 TLS가 정상이고 일회성으로 실패한 것이라면 SSLv3로 시도하는 대신에 default protocol list를 이용해 시도하도록 수정
 *
 */

public class TLSOnlySocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;

    public TLSOnlySocketFactory(SSLSocketFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultCipherSuites() {

        return getPreferredDefaultCipherSuites(this.delegate);
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return getPreferredSupportedCipherSuites(this.delegate);
    }



    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        final Socket socket = this.delegate.createSocket(s, host, port, autoClose);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket)socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    @Override
    public Socket createSocket(String s, int i) {
        return null;
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        final Socket socket = this.delegate.createSocket(host, port);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        final Socket socket = this.delegate.createSocket(address, port, localAddress, localPort);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    private String[] getPreferredDefaultCipherSuites(SSLSocketFactory sslSocketFactory) {
        return getCipherSuites(sslSocketFactory.getDefaultCipherSuites());
    }

    private String[] getPreferredSupportedCipherSuites(SSLSocketFactory sslSocketFactory) {
        return getCipherSuites(sslSocketFactory.getSupportedCipherSuites());
    }

    private String[] getCipherSuites(String[] cipherSuites) {
        final ArrayList<String> suitesList = new ArrayList<>(Arrays.asList(cipherSuites));
        suitesList.removeIf(cipherSuite -> cipherSuite.contains("SSL"));
        return suitesList.toArray(new String[0]);
    }

    private String[] getEnabledProtocols(SSLSocket socket) {
        final ArrayList<String> protocolList = new ArrayList<>(Arrays.asList(socket.getSupportedProtocols()));
        protocolList.removeIf(protocl -> protocl.contains("SSL"));
        return protocolList.toArray(new String[0]);
    }

}