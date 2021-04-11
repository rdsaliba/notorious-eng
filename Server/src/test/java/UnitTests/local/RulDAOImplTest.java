package UnitTests.local;

import local.RulDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        //assertNull(estimate);
    }
}