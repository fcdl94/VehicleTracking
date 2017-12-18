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
	
	 private static int NUM_ROADS = 5;
	 private static int NUM_AREAS = 5;
	 private static int SEED = 0;
	 
	 public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the vehicle.tr package
            JAXBContext jc = JAXBContext.newInstance( "vehicle.tr" );
            int roads = NUM_ROADS, areas = NUM_AREAS, seed = SEED;
            File output;
        	if( args.length >= 1)	
            	output = new File(args[0]);
        	else {
        		//there is no arguments at all
        		output = new File("xml-gen.xml");
        	}
        	
            if( args.length >= 3) {
            	roads = Integer.parseInt(args[1]);
            	areas = Integer.parseInt(args[2]);
            }
            if( args.length == 4)
            	seed = Integer.parseInt(args[3]);
            
            GraphGenerator gg = new GraphGenerator(roads, areas, 25, seed);
            JAXBElement<GraphType> graph = gg.createRandomGraph();
            
            //then marshall it to the console to see if it was read right
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal(graph, output);
            
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
