package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTests {

    @Test
    public void deleteTest() {
        DeleteService deleteService = new DeleteService();
        Assertions.assertDoesNotThrow(() -> {});
        Assertions.assertTrue(deleteService.deleteAll().success());
    }


}
