package com.cbms.app;

import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

/** This class is the base class for all automated end-to-end testing on the application (UI testing). TestFX
 * is the framework used for automated testing on JavaFX and it attaches itself to Junit (see dependencies) for
 * integration with our test runner.
 *
 * @author Jérémie Chouteau
 * */
public class TextFXBase extends ApplicationTest {
    /** All test cases have some common elements to them
     * '@Before' represents the set-up of the application with the correct initial conditions and is run before each
     * test case
     * '@Override' is used to substitute the start function with a personalized one for our application
     * '@After' is run after each test case to free up resources and allow them to be correctly re-allocated for the
     * next test case
     * '@Test' represents 1 test case
     *
     * @author Jérémie Chouteau
     * */
    @Before
    public void setUp () throws Exception {
        ModelController modelController = ModelController.getInstance();
        modelController.initializer();
        ApplicationTest.launch(Main.class);
    }

    @Override
    public void start (Stage stage) {
       stage.show();
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void ScrollDownSystems () {
        scroll(5, VerticalDirection.DOWN);
    }
}