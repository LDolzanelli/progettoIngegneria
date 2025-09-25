package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.*;

class BlockedDatesServiceTest {

    private BlockedDatesRepositoryPort repositoryMock;

    @BeforeEach
    void setUp() {
         repositoryMock = Mockito.mock(BlockedDatesRepositoryPort.class);
    }

    @Test
    void getMonthToUpdate_shouldReturnNovemberIfDateOctober15() {
        Clock fixedClock = Clock.fixed(Instant.parse("2025-08-15T00:00:00Z"), ZoneId.systemDefault());

        BlockedDatesService service = new BlockedDatesService(repositoryMock, fixedClock);

        YearMonth result = service.getMonthToUpdate();
        YearMonth expectedResult = YearMonth.of(2025, 11);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getMonthToUpdate_shouldReturnDecemberIfDateOctober16() {
        Clock fixedClock = Clock.fixed(Instant.parse("2025-08-16T00:00:00Z"), ZoneId.systemDefault());

        BlockedDatesService service = new BlockedDatesService(repositoryMock, fixedClock);

        YearMonth result = service.getMonthToUpdate();
        YearMonth expectedResult = YearMonth.of(2025, 12);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

}