package gestcode.server.controller;

import gestcode.server.dto.request.LoanRequestDTO;
import gestcode.server.dto.response.LoanListResponseDTO;
import gestcode.server.dto.response.LoanResponseDTO;
import gestcode.server.model.entity.User;
import gestcode.server.model.enums.LoanStatus;
import gestcode.server.service.LoanService;
import gestcode.server.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

/**
 * Controlador REST per a la gestió de préstecs.
 * 
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstecs", description = "Endpoints de gestió de préstecs de llibres")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Mètode auxiliar per obtenir l'usuari actual autenticat.
     * Necessita l'obtenció completa de User per passar-la al service.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuari no autenticat adequadament"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Crear préstec", description = "Concedeix un préstec del llibre sol·licitat si es compleixen les condicions.")
    @ApiResponse(responseCode = "201", description = "Préstec creat correctament")
    @ApiResponse(responseCode = "400", description = "Límit assolit o llibre no disponible")
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody LoanRequestDTO requestDTO) {
        User user = getCurrentUser();
        LoanResponseDTO response = loanService.createLoan(requestDTO, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Retornar préstec", description = "Marca el préstec com a retornat.")
    @ApiResponse(responseCode = "200", description = "S'ha retornat correctament")
    public ResponseEntity<LoanResponseDTO> returnLoan(@PathVariable Long id) {
        User user = getCurrentUser();
        LoanResponseDTO response = loanService.returnLoan(id, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Llistar tots els préstecs (Admin)", description = "Servei d'ús administratiu amb filtres i paginació.")
    @ApiResponse(responseCode = "200", description = "Llista retornada satisfactòriament")
    public ResponseEntity<LoanListResponseDTO> getAllLoans(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        LoanListResponseDTO response = loanService.getAllLoans(userId, bookId, status, pageNo, pageSize, sortBy,
                sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-loans")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Llistar préstecs de l'usuari identificat", description = "Obté tots els préstecs històrics i actuals de qui fa la crida.")
    public ResponseEntity<LoanListResponseDTO> getMyLoans(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        User user = getCurrentUser();
        LoanListResponseDTO response = loanService.getMyLoans(user, pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-loans/overdue")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Llistar préstecs fora de termini de l'usuari")
    public ResponseEntity<LoanListResponseDTO> getMyOverdueLoans(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        User user = getCurrentUser();
        List<LoanStatus> statuses = Arrays.asList(LoanStatus.FORA_DE_LIMIT);
        LoanListResponseDTO response = loanService.getMyLoansByStatuses(user, statuses, pageNo, pageSize, sortBy,
                sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/my-loans/near-due")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Llistar préstecs prop a caducar de l'usuari")
    public ResponseEntity<LoanListResponseDTO> getMyNearDueLoans(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        User user = getCurrentUser();
        List<LoanStatus> statuses = Arrays.asList(LoanStatus.PROXIM_A_CADUCAR);
        LoanListResponseDTO response = loanService.getMyLoansByStatuses(user, statuses, pageNo, pageSize, sortBy,
                sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Llistar tots els préstecs caducats (Admin)")
    public ResponseEntity<LoanListResponseDTO> getAllOverdueLoans(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        List<LoanStatus> statuses = Arrays.asList(LoanStatus.FORA_DE_LIMIT);
        LoanListResponseDTO response = loanService.getAllLoansByStatuses(statuses, pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all-near-due")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Llistar tots els préstecs que caduquen properament (Admin)")
    public ResponseEntity<LoanListResponseDTO> getAllNearDueLoans(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "loanDate", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        List<LoanStatus> statuses = Arrays.asList(LoanStatus.PROXIM_A_CADUCAR);
        LoanListResponseDTO response = loanService.getAllLoansByStatuses(statuses, pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
