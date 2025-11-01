package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;

class BlockedDatesServiceTest {

    private BlockedDatesRepositoryPort repositoryMock;

    private BlockedDatesService service;

    @BeforeEach
    void setUp() {
        repositoryMock = mock(BlockedDatesRepositoryPort.class);
    }


    private void setUpFixedClockForService(String instantToParse) {
        Clock fixedClock = Clock.fixed(Instant.parse(instantToParse), ZoneId.systemDefault());

        service = new BlockedDatesService(repositoryMock, fixedClock);
    }


    @Test
    void getMonthToUpdate_DateOctober15_ShouldReturnNovember() {
        setUpFixedClockForService("2025-08-15T00:00:00Z");

        YearMonth result = service.getMonthToUpdate();
        YearMonth expectedResult = YearMonth.of(2025, 11);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }


    @Test
    void getMonthToUpdate_DateOctober16_ShouldReturnDecember() {
        setUpFixedClockForService("2025-08-16T00:00:00Z");

        YearMonth result = service.getMonthToUpdate();
        YearMonth expectedResult = YearMonth.of(2025, 12);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}
