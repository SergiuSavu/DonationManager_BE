package de.msg.javatraining.donationmanager.service;

import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void saveNotification(User user, List<NotificationParameter> parameters, NotificationType type) {
        Notification notification = new Notification(type, new Date(), user, parameters);
        notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 0 12 * * ?") // Every day at noon
    public void deleteOldNotifications() {
        Date thirtyDaysAgo = getThirtyDaysAgo();
        notificationRepository.deleteNotificationsBefore(thirtyDaysAgo);
    }

    private Date getThirtyDaysAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return cal.getTime();
    }
//    @Scheduled(cron = "0 */1 * * * ?")  // Every minute for testing
//    public void deleteRecentNotifications() {
//        Date fewMinutesAgo = getFewMinutesAgo();
//        System.out.println("Running deleteRecentNotifications at: " + new Date());
//        System.out.println("Deleting notifications before: " + fewMinutesAgo);
//        notificationRepository.deleteNotificationsBefore(fewMinutesAgo);
//    }
//
//    private Date getFewMinutesAgo() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, -5); // Let's say 5 minutes for this example
//        return cal.getTime();
//    }
}
