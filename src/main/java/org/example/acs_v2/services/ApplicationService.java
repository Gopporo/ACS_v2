package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.ApplicationRepository;
import org.example.acs_v2.repositories.TemporaryAccessRequestRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления заявками
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ZoneRepository zoneRepository;
    private final UserRepository userRepository;
    private final TemporaryAccessRequestRepository temporaryAccessRequestRepository;

    private List<Application> excludePendingTempAccessRequests(List<Application> applications) {
        List<Long> requestedIds = temporaryAccessRequestRepository.findAllRequestedApplicationIds();
        if (requestedIds == null || requestedIds.isEmpty()) {
            return applications;
        }
        Set<Long> requestedSet = requestedIds.stream().collect(Collectors.toSet());
        return applications.stream()
                .filter(a -> a.getId() == null || !requestedSet.contains(a.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Получает заявки по уровню доступа (только незавершенные)
     *
     * @param accessLvl уровень доступа
     * @return список заявок
     */
    public List<Application> getApplicationsByAccessLvl(AccessLevel accessLvl) {
        return excludePendingTempAccessRequests(
                applicationRepository.findApplicationsByAccessLevelAndCompletedFalse(accessLvl)
        );
    }

    /**
     * Получает заявки по имени (только незавершенные)
     *
     * @param name имя заявки
     * @return список заявок
     */
    public List<Application> getApplicationsByName(String name) {
        return excludePendingTempAccessRequests(
                applicationRepository.findApplicationsByNameAndCompletedFalse(name)
        );
    }

    /**
     * Получает список свободных заявок (без назначенного пользователя)
     *
     * @return список заявок
     */
    public List<Application> listOfFreeApplications() {
        return excludePendingTempAccessRequests(
                applicationRepository.findByUserIdIsNullAndCompletedFalse()
        );
    }

    /**
     * Получает список заявок конкретного пользователя
     *
     * @param userId ID пользователя
     * @return список заявок
     */
    public List<Application> listOfUserApplications(Long userId) {
        return applicationRepository.findAllByUserIdAndCompletedFalse(userId);
    }

    /**
     * Получает список всех незавершенных заявок
     *
     * @return список заявок
     */
    public List<Application> list() {
        return excludePendingTempAccessRequests(applicationRepository.findAllByCompletedFalse());
    }

    /**
     * Получает заявку по ID (только незавершенную)
     *
     * @param id ID заявки
     * @return заявка или null
     */
    public Application getById(Long id) {
        return applicationRepository.findByIdAndCompletedFalse(id);
    }

    /**
     * Обновляет заявку
     *
     * @param application заявка для обновления
     */
    public void updateApplication(Application application) {
        log.debug("Updating application with ID: {}", application.getId());
        applicationRepository.save(application);
    }

    /**
     * Создает новую заявку для зоны
     *
     * @param zoneId      ID зоны
     * @param application заявка для создания
     * @throws ResourceNotFoundException если зона не найдена
     */
    public void createApplication(Long zoneId, Application application) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", zoneId));

        application.setZone(zone);
        application.setAccessLevel(zone.getZoneAccessLvl());
        application.setCompleted(false);

        applicationRepository.save(application);
        log.info("Application created for zone: {}", zone.getName());
    }

    /**
     * Принимает заявку пользователем
     *
     * @param applicationId ID заявки
     * @param userId        ID пользователя
     * @throws ResourceNotFoundException если заявка или пользователь не найдены
     */
    public void acceptApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        application.setUser(user);
        applicationRepository.save(application);
        log.info("Application {} accepted by user {}", applicationId, userId);
    }

    /**
     * Отклоняет заявку (снимает назначение пользователя)
     *
     * @param applicationId ID заявки
     * @throws ResourceNotFoundException если заявка не найдена
     */
    public void declineApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        application.setUser(null);
        applicationRepository.save(application);
        log.info("Application {} declined", applicationId);
    }

    /**
     * Удаляет заявку
     *
     * @param id ID заявки
     * @throws ResourceNotFoundException если заявка не найдена
     */
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application", id);
        }
        applicationRepository.deleteById(id);
        log.info("Application {} deleted", id);
    }
}
