package id.kosan.mgmapp;

/**
 * Created by ROOT on 3/13/2018.
 */

public interface TaskListener {
    void onTaskStarted();

    void onTaskFinished(String result);
}
