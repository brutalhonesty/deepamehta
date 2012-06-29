package de.deepamehta.core.service;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Path;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;



// ### TODO: should not be a public class.
public class WebPublishingService {

    // ------------------------------------------------------------------------------------------------------- Constants

    private static final String ROOT_APPLICATION_PATH = "/";

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private DefaultResourceConfig rootApplication = new DefaultResourceConfig();
    private int classCount = 0;         // counts DM root resource and provider classes
    private int singletonCount = 0;     // counts DM root resource and provider instances
    // Note: we count DM resources separately as Jersey adds its own ones to the application.
    // Once the total DM resource count reaches 0 the Jersey servlet is unregistered.

    private ServletContainer jerseyServlet = new ServletContainer(rootApplication);
    private boolean isJerseyServletRegistered = false;

    private HttpService httpService;
    private ServiceRegistration registration;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    public WebPublishingService(BundleContext context, HttpService httpService) {
        try {
            logger.info("Registering Web Publishing service at OSGi framework");
            this.httpService = httpService;
            this.registration = context.registerService(getClass().getName(), this, null);
        } catch (Exception e) {
            // unregister...();     // ### TODO?
            throw new RuntimeException("Registering Web Publishing service at OSGi framework failed", e);
        }
    }

    // ----------------------------------------------------------------------------------------- Package Private Methods

    // === Web Resources ===

    WebResources addWebResources(Bundle bundle, String uriNamespace) {
        try {
            // Note: registerResources() throws org.osgi.service.http.NamespaceException
            httpService.registerResources(uriNamespace, "/web", new BundleHTTPContext(bundle));
            return new WebResources(uriNamespace);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void removeWebResources(WebResources webResources) {
        httpService.unregister(webResources.uriNamespace);
    }

    // === REST Resources ===

    // Note: synchronizing this method prevents creation of multiple Jersey servlet instances due to parallel plugin
    // initialization.
    synchronized RestResource addRestResource(Object resource, Set<Class<?>> providerClasses) {
        addSingleton(resource);
        addClasses(providerClasses);
        logResourceInfo();
        //
        // Note: we must register the Jersey servlet lazily, that is not before any resources or providers
        // are added. An "empty" application would fail (com.sun.jersey.api.container.ContainerException:
        // The ResourceConfig instance does not contain any root resource classes).
        if (!isJerseyServletRegistered) {
            registerJerseyServlet();
        } else {
            reloadJerseyServlet();
        }
        //
        return new RestResource(resource, providerClasses);
    }

    synchronized void removeRestResource(RestResource restResource) {
        removeSingleton(restResource.resource);
        removeClasses(restResource.providerClasses);
        logResourceInfo();
        //
        // Note: once all resources and providers are removed we must unregister the Jersey servlet.
        // Reloading it with an "empty" application would fail (com.sun.jersey.api.container.ContainerException:
        // The ResourceConfig instance does not contain any root resource classes).
        if (!hasResources()) {
            unregisterJerseyServlet();
        } else {
            reloadJerseyServlet();
        }
    }

    // ---

    boolean isRestResource(Object object) {
        return getUriNamespace(object) != null;
    }

    String getUriNamespace(Object object) {
        Path path = object.getClass().getAnnotation(Path.class);
        return path != null ? path.value() : null;
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private Set<Class<?>> getClasses() {
        return rootApplication.getClasses();
    }

    private Set<Object> getSingletons() {
        return rootApplication.getSingletons();
    }

    // ---

    private void addClasses(Set<Class<?>> classes) {
        getClasses().addAll(classes);
        classCount += classes.size();
    }

    private void addSingleton(Object singleton) {
        getSingletons().add(singleton);
        singletonCount++;
    }

    // ---

    private void removeClasses(Set<Class<?>> classes) {
        getClasses().removeAll(classes);
        classCount -= classes.size();
    }

    private void removeSingleton(Object singleton) {
        getSingletons().remove(singleton);
        singletonCount--;
    }

    // ---

    private boolean hasResources() {
        return classCount + singletonCount > 0;
    }

    private void logResourceInfo() {
        logger.fine("##### DM Classes: " + classCount + ", All: " + getClasses().size() + " " + getClasses());
        logger.fine("##### DM Singletons: " + singletonCount + ", All: " + getSingletons().size() + " " +
            getSingletons());
    }

    // ---

    private void registerJerseyServlet() {
        try {
            logger.fine("########## Registering Jersey servlet at HTTP service (URI namespace=\"" +
                ROOT_APPLICATION_PATH + "\")");
            // Note: registerServlet() throws javax.servlet.ServletException
            httpService.registerServlet(ROOT_APPLICATION_PATH, jerseyServlet, null, null);
            isJerseyServletRegistered = true;
        } catch (Exception e) {
            // unregister...();     // ### TODO?
            throw new RuntimeException("Registering Jersey servlet at HTTP service failed", e);
        }
    }

    private void unregisterJerseyServlet() {
        logger.fine("########## Unregistering Jersey servlet at HTTP service (URI namespace=\"" +
            ROOT_APPLICATION_PATH + "\")");
        httpService.unregister(ROOT_APPLICATION_PATH);
        isJerseyServletRegistered = false;
    }

    // ---

    private void reloadJerseyServlet() {
        logger.fine("##### Reloading Jersey servlet");
        jerseyServlet.reload();
    }

    // ------------------------------------------------------------------------------------------------- Private Classes

    /**
     * Custom HttpContext to map resource name "/" to URL "/index.html"
     */
    private class BundleHTTPContext implements HttpContext {

        private Bundle bundle;
        private HttpContext httpContext;

        private BundleHTTPContext(Bundle bundle) {
            this.bundle = bundle;
            this.httpContext = httpService.createDefaultHttpContext();
        }

        // ---

        @Override
        public URL getResource(String name) {
            URL url;
            if (name.equals("web/")) {
                url = bundle.getResource("/web/index.html");
            } else {
                url = bundle.getResource(name);
            }
            // logger.info("### Mapping resource name \"" + name + "\" for plugin \"" +
            //     pluginName + "\"\n          => URL \"" + url + "\"");
            return url;
        }

        @Override
        public String getMimeType(String name) {
            return httpContext.getMimeType(name);
        }

        @Override
        public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response)
                                                                            throws java.io.IOException {
            return httpContext.handleSecurity(request, response);
        }
    }
}