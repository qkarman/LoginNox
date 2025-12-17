package com.nox.login.service;

import com.nox.login.decision.DecisionLogin;
import com.nox.login.decision.MotorDecisionCuervo;
import com.nox.login.evento.EventoLogin;
import com.nox.login.repository.EventoLoginRepository;
import org.springframework.stereotype.Service;

@Service
public class NoxLoginService
{
    private final EventoLoginRepository eventoRepository;
    private final MotorDecisionCuervo motorCuervo;

    public NoxLoginService(EventoLoginRepository eventoRepository, MotorDecisionCuervo motorCuervo)
    {
        this.eventoRepository = eventoRepository;
        this.motorCuervo = motorCuervo;
    }

    public DecisionLogin procesarIntentoLogin(String email, String ipAddress, boolean exitoso)
    {
        //1.- Registrar evento
        EventoLogin evento = new EventoLogin(email, ipAddress, exitoso);
        eventoRepository.save(evento);

        // 2, Evaluar riesgo
        return motorCuervo.evaluarRiesgo(ipAddress);
    }
}
