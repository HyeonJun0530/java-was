package codesquad.http.adapter;

import codesquad.app.domain.User;
import codesquad.http.adapter.renderer.ArticleRenderer;
import codesquad.http.adapter.renderer.UserListRenderer;
import codesquad.http.exception.NotFoundException;
import codesquad.http.model.ModelAndView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpResponseAdapterTest {

    @Test
    @DisplayName("TemplateAdapter를 지원하는지 확인한다.")
    void isSupportTemplateAdapter() {
        Object object = new ModelAndView();
        Object object1 = new String();
        HttpResponseAdapter templateAdapter = new TemplateAdapter(List.of(new UserListRenderer(), new ArticleRenderer()));

        Assertions.assertAll(
                () -> assertTrue(templateAdapter.isSupport(object)),
                () -> assertFalse(templateAdapter.isSupport(object1))
        );
    }

    @Test
    @DisplayName("TemplateAdapter를 통해 HttpResponse를 생성한다.")
    void adaptTemplateAdapter() {
        User user1 = new User.Builder()
                .userId("test")
                .password("test")
                .name("test")
                .email("test@test.com")
                .build();

        User user2 = new User.Builder()
                .userId("javajigi")
                .password("password")
                .name("박재성")
                .email("javajigi@1.com")
                .build();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/user/userList.html");
        mav.addObject("users", List.of(user1, user2));
        HttpResponseAdapter templateAdapter = new TemplateAdapter(List.of(new UserListRenderer()));

        assertAll(
                () -> assertTrue(templateAdapter.adapt(mav).toString().contains("OK")),
                () -> assertTrue(templateAdapter.adapt(mav).toString().contains("text/html"))
        );
    }

    @Test
    @DisplayName("TemplateAdapter를 통해 HttpResponse를 생성한다. - 지원하지 않으면 예외를 뱉는다")
    void adaptTemplateAdapter_invalidViewName() {
        User user1 = new User.Builder()
                .userId("test")
                .password("test")
                .name("test")
                .email("test@test.com")
                .build();

        User user2 = new User.Builder()
                .userId("javajigi")
                .password("password")
                .name("박재성")
                .email("javajigi@1.com")
                .build();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("invalid");
        mav.addObject("users", List.of(user1, user2));
        HttpResponseAdapter templateAdapter = new TemplateAdapter(List.of(new UserListRenderer()));

        assertThatThrownBy(() -> templateAdapter.adapt(mav))
                .isInstanceOf(NotFoundException.class);
    }


}
