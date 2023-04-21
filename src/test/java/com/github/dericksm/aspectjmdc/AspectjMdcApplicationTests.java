package com.github.dericksm.aspectjmdc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

@SpringBootTest
@AutoConfigureMockMvc
class AspectjMdcApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(LogController.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @DisplayName("Should log annotated parameters")
    @Test
    public void shouldLogAnnotatedParameters() throws Exception {
        final String paramOne = "one";
        final String valueOne = "foo";

        final String paramTwo = "two";
        final String valueTwo = "dumb";
        this.mockMvc.perform(MockMvcRequestBuilders.get("/log-parameter").param(paramOne, valueOne).param(paramTwo, valueTwo)).andDo(print()).andExpect(status().isOk());

        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals(1, logsList.size());
        Assertions.assertEquals(1, logsList.get(0).getMDCPropertyMap().size());
        Assertions.assertEquals(valueOne, logsList.get(0).getMDCPropertyMap().get(paramOne));
    }

    @DisplayName("Should log annotated method")
    @Test
    public void shouldLogAnnotatedMethod() throws Exception {
        final String paramOne = "one";
        final String valueOne = "foo";

        final String paramTwo = "two";
        final String valueTwo = "dumb";
        this.mockMvc.perform(MockMvcRequestBuilders.get("/log-method").param(paramOne, valueOne).param(paramTwo, valueTwo)).andDo(print()).andExpect(status().isOk());

        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals(1, logsList.size());
        Assertions.assertEquals(2, logsList.get(0).getMDCPropertyMap().size());
        Assertions.assertEquals(valueOne, logsList.get(0).getMDCPropertyMap().get(paramOne));
        Assertions.assertEquals(valueTwo, logsList.get(0).getMDCPropertyMap().get(paramTwo));
    }
}
