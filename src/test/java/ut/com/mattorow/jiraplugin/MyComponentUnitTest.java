package ut.com.mattorow.jiraplugin;

import org.junit.Test;
import com.mattorow.jiraplugin.api.MyPluginComponent;
import com.mattorow.jiraplugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {
    @Test
    public void testMyName() {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent", component.getName());
    }
}