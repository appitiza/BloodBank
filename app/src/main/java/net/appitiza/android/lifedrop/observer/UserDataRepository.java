package net.appitiza.android.lifedrop.observer;

import java.util.ArrayList;


public class UserDataRepository implements ObserverActions {

    private static UserDataRepository INSTANCE = null;
    private ArrayList<RepositoryObserver> mObservers;

    private UserDataRepository() {
        mObservers = new ArrayList<>();
    }


    // Creates a Singleton of the class
    public static UserDataRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserDataRepository();
        }
        return INSTANCE;
    }

    @Override
    public void registerObserver(RepositoryObserver repositoryObserver) {
        if (!mObservers.contains(repositoryObserver)) {
            mObservers.add(repositoryObserver);
        }
    }

    @Override
    public void removeObserver(RepositoryObserver repositoryObserver) {
        if (mObservers.contains(repositoryObserver)) {
            mObservers.remove(repositoryObserver);
        }
    }

    @Override
    public void notifyObservers(ObserverData obj) {
        for (RepositoryObserver observer : mObservers) {
            observer.onUserDataChanged(obj);
        }
    }

}