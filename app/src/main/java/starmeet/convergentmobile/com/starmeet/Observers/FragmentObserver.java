package starmeet.convergentmobile.com.starmeet.Observers;

import java.util.Observable;

public class FragmentObserver extends Observable {
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }
}
