package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.repositories.ApplicationRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Application> getApplicationsByAccessLvl(int accessLvl) {
        return applicationRepository.findApplicationsByAccessLevelAndCompletedFalse(accessLvl);
    }

    public List<Application> getApplicationsByName(String name) {
        return applicationRepository.findApplicationsByNameAndCompletedFalse(name);
    }

    public List<Application> listOfFreeApplications() {
        return applicationRepository.findByUserIdIsNullAndCompletedFalse();
    }

    public List<Application> listOfUserApplications(Long userId) {
        return applicationRepository.findAllByUserIdAndCompletedFalse(userId);
    }

    public List<Application> list() {
        return applicationRepository.findAllByCompletedFalse();
    }

    public Application getById(Long id) {
        return applicationRepository.findByIdAndCompletedFalse(id);
    }

    public void updateApplication(Application application) {
        applicationRepository.save(application); // Сохраняем пользователя с обновленными ролями
    }

    public boolean createApplication(Long zoneId, Application application) {

        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new RuntimeException("Zone with ID " + zoneId + " not found"));
        application.setZone(zone);
        application.setAccessLevel(zone.getZoneAccessLvl());
        application.setCompleted(false);

        applicationRepository.save(application);
        return true;
    }


    public void acceptApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application with ID " + applicationId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        application.setUser(user);
        applicationRepository.save(application);
    }

    public void declineApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application with ID " + applicationId + " not found"));
        application.setUser(null);
        applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
