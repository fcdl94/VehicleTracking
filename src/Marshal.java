
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;

import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.ObjectFactory;

public class Marshal {
	
	 private static int NUM_ROADS = 20;
	 private static int NUM_AREAS = 10;
	 private static int SEED = 127;
	 private static int VEHICLES = 0;
	 private static ObjectFactory of = new ObjectFactory();
	 
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
        		output = new File("xml/xml-gen.xml");
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
            
            m.marshal(of.createModel(model), output);
            
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
