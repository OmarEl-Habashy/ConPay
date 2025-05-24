package aammo.ppv.observer;

public interface Observer {
    void update(String action, int senderId, int recipientId, int referenceId);
}