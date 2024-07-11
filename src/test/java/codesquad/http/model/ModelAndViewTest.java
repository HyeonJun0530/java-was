package codesquad.http.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelAndViewTest {

    @Test
    @DisplayName("ModelAndView 객체를 생성할 수 있다.")
    void create() {
        ModelAndView modelAndView = new ModelAndView();
        assertNotNull(modelAndView);
    }

    @Test
    @DisplayName("ModelAndView 객체에 model을 추가할 수 있다.")
    void addObject() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("key", "value");

        assertAll(() -> assertTrue(modelAndView.containsAttribute("key")),
                () -> assertEquals("value", modelAndView.getObject("key").toString())
        );
    }

    @Test
    @DisplayName("ModelAndView 객체에 viewName을 추가할 수 있다.")
    void setViewName() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");

        assertEquals("index.html", modelAndView.getViewName());
    }

}
