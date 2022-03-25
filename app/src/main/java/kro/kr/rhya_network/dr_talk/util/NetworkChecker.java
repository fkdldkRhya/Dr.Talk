package kro.kr.rhya_network.dr_talk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

/*
 * Internet Connection Detection Manager
 *
 * Internet Connection : isConnection --> true
 * No Internet connection : isConnection --> false
 */
public class NetworkChecker extends ConnectivityManager.NetworkCallback {
    // 네트워크 변경 감지
    private final NetworkRequest networkRequest;
    private final ConnectivityManager connectivityManager;
    // 네트워크 상태
    private boolean _isConnection = false;
    // Callback 인터페이스
    // ----------------------------------------------
    public interface NetworkListener {
        void isDisconnection(); // 연결 X
        void isConnection(); // 연결 O
    }
    // ----------------------------------------------
    // Callback 변수
    private NetworkListener networkListener;


    /**
     * 생성자 - 변수 초기화
     * @param context activity context
     */
    public NetworkChecker(Context context) {
        networkListener = null;

        networkRequest =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();

        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * NetworkListener 설정
     * @param networkListener Listener
     */
    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }


    /**
     * 네트워크 상태 변경 감지 등록
     */
    public void register() {
        this.connectivityManager.registerNetworkCallback(networkRequest, this);
    }


    /**
     * 네트워크 상태 변경 감지 등록 해제
     */
    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }


    /**
     * 네트워크 연결됨
     * @param network [Override]
     */
    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        // 네트워크가 연결됨

        _isConnection = true;

        if (networkListener != null) {
            networkListener.isConnection();
        }
    }


    /**
     * 네트워크가 연결 안됨
     * @param network [Override]
     */
    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        // 네트워크 연결 안됨

        _isConnection = false;

        if (networkListener != null) {
            networkListener.isDisconnection();
        }
    }


    /**
     * 네트워크 상태 반환
     * @return 네트워크 상태
     */
    public boolean isConnection() {
        return _isConnection;
    }
}
