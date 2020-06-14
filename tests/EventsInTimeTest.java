import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

class EventsInTimeTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void restartTimer() {
    }

    @Test
    void testTimer() {
        test1();
        test2();

    }

    private void test1() {
        float temp;
        EventsInTime eventsInTime;
        eventsInTime = new EventsInTime(false);
        int initEvents = 10;
        int sleepMillis = 1000;
        System.out.println(String.format("Genero %d eventi iniziali prima di misurare l'eps", initEvents));
        for (int i = 0; i < initEvents; i++) {
            temp = eventsInTime.incrementEventCounterAndGetOverallEPS();
        }
        try {
            System.out.println(String.format("sleeping %d millis...", sleepMillis));
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Nuovo evento
        temp = eventsInTime.incrementEventCounterAndGetOverallEPS();
        System.out.println(String.format("Eventi attualmente contati: %d - EPS: %f", eventsInTime.getCurrentEventsCounter(), temp));
        System.out.println(String.format("Tempo trascorso (human readable): %s", EventsInTime.ConvertDeltaMillisToHuman(eventsInTime.getDeltaCurrentMillisFromStart(),true)));
    }

    private void test2() {
        float temp;
        int sleepMillis = 5000;
        EventsInTime eventsInTime = new EventsInTime(false);
        try {
            System.out.println(String.format("sleeping %d millis...", sleepMillis));
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        temp=eventsInTime.incrementEventCounterAndGetOverallEPS(Long.MAX_VALUE/3);
        System.out.println(String.format("Eventi attualmente contati: %d - EPS: %f", eventsInTime.getCurrentEventsCounter(), temp));
        System.out.println(String.format("Tempo trascorso (human readable): %s", EventsInTime.ConvertDeltaMillisToHuman(eventsInTime.getDeltaCurrentMillisFromStart(),false)));
        long remTime = eventsInTime.estimateRemainingTime();
        System.out.println(String.format("Tempo rimanente (millis): %s - %s", remTime,eventsInTime.estimateRemainingTime2Human()));

    }

    @Test
    void test3() throws InterruptedException {
        float temp;
        int sleepMillisMax = 383;
        int eventi=190;
        EventsInTime eventsInTime = new EventsInTime(false);
        Random random = new Random();

        int sumSleepTime=0;
        long startTimer=eventsInTime.getStartTime();
        long sumEventTime;

        for (int i=0;i<eventi;i++){
            int sleep = random.nextInt(sleepMillisMax);
            sumSleepTime+=sleep;
            System.out.print(" ("+i+") - Sleeping (ms): "+sleep);
            Thread.sleep(sleep);

            // Nuovo evento
            temp=eventsInTime.incrementEventCounterAndGetOverallEPS();

            sumEventTime = (eventsInTime.getLatestUpdateEventsMillis() - startTimer);
            System.out.print(" - EventTime totali (ms): "+sumEventTime);

            System.out.print(String.format(" - Eventi attualmente contati: %d - EPS: %f", eventsInTime.getCurrentEventsCounter(), temp));
            //System.out.println(String.format(" - Tempo trascorso (human readable): %s", EventsInTime.ConvertDeltaMillisToHuman(eventsInTime.getDeltaMillisFromStart())));
            System.out.println(String.format(" - Tempo trascorso: %s", eventsInTime.getDeltaCurrentMillisFromStart()));
        }

        System.out.println(String.format("Generati %d eventi in %d millis - EPS %f",eventi,sumSleepTime,((float)eventi)/sumSleepTime*1000.0));


    }
}