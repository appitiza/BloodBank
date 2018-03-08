package net.appitiza.android.lifedrop.observer;


public interface ObserverActions {
    void registerObserver(RepositoryObserver repositoryObserver);
    void removeObserver(RepositoryObserver repositoryObserver);
    void notifyObservers(ObserverData obj);
}
