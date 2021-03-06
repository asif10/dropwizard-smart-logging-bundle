# Dropwizard Smart Logging Bundle

## Why

APIs are great. Microservices are great. But how do we keep track of a user who uses multiple APIs throughout a session? 
One approach is to add an additional value to each request i.e. user_id or session_id. This forces every API call to have 
an additional id attached to it (if available) and somewhere in the code we need to assign this to a log message.

Wouldn't it be easier to let an API client send identifiable information to the host via a header?

It would also be nice to be able to configure some extra fields to appear in every log message.

## How

Add the corresponding configuration to your config file (MyServiceConfiguration in this example) and implement interface

```java

    public class MyServiceConfiguration extends Configuration implements PrependLogConfiguration
    
    @JsonProperty
    private SmartLogging smartLogging;
```

Specifiy what header and any extra fields you want to log in your requests by adding this to your configuration:

```YAML
smartLogging:
  useHeader: X_UNIQUE_ID
  extraFields: {
    "environment": "TEST",
    "host": "localhost",
    "applicationName" : "my-cool-service"
  }
```
Add your new log format to your chosen appender

```YAML
    logFormat: "%-6level [%d{HH:mm:ss.SSS}] [%t] %logger{5} - %X{environment} %X{host} %X{applicationName} %X{X_UNIQUE_ID} %msg %n"
```

This bundle can be added to a dropwizard app using:

```java
    @Override
    public void initialize(Bootstrap<MyServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new PrependLogBundle());
    }
```


## Security

Be careful of what you log, make sure you validate headers to ensure your logs don't become a potential attack
vector.
