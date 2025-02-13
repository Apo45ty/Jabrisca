package edu.uprm.ece.icom4015.jabrisca.test.unit;

import edu.uprm.ece.icom4015.jabrisca.client.JabriscaController;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JabriscaControllerTest {

    private static String
            ComponentName = "MyComponent";;

    @Test
    public void test_setQueue_HappyEnqueueMousePressed(){
        //Prepare
        BlockingQueue queue = new ArrayBlockingQueue(100);
        JabriscaController controller = new JabriscaController(queue);

        //Execute
        controller.mousePressed(new MouseEvent(getCustomComponent(), 0, 0, 0, 0, 0, 0, false, 0));
        String result = queue.poll().toString();

        //Test
        assert (result.equals(
                "mousePressed-"  +ComponentName));
    }
    @Test
    public void test_setQueue_HappyEnqueueMouseReleased(){
        //Prepare
        BlockingQueue queue = new ArrayBlockingQueue(100);
        JabriscaController controller = new JabriscaController(queue);

        //Execute
        controller.mouseReleased(new MouseEvent(getCustomComponent(), 0, 0, 0, 0, 0, 0, false, 0));
        String result = queue.poll().toString();

        //Test
        assert (result.equals(
                "mouseReleased-"+ComponentName));
    }
    @Test
    public void test_setQueue_HappyEnqueueMouseEnter(){
        //Prepare
        BlockingQueue queue = new ArrayBlockingQueue(100);
        JabriscaController controller = new JabriscaController(queue);

        //Execute
        controller.mouseEntered(new MouseEvent(getCustomComponent(), 0, 0, 0, 0, 0, 0, false, 0));
        String result = queue.poll().toString();

        //Test
        assert (result.equals(
                "mouseEnter-" +ComponentName));
    }
    @Test
    public void test_setQueue_HappyEnqueueMouseExited(){
        //Prepare
        BlockingQueue queue = new ArrayBlockingQueue(100);
        JabriscaController controller = new JabriscaController(queue);

        //Execute
        controller.mouseExited(new MouseEvent(getCustomComponent(), 0, 0, 0, 0, 0, 0, false, 0));
        String result = queue.poll().toString();

        //Test
        assert (result.equals(
                "mouseExited-" +ComponentName));
    }
    @Test
    public void test_setQueue_HappyEnqueueMouseExitedFiveTimes(){
        //Prepare
        BlockingQueue queue = new ArrayBlockingQueue(100);
        JabriscaController controller = new JabriscaController(queue);

        //Execute
        for(int i=0;i<5;i++) {
            controller.mouseExited(new MouseEvent(getCustomComponent(), 0, 0, 0, 0, 0, 0, false, 0));
            String result = queue.poll().toString();

            //Test
            assert (result.equals(
                    "mouseExited-" + ComponentName));
        }
    }
    private static Component getCustomComponent() {
        return new Component() {
            @Override
            public String getName() {
                return ComponentName;
            }
        };
    }
}
