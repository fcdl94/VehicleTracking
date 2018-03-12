package it.polito.dp2.vehicle.utils;
// This validator performs JAXB unmarshalling with validation
// against the schema
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Provider
@Consumes({"application/xml","text/xml"})
public class ModelValidationProvider implements MessageBodyReader<JAXBElement<?>> {
	final String jaxbPackage = "it.polito.dp2.vehicle.model";
	
	Logger logger;
	String responseBodyTemplate;
	InputStream schemaStream;
	JAXBContext jc ;
	Schema schema;
	SchemaFactory sf;

	public ModelValidationProvider() {
		logger = Logger.getLogger(ModelValidationProvider.class.getName());

		try {				
			schemaStream = ModelValidationProvider.class.getResourceAsStream("/xsd/vehicleTracking.xsd");
			if (schemaStream == null) {
				logger.log(Level.SEVERE, "xml schema file Not found.");
				throw new IOException();
			}
            //unmarshaller = jc.createUnmarshaller();
            sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            schema = sf.newSchema(new StreamSource(schemaStream));
            //unmarshaller.setSchema(schema);
            
			jc = JAXBContext.newInstance( jaxbPackage );
			InputStream templateStream = ModelValidationProvider.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			if (templateStream == null) {
				logger.log(Level.SEVERE, "html template file Not found.");
				throw new IOException();
			}
	        BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
			responseBodyTemplate = out.toString();

            logger.log(Level.INFO, "ModelValideationProvider initialized successfully");
		} catch (IOException | JAXBException | SAXException se) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", se);
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return JAXBElement.class.equals(type) || jaxbPackage.equals(type.getPackage().getName());
	}

	@Override
	public synchronized JAXBElement<?> readFrom(Class<JAXBElement<?>> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try {
			Unmarshaller unmarshaller;
			unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
			return (JAXBElement<?>) unmarshaller.unmarshal(entityStream);
			
		} catch (JAXBException ex) {
			logger.log(Level.WARNING, "Request body validation error.", ex);
			Throwable linked = ex.getLinkedException();
			String validationErrorMessage = "Request body validation error";
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			BadRequestException bre = new BadRequestException("Request body validation error");
			String responseBody = responseBodyTemplate.replaceFirst("___TO_BE_REPLACED___", validationErrorMessage);
			Response response = Response.fromResponse(bre.getResponse()).entity(responseBody).build();
			throw new BadRequestException("Request body validation error", response);
		}
	}

}
