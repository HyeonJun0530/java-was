package codesquad.http.adapter.renderer;

import codesquad.app.domain.User;
import codesquad.http.model.ModelAndView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ViewRendererTest {

    @Test
    @DisplayName("UserListRenderer를 통해 동적으로 랜더링 한다.")
    void renderUserList() {
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
        ViewRenderer viewRenderer = new UserListRenderer();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/user/userList.html");
        mav.addObject("users", List.of(user1, user2));
        String userList = viewRenderer.render(mav);

        assertNotNull(userList);

        assertAll(() -> assertTrue(userList.contains("test")),
                () -> assertTrue(userList.contains("javajigi")),
                () -> assertTrue(userList.contains("박재성"))
        );
    }

    @Test
    @DisplayName("UserListRenderer를 지원하는지 확인한다.")
    void isSupportUserList() {
        ViewRenderer viewRenderer = new UserListRenderer();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/user/userList.html");
        mav.addObject("users", List.of(new User.Builder().build()));

        assertTrue(viewRenderer.isSupport(mav));
    }

}
