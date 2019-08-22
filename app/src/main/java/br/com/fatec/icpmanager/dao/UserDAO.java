package br.com.fatec.icpmanager.dao;

import br.com.fatec.icpmanager.model.Notification;
import br.com.fatec.icpmanager.model.User;

public class UserDAO extends AbstractDAO<User> {

    public UserDAO() {
        super("users");
    }

    public void addNotification(String userId, Notification notification) {
        String id = newKey();
        notification.setId(id);
        db.child(userId).child("notifications").child(id).setValue(notification);
    }

    public void removeNotification(String userId,String notificationId){
        db.child(userId).child("notifications").child(notificationId).removeValue();
    }

    public void markNotificationRead(String userId,String notificationId){
        db.child(userId).child("notifications").child(notificationId).child("read").setValue(true);
    }

    public void setMessageToken(String userId,String messageToken){
        db.child(userId).child("mToken").setValue(messageToken);
    }

}