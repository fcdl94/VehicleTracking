import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import vehicle.tr.GraphType;

public class Main {
	
	 public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the vehicle.tr package
            JAXBContext jc = JAXBContext.newInstance( "vehicle.tr" );
            
            /*// create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = sf.newSchema(new File("xsd/vehicleTracking.xsd"));
                u.setSchema(schema);
                u.setEventHandler(
                    new ValidationEventHandler() {
                        // allow unmarshalling to continue even if there are errors
                        public boolean handleEvent(ValidationEvent ve) {
                            // ignore warnings
                            if (ve.getSeverity() != ValidationEvent.WARNING) {
                                ValidationEventLocator vel = ve.getLocator();
                                System.out.println("Line:Col[" + vel.getLineNumber() +
                                    ":" + vel.getColumnNumber() +
                                    "]:" + ve.getMessage());
                            }
                            return true;
                        }
                    }
                );
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Unable to validate due to following error.");
                se.printStackTrace();
            }
            
            // unmarshal a vehicle tracking instance document into a tree of Java
            // content objects composed of classes from the primer.po package
            JAXBElement<GraphType> graph = 
                (JAXBElement<GraphType>) u.unmarshal( new File( "xsd/model.xml" ) );
			*/
            
            GraphGenerator gg = new GraphGenerator(4, 10, 25);
            JAXBElement<GraphType> graph = gg.createRandomGraph();
            
            //then marshall it to the console to see if it was read right
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal(graph, new File("xml-gen.xml"));
            
        } catch( UnmarshalException ue ) {
            System.out.println( "Caught UnmarshalException" );
        } catch( JAXBException je ) {
        	System.out.println( "Caught JAXB exception" );
            //je.printStackTrace();
        } catch (ClassCastException cce) {
        	System.out.println( "Caught ClassCast exception" );
        	//cce.printStackTrace();
        }
        
    }
}
