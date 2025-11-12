package com.ruiz.prestamos.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruiz.prestamos.persistence.entity.Notificacion;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.enums.EstadoPago;
import com.ruiz.prestamos.persistence.repository.NotificacionRepository;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.service.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificacionScheduler {

    private final PagoRepository pagoRepository;
    private final NotificacionRepository notificacionRepository;
    private final EmailService emailService;
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate hoy = LocalDate.now();

    public NotificacionScheduler(
            PagoRepository pagoRepository,
            NotificacionRepository notificacionRepository,
            EmailService emailService) {
        this.pagoRepository = pagoRepository;
        this.notificacionRepository = notificacionRepository;
        this.emailService = emailService;
    }

    // Ejecuta cada día a las 8:00 a.m.  (s m  h D M W)
    @Scheduled(cron = "0 27 15 * * *")
    public void notificarPagosAtrasados() {
        System.out.println("=== Revisando pagos atrasados: " + LocalDateTime.now() + " ===");
        List<Pago> atrasados = pagoRepository
                .findByEstadoAndFechaVencimientoBefore(EstadoPago.PENDIENTE, LocalDate.now());
        System.out.println("pagos atrasados : "+atrasados.size());
        for (Pago pago : atrasados) {
            var prestamo = pago.getPrestamo();

            // Evita duplicar notificaciones si ya se envió hoy
            boolean yaNotificado = notificacionRepository.existsByIdPrestamoAndDescripcionAndFechaEnvioBetween(
                    prestamo.getId(), "Pago Atrasado",
                    hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());

            if (yaNotificado)  continue;
            String asunto = "Pago atrasado - Préstamo #" + prestamo.getId();
            String mensaje = "Se detectó un pago vencido correspondiente al préstamo #:"+ prestamo.getId()+" de fecha :" + prestamo.getFechaInicio().format(formato) + ".Si ud. ya cancelo el pago, por favor ignore este mensaje.";

            enviarYRegistrar(prestamo.getPrestatario().getEmail(), prestamo.getId(), asunto, mensaje);
            enviarYRegistrar(prestamo.getPrestador().getEmail(), prestamo.getId(), asunto, mensaje);

        }
    }

    private void enviarYRegistrar(String email, Integer idPrestamo, String asunto, String mensaje) {
        emailService.enviarEmail(email, asunto, mensaje);

        Notificacion notificacion = new Notificacion();
        notificacion.setDescripcion("Pago Atrasado");
        notificacion.setIdPrestamo(idPrestamo);
        notificacion.setDestinatarioEmail(email);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEstado("ENVIADO");

        notificacionRepository.save(notificacion);
    }

}
