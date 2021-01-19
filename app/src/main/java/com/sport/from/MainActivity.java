package com.sport.from;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocalIpV6().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                validateV6(strings);
            }
        });
    }


    public MutableLiveData<List<String>> getLocalIpV6() {
        MutableLiveData<List<String>> data=new MutableLiveData<>();
        List<String> addressList = new ArrayList<>();
        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            /*if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                address=  inetAddress.getHostAddress();
                                Log.i("address-4",address);
                            }*/
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                                String address = inetAddress.getHostAddress();
                                Log.i("address-6", address);
                                addressList.add(address);
                            }
                        }
                    }
                    data.setValue(addressList);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }/* else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                Log.i("ipAddress", ipAddress);
                return ipAddress;
            }*/
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return data;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public void validateV6(List<String> addressList) {
        if(addressList.size()==0){
            return;
        }
        for(String hostIp6:addressList){
            //过滤找到真实的ipv6地址
            if (hostIp6 != null && hostIp6.contains("%")) {
                String[] split = hostIp6.split("%");
                String s1 = split[0];
                Log.e("v6 remove % is ", s1);

                if (s1 != null && s1.contains(":")) {
                    String[] split1 = s1.split(":");
                    if (split1.length == 6 || split1.length == 8) {
                        if (split1[0].contains("fe") || split1[0].contains("fc")) {

                        } else {
                             Log.i("ip6_real",s1);
                        }
                    }
                }
            }
        }
    }

}