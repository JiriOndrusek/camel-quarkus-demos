package de.hilling.camel;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@QuarkusTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleRouteBuilderTest extends CamelQuarkusTestSupport {

    @Produce("direct:ftp")
    protected ProducerTemplate template;

    @Inject
    protected CamelContext context;

    @EndpointInject("mock:file:sample_requests")
    protected MockEndpoint fileMock;

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new SampleRouteBuilder();
    }

//    @Override
//    protected void doBeforeEach(QuarkusTestMethodContext context) throws Exception {
//
//        startRouteDefinitions();
//        AdviceWith.adviceWith(this.context, "sampleRoute",
//                              SampleRouteBuilderTest::enhanceRoute);
//
//        startRouteDefinitions();
//    }
    @BeforeEach
    public void advising() throws Exception {

        AdviceWith.adviceWith(this.context, "sampleRoute",
                              SampleRouteBuilderTest::enhanceRoute);
    }

    @Test
    void testConsumeFtpWriteToFileOne() throws Exception {
        fileMock.message(0).body().isEqualTo("Hello World");
        template.sendBody("direct:ftp", "Hello World");
        fileMock.assertIsSatisfied();
    }

    @Test
    void testConsumeFtpWriteToFileTwo() throws Exception {
        fileMock.message(0).body().isEqualTo("Hello World");
        template.sendBody("direct:ftp", "Hello World");
        fileMock.assertIsSatisfied();
    }

    private static void enhanceRoute(AdviceWithRouteBuilder route) {
        route.replaceFromWith("direct:ftp");
        route.interceptSendToEndpoint("file:.*samples.*")
             .skipSendToOriginalEndpoint()
             .to("mock:file:sample_requests");
    }

}
