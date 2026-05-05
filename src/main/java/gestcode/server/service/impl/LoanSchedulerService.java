package gestcode.server.service.impl;

import gestcode.server.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servei per programar la revisió d'estats de préstecs.
 * 
 * @author Jordi Verdalet Carrera
 */
@Service
public class LoanSchedulerService {

    @Autowired
    private LoanService loanService;

    /**
     * S'executa automàticament cada mitjanit.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyStatusUpdate() {
        System.out.println("Scheduler: Executant l'actualització d'estats de préstecs a mitjanit.");
        loanService.checkAndUpdateLoanStatuses();
    }

    /**
     * S'executa sempre automàticament just quan el servidor engega amb èxit.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void executeOnStartup() {
        System.out.println("Startup: Executant l'actualització d'estats de préstecs en arrencar l'aplicació.");
        loanService.checkAndUpdateLoanStatuses();

        System.out.println("Startup: Sincronitzant l'estoc dels llibres disponibles.");
        loanService.syncBookAvailableCopies();
        System.out.println("Startup: Aplicació totalment carregada i sincronitzada.");
    }
}
