
package za.ac.nwu.wsdl.courseoffering;

import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 3.0.2
 * Generated source version: 3.0
 * 
 */
@WebServiceClient(name = "CourseOfferingService", targetNamespace = "http://nwu.ac.za/wsdl/CourseOffering", wsdlLocation = "/CourseOfferingService.wsdl")
public class CourseOfferingService_Service
    extends Service
{

    private final static URL COURSEOFFERINGSERVICE_WSDL_LOCATION;
    private final static WebServiceException COURSEOFFERINGSERVICE_EXCEPTION;
    private final static QName COURSEOFFERINGSERVICE_QNAME = new QName("http://nwu.ac.za/wsdl/CourseOffering", "CourseOfferingService");

    static {
        COURSEOFFERINGSERVICE_WSDL_LOCATION = za.ac.nwu.wsdl.courseoffering.CourseOfferingService_Service.class.getResource("/CourseOfferingService.wsdl");
        WebServiceException e = null;
        if (COURSEOFFERINGSERVICE_WSDL_LOCATION == null) {
            e = new WebServiceException("Cannot find '/CourseOfferingService.wsdl' wsdl. Place the resource correctly in the classpath.");
        }
        COURSEOFFERINGSERVICE_EXCEPTION = e;
    }

    public CourseOfferingService_Service() {
        super(__getWsdlLocation(), COURSEOFFERINGSERVICE_QNAME);
    }

    public CourseOfferingService_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), COURSEOFFERINGSERVICE_QNAME, features);
    }

    public CourseOfferingService_Service(URL wsdlLocation) {
        super(wsdlLocation, COURSEOFFERINGSERVICE_QNAME);
    }

    public CourseOfferingService_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, COURSEOFFERINGSERVICE_QNAME, features);
    }

    public CourseOfferingService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CourseOfferingService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CourseOfferingService
     */
    @WebEndpoint(name = "CourseOfferingServicePort")
    public CourseOfferingService getCourseOfferingServicePort() {
        return super.getPort(new QName("http://nwu.ac.za/wsdl/CourseOffering", "CourseOfferingServicePort"), CourseOfferingService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CourseOfferingService
     */
    @WebEndpoint(name = "CourseOfferingServicePort")
    public CourseOfferingService getCourseOfferingServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://nwu.ac.za/wsdl/CourseOffering", "CourseOfferingServicePort"), CourseOfferingService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (COURSEOFFERINGSERVICE_EXCEPTION!= null) {
            throw COURSEOFFERINGSERVICE_EXCEPTION;
        }
        return COURSEOFFERINGSERVICE_WSDL_LOCATION;
    }

}
