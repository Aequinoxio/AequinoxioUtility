import java.util.concurrent.TimeUnit;

/**
 * Tiene traccia degli eventi e calcola il tempo a finire
 */
public class EventsInTime {
    //private long m_millisLast;
    private long m_millisCurrent;

    private long m_startTime;
    private long m_startValue;
    private long m_maxValue;

    private boolean m_wrapOnOverflow;

    private long m_events;
    private float m_eps=0.0f;

    private static final String COMPACT_FORMAT="%02d:%02d:%02d.%03d";
    private static final String EXTENDED_FORMAT="%02d giorni, %02d ore, %02d min, %02d sec, %03d millis";

    /**
     * Costruttore.
     * Imposto startValue a zero e maxValue a Long.MaxValue
     *
     * @param wrap true se devo ripartire da zero quando raggiungo il maxValue
     */
    public EventsInTime(boolean wrap) {
        //m_millisLast = System.currentTimeMillis();
        m_startTime = System.currentTimeMillis();
        m_millisCurrent=m_startTime+1; // Aggiungo un millisecondo per sicurezza ed evitare che abbia una divisione per zero
        m_startValue=0;
        m_maxValue=Long.MAX_VALUE;
        m_wrapOnOverflow=wrap;
        m_events = 0;
    }

    public long getStartTime() {
        return m_startTime;
    }

    /**
     * Costruttore
     *
     * @param startValue Valore iniziale in millisecondi da cui parte il calcolo degli eventi
     * @param maxValue Valore finale in millisecondi degli eventi da contare
     * @param wrap true se devo ripartire da zero quando raggiungo il maxValue
     */
    public EventsInTime(long startValue, long maxValue, boolean wrap){
        this(wrap); // Constructor with no parameters;
        m_startValue=startValue;
        m_maxValue=maxValue;
//        m_events = m_maxValue;
    }

    /**
     * Reinizializzo l'oggetto
     */
    public void restartTimer(){
        //m_millisLast = System.currentTimeMillis();
        m_millisCurrent = System.currentTimeMillis();
        m_events=0;
        m_eps=0;
    }


    /**
     * Calcola differenza temporale in millisecondi tra il momento corrente ed il momendo di avvio dell'oggetto
     * @return Ritorna la differenza in millisecondi tra il momento attuale e quello di avvio dell'oggetto
     */
    public long getDeltaCurrentMillisFromStart(){
        return (System.currentTimeMillis()-m_startTime);
    }

    /**
     * Numero di eventi correnti
     * @return Ritorna il numero di eventi correnti impostati
     */
    public long getCurrentEventsCounter(){
        return m_events;
    }

    /**
     * Tempo in millisecondi dell'ultimo evento di update
     * @return Il tempo dell'ultimo update in millisecondi
     */
    public long getLatestUpdateEventsMillis(){
        return m_millisCurrent;
    }


//    public float incrementEventCounterAndGetEPS(){
//        m_events++;
//
//        m_millisCurrent = System.currentTimeMillis();
//
//        // Ogni 500 millisecondi calcolo il numero di eventi per secondo
//        if ((m_millisCurrent - m_millisLast) > 500) {
//            m_eps= ((float) m_events) / (m_millisCurrent - m_millisLast)*1000.0f;
//            m_events=0;
//            m_millisLast = m_millisCurrent;
//        }
//        return m_eps;
//    }

    /**
     * Aggiunge un singolo evento al numero di eventi già considerati e ritorna gli eventi al secondo complessivi fino al momento
     * della chiamata
     * @return Numero di eventi al secondo calcolato con i valori passati ed il tempo preso al momento della chiamata
     */
    public float incrementEventCounterAndGetOverallEPS() {
        m_events++;
        if (m_events>m_maxValue){
            if (m_wrapOnOverflow){
                m_events=0;
            } else {
                m_events=m_maxValue;
            }
        }
        m_millisCurrent=System.currentTimeMillis();
        m_eps= ((float) m_events) / (m_millisCurrent - m_startTime)*1000.0f;
        return m_eps;
    }

    /**
     * Aggiunge al numero di eventi già considerati il parametro passato e ritorna gli eventi al secondo complessivi fino al momento
     * della chiamata
     * @param events Numero di eventi da aggiungere al conteggio
     * @return Numero di eventi al secondo calcolato con i valori passati ed il tempo preso al momento della chiamata
     */
    public float incrementEventCounterAndGetOverallEPS(long events) {
        m_events +=(events-1);
        return incrementEventCounterAndGetOverallEPS();
    }

