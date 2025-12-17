package com.nox.login.decision;

import com.nox.login.decision.DecisionLogin;
import com.nox.login.repository.EventoLoginRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MotorDecisionCuervo
{
    private final EventoLoginRepository eventoRepository;

    //Reglas iniciales(luego seran adaptativas)
    private static final int MAX_FALLOS = 5;
    private static final int MINUTOS_OBSERVACION = 10;

    public MotorDecisionCuervo(EventoLoginRepository eventoRepository)
    {
        this.eventoRepository = eventoRepository;
    }

    public DecisionLogin evaluarRiesgo(String ipAddress)
    {
        LocalDateTime desde = LocalDateTime.now().minusMinutes(MINUTOS_OBSERVACION);

        long fallosRecientes = eventoRepository.contarFallosRecientesPorIp(ipAddress, desde);

        if(fallosRecientes >= MAX_FALLOS)
        {
            return DecisionLogin.BLOQUEAR_TEMPORAL;
        }

        return DecisionLogin.PERMITIR;
    }
}
