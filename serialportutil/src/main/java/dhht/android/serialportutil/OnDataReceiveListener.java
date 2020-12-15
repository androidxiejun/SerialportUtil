package dhht.android.serialportutil;

/**
 * Created by AndroidXJ on 2020/6/16.
 */
public interface OnDataReceiveListener {
    void onDataReceive(byte[] buffer, int size);
}