    /**
     * Imposta il numero di eventi sovrascrivendo il numero già memorizzato e ritorna gli eventi al secondo complessivi fino al momento
     * della chiamata
     * @param events Numero di eventi da impostare per il conteggio
     * @return Eventi al secondo calcolato con i valori passati ed il tempo preso al momento della chiamata
     */
    public float setEventCounterAndGetOverallEPS(long events) {
        m_events =(events-1);
        return incrementEventCounterAndGetOverallEPS();
    }

    /**
     * Ritorna la stima a finire in millisecondi sulla base dei valori impostati alla creazione della classe (startValue e maxValue)
     * basandosi sul tempo di creazione iniziale della classe e del tempo trascorso fino ad ora. Il valore currentValue viene usato
     * come milestone per calcolare il tempo a finire.
     * TODO: Controllarlo in quanto sto cambiando la logica di calcolo (20190714)
     *
     * @return Stima a finire in millisecondi
     */
//    public long estimateRemainingTime(long currentValue) {
//        m_millisCurrent = System.currentTimeMillis();
//        long deltaValue = currentValue - m_startValue;
//        long deltaTime = m_millisCurrent - m_startTime;
//        if (deltaValue <= 0 || deltaTime <= 0) {
//            return -1;
//        }
//        return (m_maxValue - currentValue) * deltaTime / deltaValue;
//    }
    public long estimateRemainingTime() {

        long deltaValue = m_events- m_startValue;
        //long deltaTime = m_millisCurrent - m_startTime;           // Considero il tempo dedll'ultimo aggiornamento
        long deltaTime = System.currentTimeMillis() - m_startTime; // Calcolo il tempo attuale
        if (deltaValue <= 0 || deltaTime <= 0) {
            return -1;
        }

        return (long)((1.0*deltaTime*(m_maxValue-m_startValue))/deltaValue - deltaTime);
    }

    /**
     * Ritorna il il tempo a finire rappresentato come stringa human readable (giorni, ore, minuti, secondi e millisecondi)
     * Il calcolo è effettuato sulla base del numero di eventi impostati prima della chiamata.
     * Il numero complessivo degli eventi non cambia così come il tempo dell'ultimo aggiornamento
     * TODO: in base ai test verificare se il metodo estimateRemainingTime debba considerare il tempo della chiamata o quello dell'ultimo aggiornamento del contatore
     * @return Stringa rappresentante il tempo a finire
     */
    public String estimateRemainingTime2Human() {
        return ConvertDeltaMillisToHuman(estimateRemainingTime(), true);
    }

    /**
     * Converte in formato human readable il tempo in millisecondi in un formato compatto o esteso
     *
     * Formato compatto: HH:mm:ss.millis
     * Formato esteso: dd giorni, hh ore, mm min, ss sec, mmm millis
     *
     * @param timeToBeConverted Tempi in millisecondi da cnvertire
     * @param compact True per una rappresentazione compatta, false per quella estesa
     * @return Tempo in formato human readable
     */
    public static String ConvertDeltaMillisToHuman(long timeToBeConverted, boolean compact) {
        String temp_returnFormatString;
        if (compact){
            if (TimeUnit.MILLISECONDS.toDays(timeToBeConverted)!=0){
                temp_returnFormatString = String.format("%d - ",TimeUnit.MILLISECONDS.toDays(timeToBeConverted));
            } else {
                temp_returnFormatString ="";
            }
            temp_returnFormatString += String.format(COMPACT_FORMAT,
                    TimeUnit.MILLISECONDS.toHours(timeToBeConverted) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toMinutes(timeToBeConverted) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toSeconds(timeToBeConverted) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toMillis(timeToBeConverted) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeToBeConverted)));
        } else {

            temp_returnFormatString =  String.format(EXTENDED_FORMAT,
                    TimeUnit.MILLISECONDS.toDays(timeToBeConverted),
                    TimeUnit.MILLISECONDS.toHours(timeToBeConverted) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toMinutes(timeToBeConverted) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toSeconds(timeToBeConverted) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToBeConverted)),
                    TimeUnit.MILLISECONDS.toMillis(timeToBeConverted) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeToBeConverted)));
        }

        return temp_returnFormatString;
    }
}
