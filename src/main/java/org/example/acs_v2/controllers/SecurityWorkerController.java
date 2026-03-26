package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Worker;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.services.WorkerService;
import org.example.acs_v2.tcp.TcpFingerprintServer;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.constants.ViewConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SECURITY')")
@Slf4j
public class SecurityWorkerController {

    private final WorkerService workerService;
    private final TcpFingerprintServer tcpFingerprintServer;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/workers")
    public String getWorkers(@RequestParam(required = false) String accessLevel, Model model, Principal principal) {
        List<Worker> workers = (accessLevel == null || accessLevel.isBlank())
                ? workerService.getAllWorkers()
                : workerService.getWorkersByAccessLevel(accessLevel);

        model.addAttribute("workers", workers);
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("accessLevel", accessLevel);
        modelAttributeHelper.addCommonAttributes(model, principal);

        return ViewConstants.SECURITY_WORKERS;
    }

    @GetMapping("/security/workers/add")
    public String showAddWorkerForm(Model model, Principal principal) {
        Worker worker = new Worker();
        worker.setStatus(Status.ACTIVE);
        model.addAttribute("worker", worker);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);

        return ViewConstants.SECURITY_WORKER_ADD;
    }

    @PostMapping("/security/workers/add")
    public String addWorker(@ModelAttribute Worker worker, Principal principal) {
        worker.setStatus(Status.ACTIVE);
        if (worker.getAccessLevel() == null) {
            worker.setAccessLevel(AccessLevel.UNKNOWN);
        }
        // fingerprintHash заполняется по TCP в режиме регистрации
        worker.setFingerprintHash(null);
        workerService.addWorker(worker);
        return "redirect:/security/workers/edit/" + worker.getId();
    }

    @GetMapping("/security/workers/edit/{id}")
    public String showEditWorkerForm(@PathVariable Long id, Model model, Principal principal) {
        Worker worker = workerService.getById(id);
        if (worker == null) {
            throw new ResourceNotFoundException("Worker", id);
        }
        model.addAttribute("worker", worker);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_WORKER_EDIT;
    }

    @PostMapping("/security/workers/update/{id}")
    public String updateWorker(@PathVariable Long id, @ModelAttribute Worker formWorker) {
        workerService.updateWorker(id, formWorker);
        return "redirect:/security/workers/edit/" + id;
    }

    @GetMapping("/security/workers/delete/{id}")
    public String deleteWorker(@PathVariable Long id) {
        Worker worker = workerService.getById(id);
        if (worker != null) {
            // Сначала включаем режим удаления на TCP-сервере, затем удаляем запись
            tcpFingerprintServer.enableDeleteMode(worker);
            workerService.deleteWorker(id);
        }
        return "redirect:/security/workers";
    }

    @PostMapping("/security/workers/start-register/{id}")
    public String startFingerprintRegistration(@PathVariable Long id) {
        Worker worker = workerService.getById(id);
        if (worker == null) {
            return "redirect:/security/workers";
        }
        if (tcpFingerprintServer.isRegisterModeEnabled()) {
            return "redirect:/security/workers/edit/" + id;
        }
        tcpFingerprintServer.enableRegisterMode(worker);
        return "redirect:/security/workers/edit/" + id;
    }
}

