package aammo.ppv.service;

import aammo.ppv.observer.NotificationObserver;
import aammo.ppv.observer.UserActionSubject;

public class NotificationService {
    private static NotificationService instance;
    private final UserActionSubject userActionSubject;

    private NotificationService() {
        // Get the singleton subject
        userActionSubject = UserActionSubject.getInstance();

        // Register the notification observer
        userActionSubject.registerObserver(new NotificationObserver());
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void notifyFollowAction(int followerId, int followeeId) {
        userActionSubject.notifyObservers("FOLLOW", followerId, followeeId, followerId);
    }

    public void notifyUnfollowAction(int followerId, int followeeId) {
        userActionSubject.notifyObservers("UNFOLLOW", followerId, followeeId, followerId);
    }

    public void notifyLikeAction(int likerId, int postOwnerId, int postId) {
        userActionSubject.notifyObservers("LIKE", likerId, postOwnerId, postId);
    }

    public void notifyCommentAction(int commenterId, int postOwnerId, int postId) {
        userActionSubject.notifyObservers("COMMENT", commenterId, postOwnerId, postId);
    }

    public void notifyMentionAction(int mentionerId, int mentionedUserId, int postId) {
        userActionSubject.notifyObservers("MENTION", mentionerId, mentionedUserId, postId);
    }

}