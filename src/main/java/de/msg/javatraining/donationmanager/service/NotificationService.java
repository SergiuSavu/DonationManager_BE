package de.msg.javatraining.donationmanager.service;

import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private LogService logService;

    public void saveNotification(User user, List<NotificationParameter> parameters, NotificationType type) {
        try {
            Notification notification = new Notification(type, new Date(), user, parameters);
            notificationRepository.save(notification);
            logService.logOperation("INSERT", "Saved notification for user: " + user.getId(), null);
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error saving notification for user: " + user.getId() + ". " + e.getMessage(), null);
        }
    }

    public void markNotificationAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.markAsRead();
            notificationRepository.save(notification);
            logService.logOperation("UPDATE", "Marked notification as read: " + notificationId, null);
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error marking notification as read: " + notificationId + ". " + e.getMessage(), null);
        }
    }

    public void markNotificationAsAppeared(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.markAsAppeared();
            notificationRepository.save(notification);
            logService.logOperation("UPDATE", "Marked notification as appeared: " + notificationId, null);
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error marking notification as appeared: " + notificationId + ". " + e.getMessage(), null);
        }
    }

    public void deleteOldNotifications() {
        try {
            Date thirtyDaysAgo = getThirtyDaysAgo();
            notificationRepository.deleteNotificationsBefore(thirtyDaysAgo);
            logService.logOperation("DELETE", "Deleted notifications older than thirty days.", null);
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error deleting old notifications. " + e.getMessage(), null);
        }
    }

    private Date getThirtyDaysAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return cal.getTime();
    }

    public List<Notification> getNotificationsNotAppearedOnView(Long userId) {
        try {
            List<Notification> notifications = notificationRepository.getNotificationsNotAppearedOnView(userId);
            // No need to log a fetch operation, but you can if you want.
            return notifications;
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error fetching notifications not appeared for user: " + userId + ". " + e.getMessage(), null);
            throw e; // Re-throwing the exception after logging it.
        }
    }

    public List<Notification> getAllNotifications(Long userId) {
        try {
            List<Notification> notifications = notificationRepository.getAllNotifications(userId);
            // No need to log a fetch operation, but you can if you want.
            return notifications;
        } catch (Exception e) {
            logService.logOperation("ERROR", "Error fetching all notifications for user: " + userId + ". " + e.getMessage(), null);
            throw e; // Re-throwing the exception after logging it.
        }
    }
}
