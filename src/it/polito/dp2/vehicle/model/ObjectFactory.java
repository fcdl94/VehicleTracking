//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.11 at 11:16:04 AM CET 
//


package it.polito.dp2.vehicle.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.dp2.vehicle.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Vehicles_QNAME = new QName("http://www.vechicleTrackingSystem.org/model", "vehicles");
    private final static QName _Model_QNAME = new QName("http://www.vechicleTrackingSystem.org/model", "model");
    private final static QName _Graph_QNAME = new QName("http://www.vechicleTrackingSystem.org/model", "graph");
    private final static QName _Vehicle_QNAME = new QName("http://www.vechicleTrackingSystem.org/model", "vehicle");
    private final static QName _Path_QNAME = new QName("http://www.vechicleTrackingSystem.org/model", "path");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.dp2.vehicle.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Path }
     * 
     */
    public Path createPath() {
        return new Path();
    }

    /**
     * Create an instance of {@link Vehicles }
     * 
     */
    public Vehicles createVehicles() {
        return new Vehicles();
    }

    /**
     * Create an instance of {@link Model }
     * 
     */
    public Model createModel() {
        return new Model();
    }

    /**
     * Create an instance of {@link Graph }
     * 
     */
    public Graph createGraph() {
        return new Graph();
    }

    /**
     * Create an instance of {@link Vehicle }
     * 
     */
    public Vehicle createVehicle() {
        return new Vehicle();
    }

    /**
     * Create an instance of {@link Road }
     * 
     */
    public Road createRoad() {
        return new Road();
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link Connection }
     * 
     */
    public Connection createConnection() {
        return new Connection();
    }

    /**
     * Create an instance of {@link Self }
     * 
     */
    public Self createSelf() {
        return new Self();
    }

    /**
     * Create an instance of {@link PathNode }
     * 
     */
    public PathNode createPathNode() {
        return new PathNode();
    }

    /**
     * Create an instance of {@link NodeRef }
     * 
     */
    public NodeRef createNodeRef() {
        return new NodeRef();
    }

    /**
     * Create an instance of {@link ParkingArea }
     * 
     */
    public ParkingArea createParkingArea() {
        return new ParkingArea();
    }

    /**
     * Create an instance of {@link Car }
     * 
     */
    public Car createCar() {
        return new Car();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vehicles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vechicleTrackingSystem.org/model", name = "vehicles")
    public JAXBElement<Vehicles> createVehicles(Vehicles value) {
        return new JAXBElement<Vehicles>(_Vehicles_QNAME, Vehicles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Model }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vechicleTrackingSystem.org/model", name = "model")
    public JAXBElement<Model> createModel(Model value) {
        return new JAXBElement<Model>(_Model_QNAME, Model.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Graph }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vechicleTrackingSystem.org/model", name = "graph")
    public JAXBElement<Graph> createGraph(Graph value) {
        return new JAXBElement<Graph>(_Graph_QNAME, Graph.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vehicle }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vechicleTrackingSystem.org/model", name = "vehicle")
    public JAXBElement<Vehicle> createVehicle(Vehicle value) {
        return new JAXBElement<Vehicle>(_Vehicle_QNAME, Vehicle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Path }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vechicleTrackingSystem.org/model", name = "path")
    public JAXBElement<Path> createPath(Path value) {
        return new JAXBElement<Path>(_Path_QNAME, Path.class, null, value);
    }

}
