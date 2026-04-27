package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.TemporaryAccessGrant;
import org.example.acs_v2.models.TemporaryAccessRequest;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.ApplicationRepository;
import org.example.acs_v2.repositories.TemporaryAccessGrantRepository;
import org.example.acs_v2.repositories.TemporaryAccessRequestRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemporaryAccessService {

    private final TemporaryAccessRequestRepository requestRepository;
    private final TemporaryAccessGrantRepository grantRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public TemporaryAccessRequest createRequest(Long applicationId, Long requesterId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", requesterId));

        if (application.isCompleted()) {
            throw new IllegalStateException("Заявка уже завершена");
        }
        if (application.getUser() != null) {
            throw new IllegalStateException("Заявка уже принята другим сотрудником");
        }
        if (requester.getDepartment() == null || requester.getDepartment().getHead() == null) {
            throw new IllegalStateException("Невозможно запросить допуск: у вашего отдела нет руководителя");
        }

        return requestRepository.findByApplicationIdAndRequesterId(applicationId, requesterId)
                .orElseGet(() -> {
                    TemporaryAccessRequest req = new TemporaryAccessRequest();
                    req.setApplication(application);
                    req.setRequester(requester);
                    req.setRequestedLevel(application.getAccessLevel());
                    TemporaryAccessRequest saved = requestRepository.save(req);
                    log.info("Temporary access request created: requestId={}, applicationId={}, requesterId={}",
                            saved.getId(), applicationId, requesterId);
                    return saved;
                });
    }

    public List<TemporaryAccessRequest> listRequestsForDirectorDepartment(Long departmentId) {
        return requestRepository.findAllByRequesterDepartmentIdOrderByCreatedAtDesc(departmentId);
    }

    public TemporaryAccessRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("TemporaryAccessRequest", requestId));
    }

    public void approveRequest(Long requestId, Long directorId) {
        TemporaryAccessRequest request = getRequest(requestId);
        User director = userRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", directorId));

        User requester = request.getRequester();
        if (requester.getDepartment() == null || requester.getDepartment().getHead() == null) {
            throw new IllegalStateException("У сотрудника нет отдела/руководителя");
        }
        if (!requester.getDepartment().getHead().getId().equals(director.getId())) {
            throw new IllegalStateException("Вы не можете одобрить этот запрос");
        }

        Application application = request.getApplication();
        if (application.isCompleted()) {
            requestRepository.delete(request);
            throw new IllegalStateException("Заявка уже завершена");
        }
        if (application.getUser() != null) {
            requestRepository.delete(request);
            throw new IllegalStateException("Заявка уже принята");
        }

        TemporaryAccessGrant grant = grantRepository.findByApplicationIdAndUserId(application.getId(), requester.getId())
                .orElseGet(() -> {
                    TemporaryAccessGrant g = new TemporaryAccessGrant();
                    g.setApplication(application);
                    g.setUser(requester);
                    g.setAccessLevel(request.getRequestedLevel());
                    return g;
                });
        grantRepository.save(grant);

        application.setUser(requester);
        applicationRepository.save(application);

        requestRepository.delete(request);

        log.info("Temporary access request approved: requestId={}, applicationId={}, userId={}",
                requestId, application.getId(), requester.getId());
    }

    public void denyRequest(Long requestId, Long directorId) {
        TemporaryAccessRequest request = getRequest(requestId);
        User director = userRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", directorId));

        User requester = request.getRequester();
        if (requester.getDepartment() == null || requester.getDepartment().getHead() == null) {
            requestRepository.delete(request);
            return;
        }
        if (!requester.getDepartment().getHead().getId().equals(director.getId())) {
            throw new IllegalStateException("Вы не можете отклонить этот запрос");
        }

        // просто удаляем запрос: заявка остаётся в общем списке (она и так свободна, т.к. не назначена)
        requestRepository.delete(request);
        log.info("Temporary access request denied: requestId={}", requestId);
    }

    public AccessLevel getMaxActiveTemporaryLevel(Long userId) {
        return grantRepository.findMaxActiveLevelByUserId(userId);
    }

    public void revokeGrantForApplication(Long applicationId, Long userId) {
        grantRepository.deleteByApplicationIdAndUserId(applicationId, userId);
        log.info("Temporary access grant revoked: applicationId={}, userId={}", applicationId, userId);
    }
}

