package codesquad.http.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelAndView {

    private Map<String, Object> model = new ConcurrentHashMap<>();
    private String viewName;
    private boolean redirect;

    public ModelAndView() {
    }

    public ModelAndView(final Map<String, Object> model, final String viewName) {
        this.model = model;
        this.viewName = viewName;
    }

    public Object addObject(final String attribute, final Object value) {
        return value != null ? model.put(attribute, value) : model.remove(attribute);
    }

    public Object getObject(final String attribute) {
        return model.get(attribute);
    }

    public boolean containsAttribute(final String attribute) {
        return model.containsKey(attribute);
    }

    public String getViewName() {
        return viewName;
    }

    //TODO : redirect 추가
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }

}
