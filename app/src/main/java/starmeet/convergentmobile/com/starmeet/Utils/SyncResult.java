package starmeet.convergentmobile.com.starmeet.Utils;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public class SyncResult<T> {
    private static final long TIMEOUT = 20000L;
    private T result;

    public T getResult() {
        long startTimeMillis = System.currentTimeMillis();
        while (result == null && System.currentTimeMillis() - startTimeMillis < TIMEOUT) {
            synchronized (this) {
                try {
                    wait(TIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public void setResult(T result) {
        this.result = result;
        synchronized (this) {
            notify();
        }
    }
}