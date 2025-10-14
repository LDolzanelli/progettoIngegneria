package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitDayErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.VisitDayException;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visit.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class VisitDayService implements VisitDaysUseCase {

    private final VisitRepositoryPort visitRepository;
    private final VisitTypeRepositoryPort visitTypeRepository;
    private final BlockedDatesRepositoryPort blockedDatesRepository;
    private final GetUserInfoUseCase userInfoService;
    private final Clock clock;

    @Override
    /*@ also
      @ requires visitRepository != null && visitTypeRepository != null && 
      @          blockedDatesRepository != null && clock != null;
      @ ensures (\forall VisitType vt; visitTypeRepository.findAll().contains(vt);
      @          (\exists Visit v; visitRepository.findAll().contains(v) &&
      @           v.getVisitType().equals(vt) && v.getDate().getMonthValue() == month &&
      @           v.getVisitStatus() == VisitStatus.PROPOSED));
      @*/
    public void createDefaultVisitDays(int month) {

        BlockedDates blockedDates = blockedDatesRepository.loadAll();

        LocalDate now = LocalDate.now(clock);
        int year = now.getYear();

        if (month != now.plusMonths(2).getMonthValue() || now.getDayOfMonth() < 15) {
            throw new VisitDayException(VisitDayErrorCode.CANT_BE_CREATED_AT_THIS_DATE,
                    "non è possibile creare visite per il mese i + 2 se non è dopo il 15 del mese corrente");
        }



        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        List<VisitType> visitTypes = visitTypeRepository.findAll() //
                        .stream() //
                        .filter(visitType -> !visitType.getStartDate().isAfter(endOfMonth) //
                        && !visitType.getEndDate().isBefore(startOfMonth)) //
                        .toList();

        for (VisitType visitType : visitTypes) {
            createVisitsForType(visitType, startOfMonth, endOfMonth, blockedDates);
        }
    }


    private void createVisitsForType(VisitType visitType, LocalDate startOfMonth, LocalDate endOfMonth,
            BlockedDates blockedDates) {
        LocalDate current = startOfMonth;
        while (!current.isAfter(endOfMonth)) {
            if (blockedDates.isBlocked(current)) {
                current = current.plusDays(1);
                continue;
            }
            if (!current.isBefore(visitType.getStartDate()) && !current.isAfter(visitType.getEndDate())) {
                DaysOfWeek day = DaysOfWeek.valueOf(current.getDayOfWeek().name());
                if (visitType.getDaysAvailable().contains(day)) {
                    Visit visit = new Visit(current, null, visitType, List.of(), VisitStatus.PROPOSED);
                    visitRepository.save(visit);
                }
            }
            current = current.plusDays(1);
        }
    }


    @Override
    /*@ also
      @ ensures (\forall Visit v; v.getDate().getMonthValue() == month;
      @          visitRepository.findAll().contains(v) && v.getVisitStatus() != null);
      @*/
    public void updateVisitDays(int month) {
        // TODO: implementare la logica per aggiornare i giorni di visita (con il piano
        // di visita)
    }


    @Override
    /*@ also
      @ ensures \result.size() >= 0;
      @ ensures (\forall Visit v; \result.contains(v);
      @          v.getVolunteer().getNickname().equals(volunteerNickname) &&
      @          v.getVisitStatus() == VisitStatus.CONFIRMED);
      @ ensures userInfoService.findByNickname(volunteerNickname).isPresent();
      @ ensures userInfoService.findByNickname(volunteerNickname).get().getRole() == Role.VOLUNTEER;
      @*/
    public List<Visit> getConfirmedVisitsPerVolunteer(String volunteerNickname) {
        User volunteer = userInfoService.findByNickname(volunteerNickname)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, volunteerNickname));

        if (volunteer.getRole() != Role.VOLUNTEER)
            throw new UserException(UserErrorCode.UNAUTHORIZED_REQUEST,
                    "L'utente con nickname " + volunteerNickname + " non è un volontario");

        return visitRepository.findByVolunteer(volunteerNickname).stream()
                .filter(visit -> visit.getVisitStatus() == VisitStatus.CONFIRMED).toList();
    }


    @Override
    /*@ also
      @ ensures \result != null;
      @ ensures \result.getId() == visitId;
      @ ensures visitRepository.findById(visitId).isPresent();
      @*/
    public Visit getVisitById(int visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDayException(VisitDayErrorCode.VISIT_NOT_FOUND,
                        "Visita non trovata con id: " + visitId));
    }

}
