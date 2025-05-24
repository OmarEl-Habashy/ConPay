package aammo.ppv.observer;

import java.util.ArrayList;
import java.util.List;

public class UserActionSubject implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private static final UserActionSubject instance = new UserActionSubject();

    private UserActionSubject() {}

    public static UserActionSubject getInstance() {
        return instance;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String action, int senderId, int recipientId, int referenceId) {
        for (Observer observer : observers) {
            observer.update(action, senderId, recipientId, referenceId);
        }
    }
}