package aammo.ppv.observer;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String action, int senderId, int recipientId, int referenceId);
}