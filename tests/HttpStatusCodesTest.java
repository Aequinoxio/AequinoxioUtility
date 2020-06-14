import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpStatusCodesTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getCode() {
        for(HttpStatusCodes httpStatusCodes:HttpStatusCodes.values()){
            System.out.println(httpStatusCodes.getCode()+" - "+httpStatusCodes.getDesc());
            System.out.println(HttpStatusCodes.intToHttpStatusCode(httpStatusCodes.getCode()));
        }

    }
}