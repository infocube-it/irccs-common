
# Hapi DevService Quarkus Extension

This Quarkus extension provides a development-time service for HAPI FHIR, a popular open-source implementation of the FHIR standard for health care data exchange. The extension automatically starts a HAPI FHIR server in a Docker container when your Quarkus application is running in development mode, allowing you to interact with a FHIR server without needing to manually start one.

## Features

- **Automatic HAPI FHIR Server Startup**: The extension automatically starts a HAPI FHIR server in a Docker container when your Quarkus application is running in development mode.
- **Customizable Configuration**: You can customize the HAPI FHIR server version and configuration file path through Quarkus configuration properties.
- **Integrated with Quarkus DevServices**: The extension integrates with Quarkus's DevServices mechanism, providing a seamless development experience.

## Configuration

You can configure the HAPI FHIR server version and the path to the configuration file through Quarkus configuration properties.

- **`quarkus.hapi.devservices.version`**: Specifies the version of the HAPI FHIR server to use. Defaults to `6.10.1`.
- **`quarkus.hapi.devservices.config-path`**: Specifies the path to the configuration file for the HAPI FHIR server. Defaults to `app-config.yaml`(R5).

## Usage

To use the Hapi DevService Quarkus extension, add it to your project's dependencies. The extension will automatically start a HAPI FHIR server in a Docker container when your Quarkus application is running in development mode.

### Example Configuration

    # application.properties  
    quarkus.hapi.devservices.version=6.10.1
    quarkus.hapi.devservices.config-path=app-config.yaml

##  Accessing the HAPI FHIR Server

The extension automatically exposes the HAPI FHIR server's URL and port as Quarkus configuration properties. You can inject these properties into your application using the **`@ConfigProperty`** annotation.

    import  org.eclipse.microprofile.config.inject.ConfigProperty; 
    
    public  class  MyService  {  
    
	    @ConfigProperty(name =  "quarkus.hapi.devservices.url")  
	    String hapiUrl;  
	    @ConfigProperty(name =  "quarkus.hapi.devservices.port")  
	    Integer hapiPort;  
    
    // Use hapiUrl and hapiPort to interact with the HAPI FHIR server  
    }
