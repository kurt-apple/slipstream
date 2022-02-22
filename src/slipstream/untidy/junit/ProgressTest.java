package slipstream.untidy.junit;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ProgressTest {
    @Test
    public void areNoTODOs() {
        int i = 0;
        i++; //TODO: check TaskViewController for revision opportunities
        i++; //TODO: get other functionality more stable before adding new features.
        i++; //TODO: consider making a copy constructor for Task
        i++; //TODO: switch to sandboxed editing
        i++; //TODO: review, clean, test new sandboxed editing
        if(i != 0) fail(i + " TODOs are unfinished for TaskDescIteratorTest.");
    }
}
