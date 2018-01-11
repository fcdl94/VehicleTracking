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

import it.polito.dp2.vehicle.model.Model;

public class Marshal {
	
	 private static int NUM_ROADS = 15;
	 private static int NUM_AREAS = 5;
	 private static int SEED = 127;
	 private static int VEHICLES = 10;
	 
	 public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the vehicle.tr package
            JAXBContext jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
            int roads = NUM_ROADS, areas = NUM_AREAS, seed = SEED,vehicles=VEHICLES;
            File output;
        	if( args.length >= 1)	
            	output = new File(args[0]);
        	else {
        		//there is no arguments at all
        		output = new File("xml-gen.xml");
        	}
        	
            if( args.length >= 4 ) {
            	roads = Integer.parseInt(args[1]);
            	areas = Integer.parseInt(args[2]);
            	vehicles = Integer.parseInt(args[3]);
            }
            if( args.length == 5)
            	seed = Integer.parseInt(args[4]);
            
            ModelGenerator gg = new ModelGenerator(roads, areas, 25, vehicles, seed);
            Model model = gg.createRandomModel();
            
            //then marshall it to the console to see if it was read right
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal(model, output);
            
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
