package org.moisiadis;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class Handler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent apiGatewayV2HTTPEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();

        String messageBody = apiGatewayV2HTTPEvent.getBody();
        logger.log(messageBody);
        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
            SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses("panosmoisiadis@pm.me"))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withText(new Content().withCharset("UTF-8")
                                            .withData(messageBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8")
                                    .withData("New Contact Form Submission")))
                    .withSource("noreply@panosmoisiadis.com");
            client.sendEmail(request);
            logger.log("Email successfully sent");
        } catch (Exception ex) {
            logger.log("Error sending email");
            logger.log(ex.toString());
            response.setStatusCode(500);
            return response;
        }
        response.setStatusCode(202);
        return response;
    }
}
