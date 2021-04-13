package UnitTests.local;

import local.RulDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RulDAOImplTest {
    private RulDAOImpl rulDAO;
    @Before
    public void setUp() {
        rulDAO = new RulDAOImpl();
    }

    @After
    public void tearDown() {
        rulDAO = null;
    }

    @Test
    public void getLatestRUL() {
        //Continue
        Double estimate = rulDAO.getLatestRUL(5);
        assertEquals(-100000.0, estimate, 0.01);
    }
}